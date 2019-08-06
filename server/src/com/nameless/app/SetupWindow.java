package com.nameless.app;

import com.nameless.Server;
import com.nameless.elements.Button;
import com.nameless.elements.Field;
import com.nameless.elements.Label;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SetupWindow implements Window {
	private JFrame frame;
	private JPanel panel;
	private Server server;
	private Field password;
	private MainWindow mw;

	public SetupWindow(String name, Integer width, Integer height, Boolean isEnter)
			throws IOException, InterruptedException {
		init(name, width, height);
	}

	public void checkConnection(Boolean isEnter, String passwordServer) {
		if (isEnter) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			frame.setVisible(false);
			mw.frame.setVisible(true);
		}
	}

	@Override
	public void setLabel() {
		Label title = new Label(10, 10, "Configurations server");
		panel.add(title);

		Label password = new Label(10, 30, "Password for connect:");
		panel.add(password);

		Label port = new Label(10, 83, "Port: 52225");
		panel.add(port);
	}

	@Override
	public void setField() {
		password = new Field(7, 50, 180, 20);
		panel.add(password);
	}

	@Override
	public void setButton() {
		Button start = new Button(100, 80, 85,20, "start");
		start(start);
		panel.add(start);
	}

	public void start(Button start) {
		ActionListener actionListener = e -> {
			String passwordServer = password.getText();
			server.setPassword(passwordServer);
			checkConnection(true, passwordServer);
		};
		start.addActionListener(actionListener);
	}

	@Override
	public void setPanel() {
		panel = new JPanel();
		panel.setLayout(null);
	}

	private void init(String name, Integer width, Integer height) throws IOException, InterruptedException {
		server = new Server();
		setDecoration();
		frame = new JFrame(name);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPanel();
		setLabel();
		setField();
		setButton();
		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		mw = new MainWindow("Server - NameLess",
				900, 600, server);
		server.setMw(mw);
		server.banUsers();
		server.startServer();
	}

}