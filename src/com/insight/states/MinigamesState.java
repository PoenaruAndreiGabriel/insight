package com.insight.states;

import com.insight.Content;
import com.insight.Game;
import com.insight.Input;
import com.insight.State;
import com.insight.graphics.Art;
import com.insight.graphics.Bitmap;
import com.insight.graphics.Button;
import com.insight.graphics.Screen;
import com.insight.graphics.SmallButton;

public class MinigamesState extends State {
	private static final Bitmap logo = Art.load("./res/minigames.png");
	
	private Button xo, thousand, minesweeper, back;
	private int xback = 0, lx = 0;
	private double fadein = 0.0;
	
	public MinigamesState(Game game) {
		super(game);
		
		this.xo = new SmallButton(((Content.WIDTH - SmallButton.width()) >> 1), (Content.HEIGHT >> 1) - ((SmallButton.height() * 3) / 2 + 10), "X & O") {
			@Override
			public void clicked() {
				game.setState(XO_STATE);
			}
		};
		
		this.thousand = new SmallButton(((Content.WIDTH - SmallButton.width()) >> 1), ((Content.HEIGHT - SmallButton.height()) >> 1), "2048") {
			@Override
			public void clicked() {
				game.setState(STATE_2048);
			}
		};
		
		this.minesweeper = new SmallButton(((Content.WIDTH - SmallButton.width()) >> 1), (Content.HEIGHT >> 1) + (SmallButton.height() / 2) + 10, "Mines") {
			@Override
			public void clicked() {
				game.setState(MINESWEEPER_STATE);
			}
		};
		
		this.back = new SmallButton((Content.WIDTH - SmallButton.width()) >> 1, Content.HEIGHT - 10 - SmallButton.height(), "Back") {
			@Override
			public void clicked() {
				game.setState(MENU_STATE);
			}
		};
	}

	@Override
	public void render(Screen screen) {
		screen.fill(0, 0, screen.width, screen.height, 0xf6c858);
		screen.blitWrap(Art.back, xback * 1, 0);
		screen.blitWrap(Art.back, 30 + xback + Art.back.width, 0); ++xback;
		screen.blit(logo, ((screen.width - logo.width) >> 1) + 3 + lx, logo.height + 3 + lx, 0x131713);
		screen.blit(logo, ((screen.width - logo.width) >> 1) + lx, logo.height);
		lx = (int)(2 * Math.cos(fadein));
		fadein += 0.075;
		
		this.thousand.render(screen, game.input);
		this.back.render(screen, game.input);
		this.minesweeper.render(screen, game.input);
		this.xo.render(screen, game.input);
	}

	@Override
	public void update(Input input) {
		this.thousand.update(input);
		this.back.update(input);
		this.minesweeper.update(input);
		this.xo.update(input);
	}

}
