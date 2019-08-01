package com.nameless.app;

import com.nameless.Server;
import com.nameless.Status;
import com.nameless.elements.Label;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

import java.io.IOException;
import java.util.HashMap;

public class MainWindow extends Thread implements Window{
	private JFrame frame;
	private JPanel panel;
	private static JList usersList;
	private static JList ipList;
	private String info = "server is working";

	public static Label s;

	public MainWindow(String name, Integer width,
					  Integer height, String passwordServer) throws IOException, InterruptedException {
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
	}

	@Override
	public void setButton() {

	}

	@Override
	public void setPanel() {
		panel = new JPanel();
		panel.setLayout(null);
	}

	@Override
	public void setField() {

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

	public static void getUsers() {
		HashMap<String, String> users = Server.users;
		DefaultListModel<String> usersModel = new DefaultListModel<>();
		DefaultListModel<String> ipModel = new DefaultListModel<>();
		int j = 0;
		for (String i: users.keySet()) {
			ipModel.add(j, users.get(i));
			usersModel.add(j, i);
			j++;
		}
		usersList.setModel(usersModel);
		ipList.setModel(ipModel);
	}

	private void init(String name, Integer width, Integer height)
			throws IOException {
		setDecoration();
		frame = new JFrame(name);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPanel();
		setLabel();
		setList();
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}

}
