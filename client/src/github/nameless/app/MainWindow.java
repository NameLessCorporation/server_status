package github.nameless.app;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import github.nameless.elements.Button;
import github.nameless.elements.Label;

public class MainWindow implements Window{
	private static JFrame frame;
	private static JPanel panel;
	Label cpuInfoLabel;
	Label ramInfoLabel;
	Label netInfoLabel;
	private String host, user;
	private static int port = 52225;

	private HashMap<String, String> disconnectRequest = new HashMap<>();
	private HashMap<String, String> stopRequest = new HashMap<>();

	public MainWindow(String name, Integer width, Integer height, String host, String user) {
		this.host = host;
		this.user = user;
		init(name, width, height);
	}

	@Override
	public void setLabel() {
		Label ipLabel = new Label(10, 10, "Server address: " + host);
		Label userLabel = new Label(10, 35, "Your username as: " + user);

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

		stopButton.addActionListener(e -> sendRequest(stopRequest, host));
		disconnectButton.addActionListener(e -> sendRequest(disconnectRequest, host));

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

	private void initPackages() {
		disconnectRequest.put("type", "disconnect");
		disconnectRequest.put("user", user);

		stopRequest.put("type", "stopServer");
		stopRequest.put("user", user);
	}

	private void init(String name, Integer width, Integer height) {
		frame = new JFrame(name);
		setDecoration();
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
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
