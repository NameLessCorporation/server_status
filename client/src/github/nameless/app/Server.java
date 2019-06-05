package github.nameless.app;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server extends Thread {

	int port;
	MainWindow frame;

	public Server() {
		this.port = port;
	}

	@Override
    public void run() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(9090);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Listening for connection on port 8080 ....");
        while (true) {
            try (Socket socket = server.accept()){
				System.out.println("adsdasd");
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine().replace("GET /?", "")
                                                        .replace(" HTTP/1.1", "");
				if (!line.equals("GET /favicon.ico")){
					getData(line);
					//System.out.println(pc);
				}
                String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + "Server started";
                socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
				System.out.println(e.toString());
			}
        }
    }

	public void setFrame(MainWindow frame) {
		this.frame = frame;
	}

	public static void getData(String request) {
		String[] dataArray = request.split("&");
		HashMap<String, String> response = new HashMap<>();
		for (int i = 0; i < dataArray.length; i++) {
			String[] data = dataArray[i].split("=");
			response.put(data[0], data[1]);
		}
	}

	public static void main(String[] args) {
		//new Server();
	}



}
