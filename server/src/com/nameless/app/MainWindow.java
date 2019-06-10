package com.nameless.app;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainWindow extends Thread implements Window{
	private JFrame frame;
	private JPanel panel;

	public MainWindow(String name, Integer width,
					  Integer height, String passwordServer,
					  String portServer) throws IOException {
		init(name, width, height, passwordServer, portServer);
	}

	@Override
	public void setLabel() {
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


	private void init(String name, Integer width, Integer height, String passwordServer, String portServer) throws IOException {
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
