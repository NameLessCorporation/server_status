package github.nameless.app;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class AboutWindow implements Window {

	JFrame frame;
	JPanel panel;

	public AboutWindow() {
		init();
	}

	@Override
	public void setLabel() {
		String pathToImage = "res/sticker.png";
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(pathToImage));
		JLabel imageLabel = new JLabel();
		imageLabel.setIcon(icon);
		imageLabel.setBounds(50, 50, 300, 100);
		imageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop desktop = java.awt.Desktop.getDesktop();
					URI oURL = new URI("https://github.com/NameLessCorporation/server_status");
					desktop.browse(oURL);
				} catch (Exception ignored) {}
			}
		});
		panel.add(imageLabel);

		JLabel label1 = new JLabel("Server Status 0.1", JLabel.CENTER);
		label1.setFont(new Font("Arial", Font.PLAIN, 18));
		label1.setForeground(Color.WHITE);
		label1.setBounds(0, 210, 400, 20);

		JLabel label2 = new JLabel("It's simple program, which help track your server", JLabel.CENTER);
		label2.setFont(new Font("Arial", Font.PLAIN, 15));
		label2.setForeground(Color.WHITE);
		label2.setBounds(0, 230, 400, 20);

		panel.add(label1);
		panel.add(label2);
	}

	@Override
	public void setButton() {

	}

	@Override
	public void setPanel() {
		panel = new JPanel();
		panel.setBackground(new Color(20, 118, 91));
		frame.getContentPane().add(panel);
	}

	@Override
	public void setField() {

	}

	private void init() {
		frame = new JFrame("About");
		setDecoration();
		frame.setPreferredSize(new Dimension(400, 250));
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		setPanel();
		setLabel();
		setField();
		setButton();

		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
