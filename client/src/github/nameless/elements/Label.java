package github.nameless.elements;

import javax.swing.JLabel;
import java.awt.Font;

public class Label extends JLabel {
	private Integer x, y;
	private String name;

	public Label(Integer x, Integer y, String name) {
		this.setText(name);
		this.setBounds(x, y, name.length() * 70, 15);
		this.setFont(new Font("Arial", Font.PLAIN, 15));
	}
}
