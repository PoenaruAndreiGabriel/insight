package com.insight.graphics;

import com.insight.Input;

public abstract class BigButton extends Button {
	private static final Bitmap base = Art.load("./res/bbutton.png");
	
	public BigButton(int x, int y, String label) {
		super(x, y, label);
		
		this.image = new Bitmap(base.width, base.height);
		this.image.blit(base, 0, 0);
	}
	
	@Override
	public void render(Screen screen, Input input) {
		super.render(screen, input);
		
		if(this.hovered(input)) {
			screen.blit(this.image, this.x + 1, this.y + 1, 0x7f7f7f);
			Font.write(screen, label, x + 1 + ((image.width - label.length() * Font.CHAR_WIDTH) >> 1), y + 1 + ((image.height - Font.CHAR_HEIGHT) >> 1), 0xdfdfdf);
		} else {
			Font.write(screen, label, x + ((image.width - label.length() * Font.CHAR_WIDTH) >> 1), y + ((image.height - Font.CHAR_HEIGHT) >> 1), 0xffffff);
		}
	}
	
	public static int width() {
		return base.width;
	}

	public static int height() {
		return base.height;
	}
}
