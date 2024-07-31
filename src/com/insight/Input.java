package com.insight;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

public class Input implements KeyListener, MouseListener, MouseMotionListener {
	private static final double MSCALE = 1 / (double) Content.SCALE;
	
	private static final List<Integer> forbidden = List.of(
		KeyEvent.VK_CAPS_LOCK,
		KeyEvent.VK_SHIFT,
		KeyEvent.VK_BACK_SPACE,
		KeyEvent.VK_TAB,
		KeyEvent.VK_ESCAPE
	);
	
	public final boolean[] keys;
	public boolean mouse[];
	// public StringBuffer sb;
	public char lastChar;
	public double mx, my;
	
	public Input() {
		this.keys = new boolean[KeyEvent.KEY_LAST + 1];
		// this.sb = new StringBuffer();
		this.mouse = new boolean[2];
		this.lastChar = '\b';
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		var code = e.getKeyCode();
		
		if(0 <= code && code < this.keys.length) {
			this.keys[code] = true;
		}
		
		if(!forbidden.contains(code)) {
			lastChar = e.getKeyChar();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		var code = e.getKeyCode();
		
		if(0 <= code && code < this.keys.length) {
			this.keys[code] = false;
		}
	}
	
	public boolean any() {
		for(int i = 0; i < this.keys.length; ++i) {
			if(this.keys[i]) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		mx = (double)e.getX() * MSCALE;
		my = (double)e.getY() * MSCALE;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) mouse[0] = true;
		else if(e.getButton() == MouseEvent.BUTTON3) mouse[1] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) mouse[0] = false;
		else if(e.getButton() == MouseEvent.BUTTON3) mouse[1] = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

}
