package com.insight.graphics;

import com.insight.Input;

public class CheckBox {
	private static final Bitmap eimg = Art.load("./res/echeck.png");
	private static final Bitmap img = Art.load("./res/check.png");
	
	public boolean checked; 
	public int x, y;
	
	public CheckBox(final int x, final int y) {
		this.x = x; this.y = y;
		this.checked = false;
	}
	
	public void render(Screen screen) {
		if(checked) screen.blit(img, x, y);
		else screen.blit(eimg, x, y);
	}
	
	public void update(Input input) {
		if((input.mx >= this.x && input.mx <= (this.x + img.width)) &&
				input.my >= this.y && input.my <= (this.y + img.height)) {
			if(input.mouse[0]) {
				this.checked = !this.checked;
				input.mouse[0] = false;
			}
		}
	}
}
