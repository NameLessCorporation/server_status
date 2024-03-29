package github.nameless.app;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import github.nameless.elements.Button;
import github.nameless.elements.Field;
import github.nameless.elements.Label;

public class MainWindow implements Window{
	JFrame frame;
	JPanel panel;
	JList<String> usersList, ipList;
	JMenuBar menuBar;
	Button disconnectButton;
	Button stopButton, sendButton, getScreenButton;
	Label cpuInfoLabel;
	Label ramInfoLabel;
	Label netInfoLabel;
	Label statusLabel;
	JLabel imageLabel;
	TextArea logArea, shellArea;
	String host, user;
	Field shellCommand;
	private final int SERVER_PORT = 52225;

	Server server;

	private HashMap<String, String> disconnectRequest = new HashMap<>();
	private HashMap<String, String> stopRequest = new HashMap<>();
	private HashMap<String, String> getScreenRequest = new HashMap<>();

	public MainWindow(String name, Integer width, Integer height) {
		init(name, width, height);
	}

	public void log(String log) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String nowDate = formatter.format(date);
		logArea.append(nowDate + "  |  " + log + "\n");
		logToFile(log);
	}

	public void logToFile(String log) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String nowDate = formatter.format(date);
		FileWriter fw = null;
		try {
			fw = new FileWriter("logs.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(nowDate + "  |  " + log);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		Label userLabel = new Label(8, 320, "Users:");

		userLabel.setBounds(8, 220, 100, 15);

		panel.add(userLabel);
		panel.add(new Label(250, 110, "Log:"));
		panel.add(new Label(250, 340, "Shell:"));
		panel.add(new Label(250, 582, "Command:"));

		panel.add(cpuInfoLabel);
		panel.add(ramInfoLabel);
		panel.add(netInfoLabel);
		panel.add(statusLabel);
	}

	@Override
	public void setButton() {
		stopButton = new Button(53, 60, 120, 32, "Stop server");
		disconnectButton = new Button(53, 90, 120, 32, "Disconnect");
		sendButton = new Button(790, 577, 100, 25, "Send");
		Button clearLogsButton = new Button(53, 120, 120, 32, "Clear logs");
		Button clearShellButton = new Button(53, 150, 120, 32, "Clear shell");
		getScreenButton = new Button(53, 180, 120, 32, "Get screenshot");

		stopButton.addActionListener(e -> {
			log("Trying to stopping server");
			sendRequest(stopRequest, host);
		});
		disconnectButton.addActionListener(e -> {
			log("Trying to disconnect");
			sendRequest(disconnectRequest, host);
		});
		sendButton.addActionListener(e -> sendShellRequest());
		clearLogsButton.addActionListener(e -> logArea.setText(""));
		clearShellButton.addActionListener(e -> shellArea.setText(""));
		getScreenButton.addActionListener(e -> {
			log("Trying to get screenshot");
			sendRequest(getScreenRequest, host);
		});

		panel.add(sendButton);
		panel.add(stopButton);
		panel.add(disconnectButton);
		panel.add(clearLogsButton);
		panel.add(clearShellButton);
		panel.add(getScreenButton);
	}

	public void sendShellRequest() {
		if (!shellCommand.getText().trim().isEmpty()) {
			HashMap<String, String> shellPackage = new HashMap<>();
			shellPackage.put("type", "shell");
			shellPackage.put("user", user);
			shellPackage.put("data", shellCommand.getText().trim());
			shellArea.append(">>> " + shellCommand.getText().trim() + "\n");
			shellCommand.setText("");
			sendRequest(shellPackage, host);
		}
	}

	@Override
	public void setPanel() {
		panel = new JPanel();
		panel.setLayout(null);
	}

	@Override
	public void setField() {
		shellCommand = new Field(330, 577, 450, 25);

		Action action = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				sendShellRequest();
			}
		};
		shellCommand.addActionListener(action);

		panel.add(shellCommand);
	}

	private void setMenu() {
		menuBar = new JMenuBar();
		JMenu helpMenu = new JMenu("Help");

		JMenuItem item = new JMenuItem("About");
		helpMenu.add(item);
		menuBar.add(helpMenu);
		item.addActionListener(e -> new AboutWindow());

		frame.setJMenuBar(menuBar);
	}

	private void sendRequest(HashMap<String, String> pc, String url) {
		try {
			url = "http://" + url + ":" + SERVER_PORT + "?";
			for (String key : pc.keySet()) {
				url += key + "=" + pc.get(key) + "&";
			}
			URL server = new URL(url);
			InputStream is = server.openStream();
		} catch (IOException e) {
			Notifications.showErrorNotification("Error", e.toString());
			logToFile(e.toString());
		}
	}

	private void setLogArea() {
		logArea = new TextArea("", 10, 40);
		logArea.setBounds(250, 130, 630, 200);
		logArea.setFont(new Font("Arial", Font.PLAIN, 13));
		logArea.setEditable(false);
		panel.add(logArea);
	}

	private void setShellArea() {
		shellArea = new TextArea("", 10, 40);
		shellArea.setBounds(250, 365, 630, 200);
		shellArea.setFont(new Font("Arial", Font.PLAIN, 13));
		shellArea.setEditable(false);
		panel.add(shellArea);
	}

	private void setList() {
		usersList = new JList();
		usersList.setBounds(8, 242, 108, 300);
		usersList.setBorder(new LineBorder(Color.BLACK));
		usersList.setSelectionMode(0);
		panel.add(usersList);

		ipList = new JList();
		ipList.setBounds(116, 242, 108, 300);
		ipList.setBorder(new LineBorder(Color.BLACK));
		ipList.setSelectionMode(0);
		panel.add(ipList);
	}

	public void initPackages() {
		disconnectRequest.put("type", "disconnect");
		disconnectRequest.put("user", user);

		stopRequest.put("type", "stopServer");
		stopRequest.put("user", user);

		getScreenRequest.put("type", "screen");
		getScreenRequest.put("user", user);
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
		setMenu();

		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if (!server.disconnected) {
					log("Trying to disconnect");
					sendRequest(disconnectRequest, host);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ignored) {}
				}
			}
		});

		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}

}
