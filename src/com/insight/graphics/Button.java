package com.insight.graphics;

import com.insight.Input;

public abstract class Button {
	protected Bitmap image;
	protected String label;
	public int x, y;
	
	public Button(final int x, final int y, final String label) {
		this.x = x; this.y = y;
		this.label = label;
	}
	
	public void render(Screen screen, Input input) {
		screen.blit(this.image, this.x, this.y);
	}
	
	public void update(Input input) {
		if(this.hovered(input) && input.mouse[0]) {
			this.clicked();
			input.mouse[0] = false;
		}
	}
	
	public abstract void clicked();
	
	public boolean hovered(final Input input) {
		return input.mx >= this.x && input.my >= this.y &&
				input.mx <= (this.x + this.image.width) &&
				input.my <= (this.y + this.image.height);
	}
}
