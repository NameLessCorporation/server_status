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
	private static JFrame frame;
	private static JPanel panel;
	private static Field port, password;

	public SetupWindow(String name, Integer width, Integer height, Boolean isEnter) {
		init(name, width, height);
		checkConnection(isEnter);
	}

	public void checkConnection(Boolean isEnter) {
		if (isEnter) {
			frame.setVisible(false);
			MainWindow mw = new MainWindow("Server - NameLess", 900, 600);
			mw.start();
		}
	}

	@Override
	public void setLabel() {
		Label title = new Label(10, 10, "Configurations server");
		panel.add(title);

		Label password = new Label(10, 30, "Password for connect:");
		panel.add(password);

		Label port = new Label(10, 80, "Port:");
		panel.add(port);
	}

	@Override
	public void setField() {
		password = new Field(7, 50, 180, 20);
		panel.add(password);

		port = new Field(7, 100, 80,20);
		panel.add(port);
	}

	@Override
	public void setButton() {
		Button start = new Button(100, 100, 85,20, "start");
		start(start);
		panel.add(start);
	}

	public void start(Button start) {
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String portServer = port.getText();
				String passwordServer = password.getText();
				checkConnection(true);
				try {
					Server server = new Server(portServer, passwordServer);
				} catch (IOException ex) {
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

	private void init(String name, Integer width, Integer height) {
//		Server server = new Server(portServer, passwordServer);
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
		frame.setVisible(true);
	}

}
