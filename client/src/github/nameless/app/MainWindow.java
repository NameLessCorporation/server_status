package github.nameless.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import github.nameless.elements.Button;
import github.nameless.elements.Label;

public class MainWindow implements Window{
	private static JFrame frame;
	private static JPanel panel;
	private Label cpuInfoLabel;
	private Label ramInfoLabel;
	private Label netInfoLabel;
	String host, port, user;

	private HashMap<String, String> disconnectRequest = new HashMap<>();
	private HashMap<String, String> stopRequest = new HashMap<>();

	public MainWindow(String name, Integer width, Integer height, String host, String port, String user) {
		this.host = host;
		this.port = port;
		this.user = user;
		init(name, width, height);
	}

	@Override
	public void setLabel() {
		Label ipLabel = new Label(10, 10, "Server address: " + host);
		Label userLabel = new Label(10, 35, "You loggined as: " + user);

		cpuInfoLabel = new Label(250, 10, "CPU: ");
		ramInfoLabel = new Label(250, 35, "RAM:");
		netInfoLabel = new Label(250, 60, "Internet:");

		panel.add(ipLabel);
		panel.add(userLabel);
		panel.add(cpuInfoLabel);
		panel.add(ramInfoLabel);
		panel.add(netInfoLabel);
	}

	@Override
	public void setButton() {
		Button stopButton = new Button(8, 60, 125, 30, "Stop server");
		Button disconnectButton = new Button(8, 90, 125, 30, "Disconnect");

		stopButton.addActionListener(e -> sendRequest(stopRequest, host, port));
		disconnectButton.addActionListener(e -> sendRequest(disconnectRequest, host, port));

		panel.add(stopButton);
		panel.add(disconnectButton);
	}

	@Override
	public void setPanel() {
		panel = new JPanel();
		panel.setLayout(null);
	}

	@Override
	public void setField() {

	}

	public static void sendRequest(HashMap<String, String> pc, String url, String port) {
		try {
			url = "http://" + url + ":" + port + "?";
			for (String key : pc.keySet()) {
				url += key + "=" + pc.get(key) + "&";
			}
			URL server = new URL(url);
			InputStream is = server.openStream();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void initPackages() {
		disconnectRequest.put("type", "disconnect");
		disconnectRequest.put("user", user);

		stopRequest.put("type", "stopServer");
		stopRequest.put("user", user);
	}

	private void init(String name, Integer width, Integer height) {
		setDecoration();
		frame = new JFrame(name);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initPackages();
		setPanel();
		setLabel();
		setField();
		setButton();
		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

}
