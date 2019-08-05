package com.nameless.app;

import com.nameless.Server;
import com.nameless.elements.Button;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;

public class BanWindow implements Window{
	private Server server;
	private JFrame frame;
	private JPanel panel;

	public BanWindow(String name, Integer width,
					  Integer height, Server server) {
		this.server = server;
		init(name, width, height);
	}

	@Override
	public void setLabel() {
	}

	@Override
	public void setButton() {
		Button unban = new Button(50, 220, 100, 32, "Unban");
		panel.add(unban);
	}

	@Override
	public void setPanel() {
		panel = new JPanel();
		panel.setLayout(null);
	}

	@Override
	public void setField() {
	}

	private void setList() {
		server.usersList = new JList();
		server.usersList.setBounds(10, 10, 180, 200);
		server.usersList.setBorder(new LineBorder(Color.BLACK));
		panel.add(server.usersList);
	}

	private void init(String name, Integer width, Integer height) {
		setDecoration();
		frame = new JFrame(name);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setPanel();
		setList();
		setButton();
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}
}
