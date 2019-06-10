package github.nameless.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;

public class Server extends Thread {

	private MainWindow frame;

	@Override
    public void run() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(9090);
        } catch (IOException e) {
			Notifications.showErrorNotification("Error", e.getMessage());
        }
        System.out.println("Listening for connection on port 9090 ....");
        while (true) {
            try (Socket socket = Objects.requireNonNull(server).accept()){
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine().replace("GET /?", "")
                                                        .replace(" HTTP/1.1", "");
				if (!line.equals("GET /favicon.ico")){
					HashMap<String, String> data = getData(line);

					for (String key : data.keySet()) {
						System.out.println(key + ":" + data.get(key));
					}
					checkResponse(data);
				}
                String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + "Server started";
                socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
            } catch (Exception e) {
                Notifications.showErrorNotification("Error", e.toString());
            }
		}
    }

    public void checkResponse(HashMap<String, String> data) {
		String type = data.get("type");
		if (!type.isEmpty()) {
			switch (type) {
				case "cpu": {
					frame.cpuInfoLabel.setText("CPU: " + data.get("data"));
					break;
				}
				case "ram": {
					frame.ramInfoLabel.setText("RAM: " + data.get("data"));
					break;
				}
				case "net": {
					frame.netInfoLabel.setText("Internet: " + data.get("data"));
					break;
				}
				case "command": {
					execute(data);
				}
			}
		}
	}

	public void execute(HashMap<String, String> data) {
		String command = data.get("command");
		String arg = data.get("args");
		if (command.equals("showNotification")) {
			Notifications.showInfoNotification("Information from server", arg);
		}
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
