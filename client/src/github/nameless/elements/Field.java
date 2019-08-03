package github.nameless.elements;

import javax.swing.JTextField;

public class Field extends JTextField {
	private Integer x, y;
	private Integer width, height;

	public Field(Integer x, Integer y, Integer width, Integer height) {
		this.setBounds(x, y, width, height);
	}
}
