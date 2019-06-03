package com.nameless.app;

import javax.swing.*;

public interface Window {
	public void setLabel();
	public void setButton();
	public void setPanel();
	public default void setDecoration() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {}
	}
}
