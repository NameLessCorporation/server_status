package github.nameless.app;

import javax.swing.DefaultListModel;
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

public class Server extends Thread {

	private MainWindow frame;
	private final int CLIENT_PORT = 62226;
	String host;
	private boolean isDataReceived = false;
	ServerSocket server = null;
	HashMap<String, String> connectRequest;

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
		}
	}

	@Override
    public void run() {
        try {
            server = new ServerSocket(CLIENT_PORT);
        } catch (IOException e) {
			Notifications.showErrorNotification("Error", e.getMessage());
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
					break;
				}
				case "serverStopped": {
					frame.statusLabel.setText("Status: server was stopped");
					frame.log("Server was stopped");
					frame.ipList.setModel(new DefaultListModel<>());
					frame.usersList.setModel(new DefaultListModel<>());
					frame.disconnectButton.setEnabled(false);
					frame.stopButton.setEnabled(false);
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
