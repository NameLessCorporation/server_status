package github.nameless.app;

import javax.swing.*;
import java.awt.*;

import github.nameless.elements.Button;
import github.nameless.elements.Label;

public class MainWindow implements Window{
	private static JFrame frame;
	private static JPanel panel;
	String host, user;

	public MainWindow(String name, Integer width, Integer height, String host, String user) {
		this.host = host;
		this.user = user;
		init(name, width, height);
	}

	@Override
	public void setLabel() {
		Label ipLabel = new Label(10, 10, "Server address: " + host);
		Label userLabel = new Label(10, 30, "You loggined as: " + user);

		panel.add(ipLabel);
		panel.add(userLabel);
	}

	@Override
	public void setButton() {
		Button stopButton = new Button(8, 50, 125, 30, "Stop server");
		Button disconnectButton = new Button(8, 80, 125, 30, "Disconnect");

		panel.add(stopButton);
		panel.add(disconnectButton);
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
		setLabel();
		setField();
		setButton();
		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

}
