package github.nameless.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import github.nameless.elements.Button;
import github.nameless.elements.Label;

public class MainWindow implements Window{
	static JFrame frame;
	private static JPanel panel;
	Label cpuInfoLabel;
	Label ramInfoLabel;
	Label netInfoLabel;
	Label statusLabel;
	TextArea logArea;
	private String host, user;
	private static int port = 52225;

	Server server;

	private HashMap<String, String> disconnectRequest = new HashMap<>();
	private HashMap<String, String> stopRequest = new HashMap<>();

	public MainWindow(String name, Integer width, Integer height) {
		init(name, width, height);
	}

	public void setHost(String host) {
		this.host = host;
		Label ipLabel = new Label(10, 10, "Server address: " + host);
		panel.add(ipLabel);
	}

	public void setUser(String user) {
		this.user = user;
		Label userLabel = new Label(10, 35, "Your username as: " + user);
		panel.add(userLabel);
	}

	@Override
	public void setLabel() {
		cpuInfoLabel = new Label(250, 10, "CPU: ");
		ramInfoLabel = new Label(250, 35, "RAM:");
		netInfoLabel = new Label(250, 60, "Internet:");
		statusLabel = new Label(250, 85, "Status: not connected");

		panel.add(cpuInfoLabel);
		panel.add(ramInfoLabel);
		panel.add(netInfoLabel);
		panel.add(statusLabel);
	}

	@Override
	public void setButton() {
		Button stopButton = new Button(8, 60, 125, 30, "Stop server");
		Button disconnectButton = new Button(8, 90, 125, 30, "Disconnect");

		stopButton.addActionListener(e -> sendRequest(stopRequest, host));
		disconnectButton.addActionListener(e -> {
			logArea.append("Trying to disconnect\n");
			sendRequest(disconnectRequest, host);
		});

		panel.add(stopButton);
		panel.add(disconnectButton);
	}

	@Override
	public void setPanel() {
		panel = new JPanel();
		panel.setLayout(null);
	}

	@Override
	public void setField() {}

	private static void sendRequest(HashMap<String, String> pc, String url) {
		try {
			url = "http://" + url + ":" + port + "?";
			for (String key : pc.keySet()) {
				url += key + "=" + pc.get(key) + "&";
			}
			URL server = new URL(url);
			InputStream is = server.openStream();
		} catch (IOException e) {
			Notifications.showErrorNotification("Error", e.toString());
		}
	}

	private void setLogArea() {
		logArea = new TextArea("", 10, 40);
		logArea.setBounds(250, 115, 630, 350);
		logArea.setFont(new Font("Arial", Font.PLAIN, 15));
		logArea.setEditable(false);
		panel.add(logArea);
	}

	void initPackages() {
		disconnectRequest.put("type", "disconnect");
		disconnectRequest.put("user", user);

		stopRequest.put("type", "stopServer");
		stopRequest.put("user", user);
	}

	public void setServer(Server server) {
		this.server = server;
	}

	private void init(String name, Integer width, Integer height) {
		frame = new JFrame(name);
		setDecoration();
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setPanel();
		setLabel();
		setField();
		setButton();
		setLogArea();
		initPackages();

		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}

}
