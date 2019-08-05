package com.nameless.app;

import com.nameless.Server;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.io.IOException;

public class BanWindow implements Window{
	private Server server;
	private JFrame frame;
	private JPanel panel;

	public BanWindow(String name, Integer width,
					  Integer height, Server server) throws IOException, InterruptedException {
		this.server = server;
		init(name, width, height);
	}

	@Override
	public void setLabel() throws IOException {

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

	private void init(String name, Integer width, Integer height) {
		setDecoration();
		frame = new JFrame(name);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPanel();
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
	}
}
