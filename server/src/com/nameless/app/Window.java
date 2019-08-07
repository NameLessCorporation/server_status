package com.nameless.app;

import javax.swing.*;
import java.io.IOException;

public interface Window {
	public void setLabel() throws IOException;
	public void setButton() throws IOException;
	public void setPanel();
	public void setField();
	public default void setDecoration() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {}
	}
}
