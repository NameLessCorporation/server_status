package github.nameless.app;

import javax.swing.*;
import java.awt.*;

public class MainWindow implements Window{
	private static JFrame frame;
	private static JPanel panel;
	String host, user;

	public MainWindow(String name, Integer width, Integer height, String host, String user) {
		init(name, width, height);
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


	private void init(String name, Integer width, Integer height) {
		setDecoration();
		frame = new JFrame(name);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPanel();
		//Here code
		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

}
