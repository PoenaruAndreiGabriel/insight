package com.insight;

import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.Dimension;
import java.awt.Canvas;

import com.insight.graphics.*;

@SuppressWarnings("serial")
public class Content extends Canvas implements Runnable {
	public static final int HEIGHT = 180;
	public static final int WIDTH = 320;
	public static final int SCALE = 3;
	
	private static final Dimension DIM = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
	
	private boolean running;
	private Thread thread;
	
	private BufferedImage content;
	private int pixels[];
	
	private final Screen screen;
	private final Input input;
	private final Game game;
	
	public Content() {
		this.setMinimumSize(DIM);
		this.setPreferredSize(DIM);
		this.setMaximumSize(DIM);
				
		this.content = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.pixels = ((DataBufferInt)this.content.getRaster().getDataBuffer()).getData();
		this.screen = new Screen(WIDTH, HEIGHT, this.pixels);
		this.input = new Input();
		this.game = new Game(input);
		
		this.addKeyListener(this.input);
		this.addMouseListener(this.input);
		this.addMouseMotionListener(this.input);
	}
	
	public synchronized void start() {
		if(running) return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void tick() {
		if(this.hasFocus()) {
			this.game.update();
		}
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		
		if(bs == null) {
			createBufferStrategy(3);
			requestFocus();
			return;
		}
		
		//if(screen.shouldClear) Arrays.fill(screen.pixels, 0);
		
		game.render(screen);
		
		//System.arraycopy(screen.pixels, 0, pixels, 0, pixels.length);
		
		var g = bs.getDrawGraphics();
		
		g.drawImage(this.content, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		
		g.dispose();
		bs.show();
	}
	
	public void run() {
		long now, then;
		double unprocessed = 0.0;
		final double ns_per_tick = 1000000000.0 / 60.0;
		then = System.nanoTime();
		boolean shouldRender = false;
		
		while(this.running) {
			shouldRender = false;
			now = System.nanoTime();
			unprocessed += (now - then);
			then = now;
			
			while(unprocessed >= ns_per_tick) {
				tick();
				shouldRender = true;
				unprocessed -= ns_per_tick;
			}
			
			if(shouldRender) {
				render();
			}
		}
	}
	
	public synchronized void stop() {
		if(!running) return;
		running = false;
		
		try {
			thread.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}
