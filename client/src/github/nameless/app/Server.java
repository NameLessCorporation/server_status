package github.nameless.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

public class Server extends Thread {

	private MainWindow frame;
	private int port = 62226;
	private boolean isConnected = false;
	ServerSocket server = null;

	public static String myPublicIp() throws IOException {
		URL url = new URL("https://api.ipify.org");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String ip = bufferedReader.readLine();
		return ip;
	}

	@Override
    public void run() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
			Notifications.showErrorNotification("Error", e.getMessage());
        }
		frame.logArea.append("Listening for connection on port " + port + "\n");
        while (true) {
			try (Socket socket = Objects.requireNonNull(server).accept()) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line = reader.readLine().replace("GET /?", "")
						      .replace(" HTTP/1.1", "");
				if (!line.equals("GET /favicon.ico")) {
					if (!isConnected) {
						frame.logArea.append("Data received\n");
						frame.statusLabel.setText("Status: connected");
						isConnected = true;
					}
					HashMap<String, String> data = getData(line);
//					for (String key : data.keySet()) System.out.println(key + ":" + data.get(key));
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
					frame.logArea.append(data.get("data"));
					break;
				}
				case "disconnected": {
					frame.statusLabel.setText("Status: disconnected");
					frame.logArea.append("Disconnected\n");
					break;
				}
				case "command": {
					execute(data);
					break;
				}
			}
		}
	}

	public void execute(HashMap<String, String> data) {
		String command = data.get("command");
		String arg = data.get("args");
		if (command.equals("showNotification")) Notifications.showInfoNotification("Information from server", arg);
	}

	public void setFrame(MainWindow frame) {
		this.frame = frame;
	}

	public HashMap<String, String> getData(String request) {
		String[] dataArray = request.split("&");
		HashMap<String, String> response = new HashMap<>();
		for (int i = 0; i < dataArray.length; i++) {
			String[] data = dataArray[i].split("=");
			response.put(data[0], data[1]);
		}
		return response;
	}

}
