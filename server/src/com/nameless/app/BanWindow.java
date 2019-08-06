package com.nameless.app;

import com.nameless.Server;
import com.nameless.elements.Button;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BanWindow implements Window{
	private Server server;
	private JFrame frame;
	private JPanel panel;
	private Integer index;

	public BanWindow(String name, Integer width,
					  Integer height, Server server) {
		this.server = server;
		init(name, width, height);
	}

	@Override
	public void setLabel() {
	}

	@Override
	public void setButton() {
		Button unban = new Button(50, 220, 100, 32, "Unban");
		panel.add(unban);
		unban(unban);
	}

	private void unban(Button unban) {
		ActionListener actionListener = e -> {
			System.out.println(index);
			server.usersBanModel.remove(index);
			server.usersList.setModel(server.usersBanModel);

			FileWriter fw = null;
			try {
				fw = new FileWriter("ban_list.txt", false);
				BufferedWriter bw = new BufferedWriter(fw);
				for (int i = 0; i < server.usersBanModel.size(); i++) {
					bw.write(server.usersBanModel.get(i));
					bw.newLine();
				}
				bw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		};
		unban.addActionListener(actionListener);
	}

	private void getUsersBanList() {
		ListSelectionListener listSelectionListener = listSelectionEvent -> {
			if (server.usersBanModel.size() > 0) {
				index = server.usersList.getSelectedIndex();
			}
		};
		server.usersList.addListSelectionListener(listSelectionListener);
	}

	@Override
	public void setPanel() {
		panel = new JPanel();
		panel.setLayout(null);
	}

	@Override
	public void setField() {
	}

	private void setList() {
		server.usersList = new JList();
		server.usersList.setBounds(10, 10, 180, 200);
		server.usersList.setBorder(new LineBorder(Color.BLACK));
		panel.add(server.usersList);
	}

	private void init(String name, Integer width, Integer height) {
		setDecoration();
		frame = new JFrame(name);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setPanel();
		setList();
		setButton();
		getUsersBanList();
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}
}
