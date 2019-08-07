package com.nameless;

import com.nameless.app.MainWindow;
import com.nameless.elements.Notifications;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class Server extends Thread {
	private MainWindow mw;
	private String password = "";
	private Boolean shutdown = false;
	private HashMap<String, String> response = new HashMap<String, String>();

	public String userBan = "";
	public JList usersList;
	public DefaultListModel<String> usersBanModel = new DefaultListModel<>();
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
						while (!shutdown) {
							mw.getUsers();
							sendInfo(socket, password);
							sendUsersInfo();
						}
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
		try {
			String[] dataArray = request.split("&");
			String[] data;
			for (String str: dataArray) {
				data = str.split("=");
				response.put(data[0], data[1]);
			}
			for (String k: response.keySet()) { // I should delete this code
				System.out.println(response.get(k));
			}
			commands(password, server, socket);
		} catch (ArrayIndexOutOfBoundsException e) {}

	}

	private void sendInfo(Socket socket, String password) {
		try {
			String pass = response.get("pass");
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

	public void loadListBan() throws IOException {
		try {
			usersBanModel.clear();
			usersList.setModel(usersBanModel);
			File file = new File("ban_list.txt");
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				usersBanModel.addElement(sc.nextLine());
			}
		} catch (FileNotFoundException e) {
			FileWriter writer = new FileWriter("ban_list.txt");
			writer.write("");
			writer.flush();
			writer.close();
		}
	}

	private void commands(String password, ServerSocket server, Socket socket) throws IOException {
		String pass = response.get("pass");
		String user = response.get("user");
		String type = response.get("type");
		String data = response.get("data");
		String ip = socket.getRemoteSocketAddress().toString().split(":")[0].replace("/", "");
		if (type.equals("connect") && pass.equals(password) && !users.containsKey(user)) {
			if (!usersBanModel.contains(user) && !usersBanModel.contains(ip)) {
				if (!users.containsValue(ip)) {
					users.put(user, ip);
					setLogs(user + " connected");
				}
			}
		} else if (type.equals("stopServer") && users.containsKey(user)) {stopServer(false); server.close();
		} else if (type.equals("disconnect")) {disconnectUser();
		} else if (type.equals("shell") && pass.equals(password) && users.containsKey(user)) {
			shell(data, user);
			setLogs(user + " executed a command: " + data);
		} else if (type.equals("screen") && users.containsKey(user)) { sendScreen(ip);
		} else {setLogs(user + " tried to sent request");}
	}

	private void sendScreen(String ip) {
		try {
			BufferedImage bufimage = new
					Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			String url = "http://" + ip + ":62226/?type=imageData&width=" +
					bufimage.getWidth() + "&height=" + bufimage.getHeight();
			URL serv = new URL(url);
			InputStream iss = serv.openStream();
			URL server = new URL(genStr(bufimage.getWidth(), bufimage.getHeight(), bufimage, ip));
			InputStream is = server.openStream();
		} catch (IOException | AWTException ex) {
			ex.printStackTrace();
		}
	}

	private int[] getColor(BufferedImage image, int x, int y) {
		int clr =  image.getRGB(x, y);
		int red = (clr & 0x00ff0000) >> 16;
		int green = (clr & 0x0000ff00) >> 8;
		int blue = clr & 0x000000ff;
		return new int[]{red, green, blue};
	}

	private String genStr(int width, int height, BufferedImage image, String ip) {
		StringBuilder url = new StringBuilder("http://" + ip + ":62226?type=image&");
		StringBuilder red = new StringBuilder("r=");
		StringBuilder green = new StringBuilder("g=");
		StringBuilder blue = new StringBuilder("b=");
		for (int j = 0; j < width; j++) {
			for (int i = 0; i < height; i++) {
				int[] color = getColor(image, j, i);
				red.append(color[0] + ",");
				green.append(color[1] + ",");
				blue.append(color[2] + ",");
			}
		}
		url.append(red.toString() + "&" + green.toString() + "&" + blue.toString());
		return url.toString();
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
			Thread.sleep(1000);
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

	private void disconnectUser() {
		try {
			String user = response.get("user");
			for (String i : users.keySet()) {
				if (i.equals(user)) {
					String url = "http://" + users.get(i) + ":62226?type=disconnected";
					setLogs(user + " was disconnected");
					URL mes = new URL(url);
					InputStream is = mes.openStream();
					users.remove(user);
					Notifications n = new Notifications();
					n.showInfoNotification("User disconnect", user + " disconnect from server");
					mw.ipLabel.setText("IP: ");
					mw.usersLabel.setText("Users:");
				}
			}
		} catch (Exception e) {}
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

	private void sendUsersInfo() {
		try {
			for (String i: users.keySet()) {
				String url = "http://" + users.get(i) + ":62226?type=users&";
				for (String user : users.keySet()) {
					url += user + "=" + users.get(user) + "&";
				}
				URL mes = new URL(url);
				InputStream is = mes.openStream();
			}
		} catch (Exception e) {disconnectUser();}
	}

	public void setLogs(String logs) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String nowDate = formatter.format(date);
		String log = nowDate + "  |  "  + logs + "\n";
		mw.logsArea.append(log);
		toFile(log, "logs.txt");
	}

	public void toFile(String log, String fileName) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(log);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void banUsers() {
		mw.ipList.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if (users.size() > 0) {
					userBan = (String) mw.ipList.getSelectedValue();
					mw.usersLabel.setText("Users: ");
					mw.ipLabel.setText("IP: " + userBan);
				}
			}
		});
		mw.usersList.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if (users.size() > 0) {
					userBan = (String) mw.usersList.getSelectedValue();
					mw.ipLabel.setText("IP: ");
					mw.usersLabel.setText("User: " + userBan);
				}
			}
		});
	}

	public void stopServer(Boolean server) {
		try {
			shutdown = true;
			String user = response.get("user");
			Notifications n = new Notifications();
			mw.s.setText("Status: server was stopped");
			if (server) {
				setLogs("Server was stopped");
				n.showInfoNotification("Server was stopped", "server was stopped");
			} else {
				setLogs(user + " stopped server");
				n.showInfoNotification("Server was stopped", user + " stopped server");
			}
			for (String i : users.keySet()) {
				String url = "http://" + users.get(i) + ":62226?type=serverStopped";
				URL mes = new URL(url);
				InputStream is = mes.openStream();
			}
			mw.stop.setEnabled(false);
			mw.disconnect.setEnabled(false);
			clearList();
		} catch (Exception e) {}
	}

}