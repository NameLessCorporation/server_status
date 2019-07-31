package com.nameless.app;

import com.nameless.Status;
import com.nameless.elements.Label;

import javax.swing.*;
import java.awt.*;

import java.io.IOException;

public class MainWindow extends Thread implements Window{
	private JFrame frame;
	private JPanel panel;

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

		Label s = new Label(10, 50, "Status: ");
		panel.add(s);
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

	private void init(String name, Integer width, Integer height)
			throws IOException {
		setDecoration();
		frame = new JFrame(name);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPanel();
		setLabel();
		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

}
