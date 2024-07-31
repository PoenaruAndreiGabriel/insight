package com.insight.states;

import com.insight.graphics.SmallButton;
import com.insight.graphics.BigButton;
import com.insight.graphics.Screen;
import com.insight.graphics.Bitmap;
import com.insight.graphics.Button;
import com.insight.graphics.Art;

import com.insight.Content;
import com.insight.Input;
import com.insight.State;
import com.insight.Game;

public class MenuState extends State {
	private static final Bitmap press = Art.load("./res/press.png");
	private int upx = -Art.logo.width, upy = -Art.logo.height;
	private int btx = Content.WIDTH, bty = Content.HEIGHT;
	private double fadein = 0, fadedir = 0.025;
	private boolean animation, waiting;
	private int xback = 0, lx = 0;
	
	private final Button create, games, join, exit;

	public MenuState(final Game game) {
		super(game);
		
		this.join = new SmallButton((Content.WIDTH >> 1) - SmallButton.width() - 5, ((Content.HEIGHT - SmallButton.height()) >> 1), "Join") {
			@Override
			public void clicked() {
				game.setState(JOIN_STATE);
			}
		};
		
		this.create = new SmallButton((Content.WIDTH >> 1) + 5, ((Content.HEIGHT - SmallButton.height()) >> 1), "Create") {
			@Override
			public void clicked() {
				game.setState(CREATE_STATE);
			}
		};
		
		this.games = new BigButton((Content.WIDTH - BigButton.width()) >> 1, ((Content.HEIGHT + SmallButton.height()) >> 1) + 10, "Minigames") {
			@Override
			public void clicked() {
				game.setState(MINIGAMES_STATE);
			}
		};
		
		this.exit = new SmallButton((Content.WIDTH - SmallButton.width()) >> 1, (Content.HEIGHT - SmallButton.height() - 10), "Exit") {
			@Override
			public void clicked() {
				System.exit(0);
			}
		};
		
		this.animation = true;
		this.waiting = false;
	}

	@Override
	public void render(Screen screen) {
		screen.clear(0xf6c858);
		screen.blitWrap(Art.back, xback * 1, 0);
		screen.blitWrap(Art.back, 30 + xback + Art.back.width, 0);
		++xback;
		
		if(this.animation) {
			screen.blit(Art.logo, btx, bty, 0x131313);
			screen.blit(Art.logo, upx, upy);
			
			final int posy = ((screen.height - Art.logo.height) >> 1);
			if(upy < posy) {
				upy += 2;
			}
			
			if(bty > posy + 3) {
				bty -= 2;
			}
			
			if(upx < btx - 2) {
				upx += 2;
				btx -= 2;
			} else {
				this.waiting = true;
				var posx = (screen.width - press.width) >> 1;
				var prsy = (int)((((screen.height - press.height) << 2) / 5) + 5 * Math.sin(fadein * 2));
				screen.blit(press, posx, prsy, Bitmap.lerp(0xf6c858, 0xffffff, fadein));
				
				fadein += fadedir;
				if(fadein >= 1.0) fadedir = -0.025;
				else if(fadein <= 0.0) fadedir = 0.025;
			}
		} else {
			screen.blit(Art.logo, ((screen.width - Art.logo.width) >> 1) + 2 + lx, Art.logo.height + 12 + lx, 0x131313);
			screen.blit(Art.logo, ((screen.width - Art.logo.width) >> 1) + lx, Art.logo.height + 10);
			lx = (int)(2 * Math.cos(fadein));
			fadein += 0.075;
			
			create.render(screen, game.input);
			games.render(screen, game.input);
			join.render(screen, game.input);
			exit.render(screen, game.input);
		}
	}

	@Override
	public void update(Input input) {
		if(this.animation && this.waiting) {
			if(game.input.any()) {
				this.animation = false;
			} else if(game.input.mouse[0]) {
				this.animation = false;
				game.input.mouse[0] = false;
			}
			
		} else if(!this.animation) {
			create.update(input);
			games.update(input);
			join.update(input);
			exit.update(input);
		}
	}

}
