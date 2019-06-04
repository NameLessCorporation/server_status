package github.nameless.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    @Override
    public void run() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(8080);
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

	public static void getData(String request) {
		String[] dataArray = request.split("&");
		System.out.printf(request);
	}

	public static void main(String[] args) {
		new Server();
	}

}
