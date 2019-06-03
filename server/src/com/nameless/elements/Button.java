package com.company.main.core.elements;

import javax.swing.*;

public class Button extends JButton{
	private Integer x, y;
	private Integer width, height;
	private String name;

	public Button(Integer x, Integer y, Integer width, Integer height, String name) {
		this.setText(name);
		this.setBounds(x, y, width, height);
	}
}
