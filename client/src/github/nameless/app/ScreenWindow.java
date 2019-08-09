package github.nameless.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class ScreenWindow implements Window {

	JFrame frame;
	JPanel panel;
	JLabel imageLabel;

	public ScreenWindow(int width, int height) {
		init(width, height);
	}

	@Override
	public void setLabel() {
		ImageIcon icon = new ImageIcon();
		imageLabel = new JLabel();
		imageLabel.setIcon(icon);
		imageLabel.setBounds(0, 0, 0, 0);
		panel.add(imageLabel);
	}

	@Override
	public void setButton() {

	}

	@Override
	public void setPanel() {
		panel = new JPanel();
		frame.getContentPane().add(panel);
	}

	@Override
	public void setField() {

	}

	private void init(int width, int height) {
		frame = new JFrame("Picture");
		setDecoration();
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		setPanel();
		setLabel();
		setField();
		setButton();

		frame.add(panel);
		frame.pack();
	}
}
