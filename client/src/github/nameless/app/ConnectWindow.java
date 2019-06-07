package github.nameless.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import github.nameless.elements.Field;
import github.nameless.elements.Label;
import github.nameless.elements.Button;

public class ConnectWindow implements Window {
	private static JFrame frame;
	private static JPanel panel;
	private String[] connections;
	private JList list;
	private Field ipField;
	private Field portField;
	private Field userField;
	private Field passField;
	Server server = new Server();


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
		panel.add(portLabel);
		panel.add(userLabel);
		panel.add(passLabel);
	}

	@Override
	public void setButton() {
		Button connectButton = new Button(10, 170, 120, 25, "Connect");
		Button saveButton = new Button(292, 170, 100, 25, "Save");
		Button deleteButton = new Button(407, 170, 100, 25, "Delete");

		connectButton.addActionListener(e -> {
			try {
				connect();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		});

		panel.add(connectButton);
		panel.add(saveButton);
		panel.add(deleteButton);
	}

	@Override
	public void setField() {
		ipField = new Field(10, 60,200, 25);
		portField = new Field(215, 60, 80, 25);
		userField = new Field(10, 130, 200, 25);
		passField = new Field(215, 130, 80, 25);

		panel.add(ipField);
		panel.add(portField);
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

		panel.add(list);
	}

	private void connect() throws InterruptedException {
		String ip = ipField.getText().trim();
		String port = portField.getText().trim();
		String user = userField.getText().trim();
		String pass = passField.getText().trim();
		if (!ip.isEmpty() && !user.isEmpty() && !pass.isEmpty() && !port.isEmpty()) {
			HashMap<String, String> request = new HashMap<>();
			request.put("type", "connect");
			request.put("user", user);
			request.put("pass", pass);
			try {
				sendRequest(request, ip, port);
				frame.setVisible(false);
				server.setFrame(new MainWindow("NameLess Server Status - Client", 900, 600, ip, port, user));

			} catch (Exception e) {
				JOptionPane.showMessageDialog(frame, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
//			JOptionPane.showMessageDialog(frame, "Some fields are empty!", "Error", JOptionPane.ERROR_MESSAGE);
			Notifications.showErrorNotification("Error", "Some fields are empty!");
		}
	}

	public static void sendRequest(HashMap<String, String> pc, String url, String port) throws Exception {
		url = "http://" + url + ":" + port + "?";
		for (String key : pc.keySet()) {
			url += key + "=" + pc.get(key) + "&";
		}
		URL server = new URL(url);
		InputStream is = server.openStream();
	}

	private void init(String name, int width, int height) {
		setDecoration();
		frame = new JFrame(name);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPanel();
		setLabel();
		setField();
		setList();
		setButton();
		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		server.run();
	}

}
