package github.nameless.app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class Server extends Thread {

	private MainWindow frame;
	private final int CLIENT_PORT = 62226;
	String host;
	private boolean isDataReceived = false;
	boolean disconnected = false;
	ServerSocket server = null;
	HashMap<String, String> connectRequest;
	BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
	int imageWidth, imageHeight;
	ScreenWindow screenWindow;

	public void setConnectRequest(HashMap<String, String> connectRequest) {
		this.connectRequest = connectRequest;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String myPublicIp() throws IOException {
		URL url = new URL("https://api.ipify.org");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String ip = bufferedReader.readLine();
		return ip;
	}

	private void sendRequest(HashMap<String, String> pc, String url) {
		try {
			url = "http://" + url + ":52225?";
			for (String key : pc.keySet()) {
				url += key + "=" + pc.get(key) + "&";
			}
			URL server = new URL(url);
			frame.log("Trying to connect");
			InputStream is = server.openStream();
		} catch (IOException e) {
			frame.log(e.toString());
			frame.logToFile(e.toString());
		}
	}

	@Override
    public void run() {
        try {
            server = new ServerSocket(CLIENT_PORT);
        } catch (IOException e) {
			Notifications.showErrorNotification("Error", e.getMessage());
			frame.logToFile(e.toString());
        }
		frame.log("Listening for connection on port " + CLIENT_PORT);
        while (true) {
			try (Socket socket = Objects.requireNonNull(server).accept()) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line = reader.readLine().replace("GET /?", "")
						      .replace(" HTTP/1.1", "");
				if (!line.equals("GET /favicon.ico")) {
					if (!isDataReceived) {
						frame.log("Data received");
						frame.statusLabel.setText("Status: connected");
						isDataReceived = true;
					}
					HashMap<String, String> data = getData(line);
					checkResponse(data);
				}
				String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + "Server started";
				socket.getOutputStream().write(httpResponse.getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				frame.logToFile(e.toString());
				Notifications.showErrorNotification("Error", e.toString());
			}
		}
    }

    public void checkResponse(HashMap<String, String> data) {
		String type = data.get("type");
		if (!type.isEmpty()) {
			switch (type) {
				case "CPU": {
					frame.cpuInfoLabel.setText("CPU: " + data.get("data"));
					break;
				}
				case "RAM": {
					frame.ramInfoLabel.setText("RAM: " + data.get("data"));
					break;
				}
				case "inet": {
					frame.netInfoLabel.setText("Internet: " + data.get("data"));
					break;
				}
				case "print": {
					frame.log(data.get("data"));
					break;
				}
				case "disconnected": {
					frame.statusLabel.setText("Status: disconnected");
					frame.log("Disconnected");
					frame.ipList.setModel(new DefaultListModel<>());
					frame.usersList.setModel(new DefaultListModel<>());
					frame.disconnectButton.setEnabled(false);
					frame.stopButton.setEnabled(false);
					disconnected = true;
					break;
				}
				case "serverStopped": {
					frame.statusLabel.setText("Status: server was stopped");
					frame.log("Server was stopped");
					frame.ipList.setModel(new DefaultListModel<>());
					frame.usersList.setModel(new DefaultListModel<>());
					frame.disconnectButton.setEnabled(false);
					frame.stopButton.setEnabled(false);
					disconnected = true;
					break;
				}
				case "users": {
					data.remove("type");
					DefaultListModel usersModel = new DefaultListModel<>();
					DefaultListModel ipModel = new DefaultListModel<>();
					int i = 0;
					for (String key : data.keySet()) {
						usersModel.add(i, key);
						ipModel.add(i, data.get(key));
						i++;
					}
					frame.usersList.setModel(usersModel);
					frame.ipList.setModel(ipModel);
					break;
				}
				case "shellResult": {
					String result = data.get("data");
					if (!result.isEmpty()) {
						result = result.replace("()n", "\n");
						frame.shellArea.append(result + "\n");
					}
					break;
				}
				case "image": {
					String[] red = data.get("r").split(",");
					String[] green = data.get("g").split(",");
					String[] blue = data.get("b").split(",");

//					int y = Integer.parseInt(data.get("y"));
					int count = 0;

					for (int i = 0; i < imageWidth; i++) {
						for (int j = 0; j < imageHeight; j++) {
							int r = Integer.parseInt(red[count]);
							int g = Integer.parseInt(green[count]);
							int b = Integer.parseInt(blue[count]);
							int p = (255<<24) | (r<<16) | (g<<8) | b;
							image.setRGB(i, j, p);
							count++;
						}
					}
					ImageIcon icon = new ImageIcon(image);
					screenWindow.imageLabel.setIcon(icon);

//					int x = Integer.parseInt(data.get("x"));
//					int y = Integer.parseInt(data.get("y"));
//					int r = Integer.parseInt(data.get("r"));
//					int g = Integer.parseInt(data.get("g"));
//					int b = Integer.parseInt(data.get("b"));
//
//					int p = (255<<24) | (r<<16) | (g<<8) | b;
//					image.setRGB(x, y, p);
//					ImageIcon icon = new ImageIcon(image);
//					frame.imageLabel.setIcon(icon);
					break;
				}
				case "imageData": {
					imageWidth = Integer.parseInt(data.get("width"));
					imageHeight = Integer.parseInt(data.get("height"));
					image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
					screenWindow = new ScreenWindow(imageWidth, imageHeight);
					screenWindow.imageLabel.setBounds(0, 0, imageWidth, imageHeight);
					frame.log("Screenshot was received");
					break;
				}
				case "command": {
					execute(data);
					break;
				}
			}
		}
	}

	private void execute(HashMap<String, String> data) {
		String command = data.get("command");
		String arg = data.get("args");
		if (command.equals("showNotification")) Notifications.showInfoNotification("Information from server", arg);
	}

	void setFrame(MainWindow frame) {
		this.frame = frame;
	}

	private HashMap<String, String> getData(String request) {
		String[] dataArray = request.split("&");
		HashMap<String, String> response = new HashMap<>();
		for (int i = 0; i < dataArray.length; i++) {
			String[] data = dataArray[i].split("=");
			try {
				response.put(data[0], data[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				response.put(data[0], "");
			}
		}
		return response;
	}

}
