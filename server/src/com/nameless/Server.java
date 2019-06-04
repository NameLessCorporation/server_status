package com.nameless;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class Server {
	private static HashMap<String, String> respons = new HashMap<String, String>();
	private static ArrayList<String> users = new ArrayList();

	public Server(String port, String password) throws IOException {
		startServer(port, password);
	}

	public void startServer(String port, String password) throws IOException {
		InetAddress addr = InetAddress.getByName("::1");
		System.out.println(addr.toString());
		ServerSocket server = new ServerSocket(Integer.parseInt(port), 50, addr);
			try {
				server = new ServerSocket(Integer.parseInt(port));
		} catch (IOException e) {
			e.printStackTrace();
		}
			System.out.println("Listening for connection on port 8080 ....");
			while (true) {
				try (Socket socket = server.accept()){
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String line = reader.readLine().replace("GET /?", "")
							.replace(" HTTP/1.1", "");
					if (!line.equals("GET /favicon.ico")){
						parser(line);
						checkPassword(password);
						System.out.println(line);
					}
					String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + "Server started";
					socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static void parser(String request) {
		String[] dataArray = request.split("&");
		String[] data;
		for (String str: dataArray) {
			data = str.split("=");
			respons.put(data[0], data[1]);
		}
		for (String k: respons.keySet()) {
			System.out.println(respons.get(k));
		}
	}

	public static void checkPassword(String password) {
		String pass = respons.get("pass");
		String user = respons.get("user");
		String type = respons.get("type");
		if (type.equals("connect") && pass.equals(password)) {
			users.add(user);
		}
	}
}
