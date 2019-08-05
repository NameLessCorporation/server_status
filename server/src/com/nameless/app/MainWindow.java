package com.nameless.app;

import com.nameless.Server;
import com.nameless.Status;
import com.nameless.elements.Button;
import com.nameless.elements.Label;
import com.nameless.elements.Notifications;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;


public class MainWindow extends Thread implements Window {
	private JPanel panel;
	private Server server;
	private String info = "server is working";

	public Label s;
	public Button ban;
	public Button stop;
	public JList ipList;
	public JFrame frame;
	public Label ipLabel;
	public JList usersList;
	public Label usersLabel;
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

		usersLabel = new Label(200, 10, "Users:");
		panel.add(usersLabel);

		ipLabel = new Label(410, 10, "IP:");
		panel.add(ipLabel);

		Label logs = new Label(200, 340, "Logs:");
		panel.add(logs);
	}

	@Override
	public void setButton() {
		stop = new Button(20, 70,130, 32, "Stop server");
		panel.add(stop);
		stop(stop);

		Button clear = new Button(20, 95, 130, 32, "Clear log");
		panel.add(clear);
		clearLog(clear);

		ban = new Button(20, 120, 130, 32, "Disconnect user");
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

	private void setArea() {
		logsArea = new TextArea("", 10, 40);
		logsArea.setBounds(200, 360, 410, 150);
		logsArea.setFont(new Font("Arial", Font.PLAIN, 13));
		logsArea.setEditable(false);
		panel.add(logsArea);
		panel.add(logsArea);
	}

	private void setList() {
		usersList = new JList();
		usersList.setBounds(200, 30, 200, 300);
		usersList.setBorder(new LineBorder(Color.BLACK));
		panel.add(usersList);

		ipList = new JList();
		ipList.setBounds(410, 30, 200, 300);
		ipList.setBorder(new LineBorder(Color.BLACK));
		panel.add(ipList);
	}

	private void setMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Settings");
		JMenuItem banMenu = new JMenuItem("Ban list");
		banWindow(banMenu);
		menu.add(banMenu);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);
	}

	private void banWindow(JMenuItem banMenu) {
		ActionListener actionListener = e -> {
			try {
				BanWindow bw = new BanWindow("Ban list", 200, 300, server);
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		};
		banMenu.addActionListener(actionListener);
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
			Thread.sleep(3000);
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
		Notifications n = new Notifications();
		ActionListener actionListener = e -> {
			if (!server.userBan.isEmpty()) {
				for (String i : server.users.keySet()) {
					if(server.users.containsKey(server.userBan)) {
						server.users.remove(server.userBan);
						server.setLogs(server.userBan + " was disconnected");
						n.showInfoNotification("User disconnect",
								server.userBan + " disconnect from server");
						usersLabel.setText("Users: ");
					} else if (server.users.containsValue(server.userBan)) {
						for (String j : server.users.keySet()) {
							if (server.userBan.equals(server.users.get(j))) {
								server.users.remove(j);
								server.setLogs(server.userBan + " was disconnected");
								n.showInfoNotification("User disconnect",
										server.userBan + " disconnect from server");
								usersLabel.setText("IP: ");
							}
						}
					}
				}
			}
		};
		add.addActionListener(actionListener);
	}

	public void closeWindow() {
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try {
					server.stopServer(true);
				} catch (IOException ex) {}
			}
		});
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
		setMenu();
		closeWindow();
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
	}

}
