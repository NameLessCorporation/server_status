package com.nameless.app;

import com.nameless.Server;
import com.nameless.elements.Button;
import com.nameless.elements.Field;
import com.nameless.elements.Label;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SetupWindow implements Window {
	private JFrame frame;
	private JPanel panel;
	private Field password;

	public SetupWindow(String name, Integer width, Integer height, Boolean isEnter) throws IOException {
		init(name, width, height);
	}

	public void checkConnection(Boolean isEnter, String passwordServer)
			throws IOException, InterruptedException {
		if (isEnter) {
			frame.setVisible(false);
			MainWindow mw = new MainWindow("Server - NameLess",
											900, 600, passwordServer);
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
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String passwordServer = password.getText();
				Server.setPassword(passwordServer);
				try {
					checkConnection(true, passwordServer);
				} catch (IOException | InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		};
		start.addActionListener(actionListener);
	}

	@Override
	public void setPanel() {
		panel = new JPanel();
		panel.setLayout(null);
	}

	private void init(String name, Integer width, Integer height) throws IOException {
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
		Server server = new Server();
	}

}