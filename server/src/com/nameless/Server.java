package com.nameless;

import com.nameless.app.MainWindow;
import com.nameless.elements.Notifications;

import java.io.*;
import java.net.*;
import java.util.HashMap;


public class Server extends Thread {
	private HashMap<String, String> respons = new HashMap<String, String>();
	private Boolean shutdown = false;
	private static String password = null;

	public static HashMap<String, String> users = new HashMap<String, String>();

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
		try {server = new ServerSocket(52225);} catch (IOException e) {e.printStackTrace();}
		System.out.println("Listening for connection on port 52225 ....");
		while (!shutdown) {
			try (Socket socket = server.accept()) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line = reader.readLine().replace("GET /?", "")
						.replace(" HTTP/1.1", "");
				if (!line.equals("GET /favicon.ico")) {
					parser(line, server, password, socket);
					Runnable task = () -> {
						try {
							while (!shutdown) {
								MainWindow.getUsers();
								sendInfo(socket, password);
							}
						} catch (IOException e) {e.printStackTrace();}
					};
					Thread thread = new Thread(task);
					thread.start();
					System.out.println(line);
				}
				String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + "Server started";
				socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
			} catch (IOException e) {e.printStackTrace();}
		}
	}

	private void parser(String request, ServerSocket server, String password, Socket socket) throws IOException {
		String[] dataArray = request.split("&");
		String[] data;
		for (String str: dataArray) {
			data = str.split("=");
			respons.put(data[0], data[1]);
		}
		for (String k: respons.keySet()) { // I should delete this code
			System.out.println(respons.get(k));
		}
		commands(password, server, socket);
	}

	private void sendInfo(Socket socket, String password) throws IOException {
		try {
			String pass = respons.get("pass");
			if (pass.equals(password)) {
				Status status = new Status();
				String info = status.statusServer();
				for (String i : users.values()) {
					String url = "http://" + i + ":62226?type=RAM&data=" + info;
					URL mes = new URL(url);
					InputStream is = mes.openStream();
				}
			}
		} catch (Exception e) {
			disconnectUser();
		}
	}

	private void commands(String password, ServerSocket server, Socket socket) throws IOException {
		String pass = respons.get("pass");
		String user = respons.get("user");
		String type = respons.get("type");
		if (type.equals("connect") && pass.equals(password) && !users.containsKey(user)) {
			String ip = socket.getRemoteSocketAddress().toString()
					.split(":")[0].replace("/", "");
			if (!users.containsValue(ip)) {
				users.put(user, ip);
			}
		} else if (type.equals("stopServer") && users.containsKey(user)) {stopServer(server);
		} else if (type.equals("disconnect")) {disconnectUser();}
	}

	private void disconnectUser() throws IOException {
		String user = respons.get("user");
		for (String i : users.keySet()) {
			if (i.equals(user)) {
				String url = "http://" + users.get(i) + ":62226?type=disconnected";
				URL mes = new URL(url);
				InputStream is = mes.openStream();
				users.remove(user);
				Notifications n = new Notifications();
				n.showInfoNotification("User disconnect", user + " disconnect from server");
			}
		}
	}

	private void stopServer(ServerSocket server) throws IOException {
		server.close();
		shutdown = true;
		String user = respons.get("user");
		Notifications n = new Notifications();
		MainWindow.s.setText("Status: server was stopped");
		n.showInfoNotification("Server was stopped", user + " stopped server");
	}

}