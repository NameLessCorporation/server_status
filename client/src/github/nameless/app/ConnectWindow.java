package github.nameless.app;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import github.nameless.elements.Field;
import github.nameless.elements.Label;
import github.nameless.elements.Button;

public class ConnectWindow implements Window {
	private static JFrame frame;
	private static JPanel panel;
	private DefaultListModel<String> connections;
	private JList list;
	private Field ipField;
	private Field portField;
	private Field userField;
	private Field passField;
	private Server server;
	private static int port = 52225;

	MainWindow window;

	public ConnectWindow(String name, int width, int height) {
		init(name, width, height);
	}

	@Override
	public void setLabel() {
		Label mainLabel = new Label(200, 12, "Server Control v0.1");
		Label servIPLabel = new Label(15, 40, "Host Name (or IP address):");
		Label portLabel = new Label(220, 40, "Port:");
		Label userLabel = new Label(15, 110, "Username:");
		Label passLabel = new Label(220, 110, "Password:");

		panel.add(mainLabel);
		panel.add(servIPLabel);
//		panel.add(portLabel);
		panel.add(userLabel);
		panel.add(passLabel);
	}

	@Override
	public void setButton() {
		Button connectButton = new Button(10, 170, 120, 25, "Connect");
		Button saveButton = new Button(292, 170, 100, 25, "Save");
		Button deleteButton = new Button(407, 170, 100, 25, "Delete");

		connectButton.addActionListener(e -> connect());
		saveButton.addActionListener(e -> saveConnection());
		deleteButton.addActionListener(e -> deleteConnection((String) list.getSelectedValue()));

		panel.add(connectButton);
		panel.add(saveButton);
		panel.add(deleteButton);
	}

	@Override
	public void setField() {
		ipField = new Field(10, 60,285, 25);
		portField = new Field(215, 60, 80, 25);
		userField = new Field(10, 130, 200, 25);
		passField = new Field(215, 130, 80, 25);

		panel.add(ipField);
//		panel.add(portField);
		panel.add(userField);
		panel.add(passField);
	}

	@Override
	public void setPanel() {
		panel = new JPanel();
		panel.setLayout(null);
	}

	private void setList() {
		list = new JList();
		list.setBounds(300, 62, 200, 92);

		list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!connections.isEmpty()) setConnection((String) list.getSelectedValue());
			}
		});

		panel.add(list);
	}

	private void getConnections() {
		connections = new DefaultListModel<>();
		File file = new File("connections");
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String connection = sc.nextLine();
				connections.addElement(connection);
			}
			if (!connections.isEmpty()) list.setModel(connections);
		} catch (FileNotFoundException e) {
			try {
				FileWriter writer = new FileWriter("connections");
				writer.write("");
				writer.flush();
				writer.close();
			} catch (IOException e1) {
				Notifications.showErrorNotification("Error", e1.toString());
			}
		}
	}

	private void saveConnection() {
		String ip = ipField.getText().trim();
		String user = userField.getText().trim();
		String pass = passField.getText().trim();
		if (!ip.isEmpty() && !user.isEmpty() && !pass.isEmpty()) {
			if (!connections.contains(ip + ":" + user + ":" + pass)) {
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter("connections", true));
					out.write(ip + ":" + user + ":" + pass + "\n");
					out.close();
				}
				catch (IOException e) {
					System.out.println("Exception Occurred" + e);
				}
				getConnections();
			}
		} else Notifications.showErrorNotification("Error", "Some fields are empty!");
	}

	private void setConnection(String connection) {
		String[] connectionArray = connection.split(":");
		ipField.setText(connectionArray[0]);
		userField.setText(connectionArray[1]);
		passField.setText(connectionArray[2]);
	}

	private void deleteConnection(String connection) {
		if (connection != null) {
			int index;
			for (index = 0; index < connections.size(); index++) if (connections.get(index).equals(connection)) break;
			connections.remove(index);
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("connections"));
				for (int i = 0; i < connections.size(); i++) {
					out.write(connections.get(i) + "\n");
				}
				out.close();
			} catch (IOException e) {
				Notifications.showErrorNotification("Error", e.toString());
			}
		} else Notifications.showWarningNotification("Warning", "Please select connection from list");
	}

	private void connect() {
		String ip = ipField.getText().trim();
		String user = userField.getText().trim();
		String pass = passField.getText().trim();
		if (!ip.isEmpty() && !user.isEmpty() && !pass.isEmpty()) {
			HashMap<String, String> request = new HashMap<>();
			request.put("type", "connect");
			request.put("user", user);
			request.put("pass", pass);
			try {
				sendRequest(request, ip);
				server.setConnectRequest(request);
				window.logArea.append("Trying to connect to " + ip + "\n");
				window.logArea.append("Waiting for data from " + ip + "\n");
				window.statusLabel.setText("Status: connecting");
				frame.setVisible(false);
				window.setHost(ip);
				server.setHost(ip);
				window.setUser(user);
				window.initPackages();
				window.frame.setVisible(true);
				window.setServer(server);
			} catch (IOException e) {
				Notifications.showErrorNotification("Error", e.toString());
			}
		} else Notifications.showErrorNotification("Error", "Some fields are empty!");
	}

	private static void sendRequest(HashMap<String, String> pc, String url) throws IOException {
		url = "http://" + url + ":" + port + "?";
		for (String key : pc.keySet()) url += key + "=" + pc.get(key) + "&";
		URL server = new URL(url);
		InputStream is = server.openStream();
	}

	private void init(String name, int width, int height) {
		server = new Server();
		window = new MainWindow("NameLess Server Status - Client", 900, 600);
		server.setFrame(window);
		frame = new JFrame(name);
		setDecoration();
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setPanel();
		setLabel();
		setField();
		setList();
		setButton();
		getConnections();

		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		server.run();
	}

}
