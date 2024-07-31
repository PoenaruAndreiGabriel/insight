package com.insight;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Insight extends JFrame {
	private static final String TITLE = "Insight - education and recreation";
	
	private final Content content;
	
	public Insight() {
		super(TITLE);
		this.content = new Content();
		this.add(this.content);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				content.stop();
			}
		});
		
		this.content.start();
	}
}
