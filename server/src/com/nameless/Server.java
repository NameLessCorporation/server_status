package com.nameless;

import com.nameless.app.MainWindow;
import com.nameless.elements.Notifications;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Server extends Thread {
	private MainWindow mw;
	private String password = "";
	private Boolean shutdown = false;
	private HashMap<String, String> respons = new HashMap<String, String>();

	public HashMap<String, String> users = new HashMap<String, String>();

	public void setMw(MainWindow mw) {
		this.mw = mw;
	}

	public void setPassword(String password) {
		this.password = password;
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
								mw.getUsers();
								sendInfo(socket, password);
								sendUsersInfo();
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
		} catch (Exception e) {disconnectUser();}
	}

	private void commands(String password, ServerSocket server, Socket socket) throws IOException {
		String pass = respons.get("pass");
		String user = respons.get("user");
		String type = respons.get("type");
		String data = respons.get("data");
		if (type.equals("connect") && pass.equals(password) && !users.containsKey(user)) {
			String ip = socket.getRemoteSocketAddress().toString()
					.split(":")[0].replace("/", "");
			if (!users.containsValue(ip)) {
				users.put(user, ip);
				setLogs(user + " connected");
			}
		} else if (type.equals("stopServer") && users.containsKey(user)) {stopServer(false); server.close();
		} else if (type.equals("disconnect")) {disconnectUser();
		} else if (type.equals("shell")) {shell(data, user);
		} else {setLogs(user + " tried to sent request");}
	}

	private void shell(String data, String user) {
		ProcessBuilder processBuilder = new ProcessBuilder();

		if (OsUtils.isWindows()) {
			processBuilder.command("cmd.exe", "/c", data);
		} else {
			processBuilder.command("bash", "-c", data);
		}
		try {
			Process process = processBuilder.start();
			StringBuilder output = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "()n");
			}
			int exitVal = process.waitFor();
			String url = "";
			if (exitVal == 0) {
				System.out.println(output);
				url = "http://" + users.get(user) + ":62226?type=shellResult&data=" + output;
			} else {
				System.out.println("error");
				url = "http://" + users.get(user) + ":62226?type=shellResult&data=error";
			}
			URL mes = new URL(url);
			InputStream is = mes.openStream();
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	private void disconnectUser() throws IOException {
		String user = respons.get("user");
		for (String i : users.keySet()) {
			if (i.equals(user)) {
				String url = "http://" + users.get(i) + ":62226?type=disconnected";
				setLogs(user + " was disconnected");
				URL mes = new URL(url);
				InputStream is = mes.openStream();
				users.remove(user);
				Notifications n = new Notifications();
				n.showInfoNotification("User disconnect", user + " disconnect from server");
			}
		}
	}

	private void clearList() {
		try {
			mw.usersModel.clear();
			mw.ipModel.clear();
			mw.usersList.setModel(mw.usersModel);
			mw.ipList.setModel(mw.ipModel);
		} catch (Exception e) {
			System.out.println("List users is clear");}

	}

	private void sendUsersInfo() throws IOException {
		try {
			for (String i: users.keySet()) {
				String url = "http://" + users.get(i) + ":62226?type=users&" + i + "=" + users.get(i);
				URL mes = new URL(url);
				InputStream is = mes.openStream();
			}
		} catch (Exception e) {disconnectUser();}
	}

	private void setLogs(String logs) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String nowDate = formatter.format(date);
		mw.logsArea.append(nowDate + "  |  "  + logs + "\n");
	}

	public void stopServer(Boolean server) throws IOException {
		shutdown = true;
		String user = respons.get("user");
		Notifications n = new Notifications();
		mw.s.setText("Status: server was stopped");
		if (server) {
			setLogs("Server was stopped");
			n.showInfoNotification("Server was stopped", "server was stopped");
		} else {
			setLogs(user + " stopped server");
			n.showInfoNotification("Server was stopped", user + " stopped server");
		}
		for (String i: users.keySet()) {
			String url = "http://" + users.get(i) + ":62226?type=serverStopped";
			URL mes = new URL(url);
			InputStream is = mes.openStream();
		}
		clearList();
	}

}