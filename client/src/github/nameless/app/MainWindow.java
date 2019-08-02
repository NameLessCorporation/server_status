package github.nameless.app;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import github.nameless.elements.Button;
import github.nameless.elements.Field;
import github.nameless.elements.Label;

public class MainWindow implements Window{
	static JFrame frame;
	private static JPanel panel;
	JList<String> usersList, ipList;
	Button disconnectButton;
	Button stopButton;
	Button sendButton;
	Label cpuInfoLabel;
	Label ramInfoLabel;
	Label netInfoLabel;
	Label statusLabel;
	TextArea logArea, shellArea;
	String host, user;
	Field shellCommand;
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
		Label userLabel = new Label(10, 35, "Your connected as: " + user);
		panel.add(userLabel);
	}

	@Override
	public void setLabel() {
		cpuInfoLabel = new Label(250, 10, "CPU: ");
		ramInfoLabel = new Label(250, 35, "RAM:");
		netInfoLabel = new Label(250, 60, "Internet:");
		statusLabel = new Label(250, 85, "Status: not connected");
		Label userLabel = new Label(8, 130, "Users:");

		userLabel.setBounds(8, 130, 100, 15);

		panel.add(userLabel);
		panel.add(new Label(250, 110, "Log:"));
		panel.add(new Label(250, 577, "Command:"));

		panel.add(cpuInfoLabel);
		panel.add(ramInfoLabel);
		panel.add(netInfoLabel);
		panel.add(statusLabel);
	}

	@Override
	public void setButton() {
		stopButton = new Button(8, 60, 216, 30, "Stop server");
		disconnectButton = new Button(8, 90, 216, 30, "Disconnect");
		sendButton = new Button(790, 572, 100, 25, "Send");
		stopButton.addActionListener(e -> {
			logArea.append("Trying to stopping server\n");
			sendRequest(stopRequest, host);
		});
		disconnectButton.addActionListener(e -> {
			logArea.append("Trying to disconnect\n");
			sendRequest(disconnectRequest, host);
		});
		sendButton.addActionListener(e -> {
			if (! shellCommand.getText().trim().isEmpty()) {
				HashMap<String, String> shellPackage = new HashMap<>();
				shellPackage.put("type", "shell");
				shellPackage.put("data", shellCommand.getText().trim());
				sendRequest(shellPackage, host);
			}
		});

		panel.add(sendButton);
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
		shellCommand = new Field(330, 572, 450, 25);

		panel.add(shellCommand);
	}

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
		logArea.setBounds(250, 130, 630, 200);
		logArea.setFont(new Font("Arial", Font.PLAIN, 15));
		logArea.setEditable(false);
		panel.add(logArea);
	}

	private void setShellArea() {
		shellArea = new TextArea("", 10, 40);
		shellArea.setBounds(250, 360, 630, 200);
		shellArea.setFont(new Font("Arial", Font.PLAIN, 15));
		shellArea.setEditable(false);
		panel.add(shellArea);
	}

	private void setList() {
		usersList = new JList();
		usersList.setBounds(8, 150, 108, 300);
		usersList.setBorder(new LineBorder(Color.BLACK));
		usersList.setSelectionMode(0);
		panel.add(usersList);

		ipList = new JList();
		ipList.setBounds(116, 150, 108, 300);
		ipList.setBorder(new LineBorder(Color.BLACK));
		ipList.setSelectionMode(0);
		panel.add(ipList);
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
		setShellArea();
		setLogArea();
		setList();
		initPackages();

		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				logArea.append("Trying to disconnect\n");
				sendRequest(disconnectRequest, host);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignored) {}
			}
		});

		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}

}
