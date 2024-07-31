package com.insight.graphics;

public final class Font {
	private static final String CHARSET = 	" !'#$%&'()*+,-. 01" +
										    "23456789:;<=>?@ABC" +
										    "DEFGHIJKLMNOPQRSTU" +
										    "VWXYZ[ ]^_`abcdefg" +
										    "hijklmnopqrstuvwxy" +
										    "z{ }~             " ;
	
	private static final int CHARS_PER_LINE = 18;
	private static final int CHAR_ROWS = 18;
	
	public static final int CHAR_HEIGHT = 9;
	public static final int CHAR_WIDTH = 7;

	public static void write(Screen screen, final String msg, int x, int y, int color) {
		for(int i = 0; i < msg.length(); ++i) {
			int chr = CHARSET.indexOf(msg.charAt(i));
			if(chr < 0) continue;
			int px = chr % CHARS_PER_LINE, py = chr / CHAR_ROWS;
			screen.blit(Art.font, x + i * CHAR_WIDTH, y, px * CHAR_WIDTH, py * CHAR_HEIGHT, CHAR_WIDTH, CHAR_HEIGHT, color);
		}
	}
	
	public static void write(Screen screen, final String msg, int x, int y, int color, int scale) {
		for(int i = 0; i < msg.length(); ++i) {
			int chr = CHARSET.indexOf(msg.charAt(i));
			if(chr < 0) continue;
			int px = chr % CHARS_PER_LINE, py = chr / CHAR_ROWS;
			screen.blitScale(Art.font, scale, x + i * CHAR_WIDTH * scale, y, px * CHAR_WIDTH, py * CHAR_HEIGHT, CHAR_WIDTH, CHAR_HEIGHT, color);
		}
	}
	
	public static void write(Screen screen, final Character c, int x, int y, int color) {
		int chr = CHARSET.indexOf(c);
		if(chr < 0) return;
		int px = chr % CHARS_PER_LINE, py = chr / CHAR_ROWS;
		screen.blit(Art.font, x, y, px * CHAR_WIDTH, py * CHAR_HEIGHT, CHAR_WIDTH, CHAR_HEIGHT, color);
	}
	
	public static void write(Screen screen, final Character c, int x, int y, int color, int scale) {
		int chr = CHARSET.indexOf(c);
		if(chr < 0) return;
		int px = chr % CHARS_PER_LINE, py = chr / CHAR_ROWS;
		screen.blitScale(Art.font, scale, x, y, px * CHAR_WIDTH, py * CHAR_HEIGHT, CHAR_WIDTH, CHAR_HEIGHT, color);
	}
}
