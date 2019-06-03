package com.company.main.core.elements;

import javax.swing.*;

public class Field extends JTextField {
	private Integer x, y;
	private Integer width, height;

	public Field(Integer x, Integer y, Integer width, Integer height) {
		this.setBounds(x, y, width, height);
	}
}
