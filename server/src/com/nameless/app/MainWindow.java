package com.nameless.app;

import com.nameless.Server;
import com.nameless.Status;
import com.nameless.elements.Button;
import com.nameless.elements.Label;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

public class MainWindow extends Thread implements Window {
	private JPanel panel;
	private Server server;
	private String info = "server is working";

	public Label s;
	public JList ipList;
	public JFrame frame;
	public JList usersList;
	public TextArea logsArea;
	public DefaultListModel<String> ipModel;
	public DefaultListModel<String> usersModel;


	public MainWindow(String name, Integer width,
					  Integer height, Server server) throws IOException, InterruptedException {
		this.server = server;
		init(name, width, height);
	}

	@Override
	public void setLabel() throws IOException {
		Status status = new Status();
		Label port = new Label(10,10, "Server port: 52225");
		panel.add(port);

		Label ip = new Label(10,30, "Server IP: " + status.getIP());
		panel.add(ip);

		s = new Label(10, 50, "Status: " + info);
		panel.add(s);

		Label usersLabel = new Label(200, 10, "Users:");
		panel.add(usersLabel);

		Label ipLabel = new Label(410, 10, "IP:");
		panel.add(ipLabel);

		Label logs = new Label(200, 340, "Logs:");
		panel.add(logs);
	}

	@Override
	public void setButton() {
		Button stop = new Button(20, 70,130, 32, "Stop server");
		panel.add(stop);
		stop(stop);

		Button clear = new Button(20, 95, 130, 32, "Clear log");
		panel.add(clear);
		clearLog(clear);

		Button ban = new Button(20, 120, 130, 32, "Ban user");
		panel.add(ban);
		ban(ban);
	}

	@Override
	public void setPanel() {
		panel = new JPanel();
		panel.setLayout(null);
	}

	@Override
	public void setField() {

	}

	public void setArea() {
		logsArea = new TextArea("", 10, 40);
		logsArea.setBounds(200, 360, 410, 150);
		logsArea.setFont(new Font("Arial", Font.PLAIN, 13));
		logsArea.setEditable(false);
		panel.add(logsArea);
		panel.add(logsArea);
	}

	public void setList() {
		usersList = new JList();
		usersList.setBounds(200, 30, 200, 300);
		usersList.setBorder(new LineBorder(Color.BLACK));
		usersList.setSelectionMode(0);
		panel.add(usersList);

		ipList = new JList();
		ipList.setBounds(410, 30, 200, 300);
		ipList.setBorder(new LineBorder(Color.BLACK));
		ipList.setSelectionMode(0);
		panel.add(ipList);
	}

	public void getUsers() {
		try {
			HashMap<String, String> users = server.users;
			usersModel = new DefaultListModel<>();
			ipModel = new DefaultListModel<>();
			int j = 0;
			for (String i : users.keySet()) {
				ipModel.add(j, users.get(i));
				usersModel.add(j, i);
				j++;
			}
			Thread.sleep(1000);
			usersList.setModel(usersModel);
			ipList.setModel(ipModel);
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	private void clearLog(Button add) {
		ActionListener actionListener = e -> logsArea.setText("");
		add.addActionListener(actionListener);
	}

	public void ban(Button add) {
		ActionListener actionListener = e -> server.banUsers();
		add.addActionListener(actionListener);
	}

	public void stop(Button add) {
		ActionListener actionListener = e -> {
			try {
				server.stopServer(true);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		};
		add.addActionListener(actionListener);
	}

	private void init(String name, Integer width, Integer height) throws IOException {
		setDecoration();
		frame = new JFrame(name);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPanel();
		setLabel();
		setList();
		setButton();
		setArea();
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
	}

}
