package com.nameless;

import com.nameless.elements.Notifications;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Server extends Thread {
	private HashMap<String, String> respons = new HashMap<String, String>();
	private ArrayList<String> users = new ArrayList();
	private Boolean shutdown = false;
	private static String password = null;
	private ArrayList<String> ipUsers = new ArrayList();

	public Server() throws IOException {
		startServer();
	}

	public static void setPassword(String password) {
		Server.password = password;
	}

	public void startServer() throws IOException {
		InetAddress addr = InetAddress.getByName("::1");
		System.out.println(addr.toString());
		ServerSocket server = new ServerSocket(52225, 50, addr);
			try {
				server = new ServerSocket(52225);
		} catch (IOException e) {
			e.printStackTrace();
		}
			System.out.println("Listening for connection on port 52225 ....");
			while (!shutdown) {
				try (Socket socket = server.accept()){
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String line = reader.readLine().replace("GET /?", "")
							.replace(" HTTP/1.1", "");
					if (!line.equals("GET /favicon.ico")){
						parser(line, server, password);
						Runnable task = () -> {
							try {
								while (!shutdown) {
									sendInfo(socket, password);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						};
						Thread thread = new Thread(task);
						thread.start();
						System.out.println(line);
					}
					String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + "Server started";
					socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}

	private void parser(String request, ServerSocket server, String password) throws IOException {
		String[] dataArray = request.split("&");
		String[] data;
		for (String str: dataArray) {
			data = str.split("=");
			respons.put(data[0], data[1]);
		}
		for (String k: respons.keySet()) {
			System.out.println(respons.get(k));
		}
		commands(password, server);
	}

	private void sendInfo(Socket socket, String password) throws IOException {
		String pass = respons.get("pass");
		if (pass.equals(password)) {
			String ip = socket.getRemoteSocketAddress().toString()
					.split(":")[0].replace("/", "");
			ipUsers.add(ip);
			Status status = new Status();
			String info = status.statusServer();
			for (String i : ipUsers) {
				String url = "http://" + i + ":62226?type=RAM&data=" + info;
				URL mes = new URL(url);
				InputStream is = mes.openStream();
			}
		}
	}

	private void commands(String password, ServerSocket server) throws IOException {
		String pass = respons.get("pass");
		String user = respons.get("user");
		String type = respons.get("type");
		if (type.equals("connect") && pass.equals(password)) {
			users.add(user);
			System.out.println(users);
		} else if (type.equals("stopServer") && users.contains(user)) {
			stopServer(server);
		}
	}

	private void stopServer(ServerSocket server) throws IOException {
		String stop = respons.get("type");
		String user = respons.get("user");
		if (stop.equals("stopServer")) {
			server.close();
			shutdown = true;
			Notifications n = new Notifications();
			n.showInfoNotification("Server Stop", user + " stopped server");
		}
		System.out.println(stop);
	}
}
