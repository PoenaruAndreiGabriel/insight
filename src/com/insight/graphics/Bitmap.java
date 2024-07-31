package com.insight.graphics;

import java.util.Arrays;

public class Bitmap {
	public final int width, height, pixels[];
	public static final int key = 0xff8112a0;
	
	public Bitmap(final int width, final int height, final int[] pixels) {
		this.width = width; this.height = height;
		this.pixels = pixels;
	}
	
	public Bitmap(final int width, final int height) {
		this.width = width; this.height = height;
		this.pixels = new int[width * height];
	}
	
	public static int lerp(final int color, final int other, double p) {
		if(p > 1) p = 1;
		else if(p < 0) p = 0;
		
		int cr = (color >> 16) & 255;
		int cg = (color >>  8) & 255;
		int cb = (color >>  0) & 255;
		
		int or = (other >> 16) & 255;
		int og = (other >>  8) & 255;
		int ob = (other >>  0) & 255;
		
		final int r = (int) (cr + (or - cr) * p);
		final int g = (int) (cg + (og - cg) * p);
		final int b = (int) (cb + (ob - cb) * p);
		return (r << 16) | (g << 8) | b;
	}
	
	public void clear(final int color) {
		Arrays.fill(this.pixels, color);
	}
	
	public void blitWrap(Bitmap other, int xo, int yo) {
		for(int y = 0; y < other.height; ++y) {
			int sy = (this.height + yo + y) % this.height;
			for(int x = 0; x < other.width; ++x) {
				int sx = (this.width + xo + x) % this.width;
				int pix = other.pixels[x + y * other.width];
				if(pix != key) this.pixels[sx + sy * this.width] = pix;
			}
		}
	}
	
	public void blitHFlip(Bitmap other, int xo, int yo) {
		for(int y = 0; y < other.height; ++y) {
			int starty = yo + y;
			if(starty < 0 || starty >= this.height) continue;
			for(int x = 0; x < other.width; ++x) {
				int startx = xo + x;
				if(startx < 0 || startx >= this.width) continue;
				
				int pix = other.pixels[(other.width - x - 1) + y * other.width];
				if(pix != key) this.pixels[startx + starty * this.width] = pix;
			}
		}
	}
	
	public void blit(Bitmap other, int xo, int yo) {
		for(int y = 0; y < other.height; ++y) {
			int starty = yo + y;
			if(starty < 0 || starty >= this.height) continue;
			for(int x = 0; x < other.width; ++x) {
				int startx = xo + x;
				if(startx < 0 || startx >= this.width) continue;
				
				int pix = other.pixels[x + y * other.width];
				if(pix != key) this.pixels[startx + starty * this.width] = pix;
			}
		}
	}
	
	public void blit(Bitmap other, int xo, int yo, int ofx, int ofy, int w, int h, int color) {
		for(int y = 0; y < h; ++y) {
			int starty = yo + y;
			if(starty < 0 || starty >= height) continue;
			for(int x = 0; x < w; ++x) {
				int startx = xo + x;
				if(startx < 0 || startx >= width) continue;
				
				final int pix = other.pixels[(x + ofx) + (y + ofy) * other.width];
				if(pix != key) {
					pixels[startx + starty * width] = pix & color;
				}
			}
		}
	}
	
	public void blitScale(Bitmap other, int scale, int xoff, int yoff, int xo, int yo, int w, int h, int col) {
		final int hx = h * scale;
		final int wx = w * scale;
		for(int y = 0; y < hx; ++y) {
			int starty = yoff + y;
			if(starty < 0 || starty >= height) continue;
			for(int x = 0; x < wx; ++x) {
				int startx = xoff + x;
				if(startx < 0 || startx >= width) continue;
				
				int pix = other.pixels[(x / scale + xo) + (y / scale + yo) * other.width];
				if(pix != key) pixels[startx + starty * width] = pix & col;
			}
		}
	}
	
	public void blit(Bitmap other, int xo, int yo, int color) {
		for(int y = 0; y < other.height; ++y) {
			int starty = yo + y;
			if(starty < 0 || starty >= this.height) continue;
			for(int x = 0; x < other.width; ++x) {
				int startx = xo + x;
				if(startx < 0 || startx >= this.width) continue;
				
				int pix = other.pixels[x + y * other.width];
				if(pix != key) this.pixels[startx + starty * this.width] = pix & color;
			}
		}
	}
	
	public void fill(final int xo, final int yo, final int w, final int h, final int color) {
		if(color == key) return;
		for(int y = 0; y < h; ++y) {
			final int sy = yo + y;
			if(sy < 0 || sy >= this.height) continue;
			for(int x = 0; x < w; ++x) {
				final int sx = xo + x;
				if(sx < 0 || sx >= this.width) continue;
				this.pixels[sx + sy * this.width] = color;
			}
		}
	}
}
