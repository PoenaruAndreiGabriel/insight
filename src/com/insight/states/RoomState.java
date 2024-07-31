package com.insight.states;

import java.awt.event.KeyEvent;

import com.insight.Content;
import com.insight.Database;
import com.insight.Game;
import com.insight.Input;
import com.insight.Player;
import com.insight.State;
import com.insight.graphics.Art;
import com.insight.graphics.Button;
import com.insight.graphics.Font;
import com.insight.graphics.Screen;
import com.insight.graphics.SmallButton;
import com.insight.graphics.TextBox;

public class RoomState extends State {
	private Button next, back, toMenu;
	private int currentAvatar;
	public TextBox nameBox;

	private double ang = 0.0;
	private int xarrow = 0;
	private int xback = 0;
	
	private boolean waiting = false;
	
	public void init() {
		currentAvatar = 0;
		nameBox.clear();
		waiting = false;
		kicked = false;
		started = false;
		waitKicked = waitStarted = 0;
	}

	public RoomState(Game game) {
		super(game);
		
		this.nameBox = new TextBox(Content.HEIGHT - 15 * 4, 15, 15);
		this.currentAvatar = 0;
		
		this.next = new SmallButton((Content.WIDTH >> 1) - SmallButton.width() - 5, Content.HEIGHT - 10 - SmallButton.height(), "Next") {
			@Override
			public void clicked() {
				var last = (JoinState) game.getState(JOIN_STATE);
				Database.get().playerJoin(last.getID(), new Player(nameBox.getInputText(), currentAvatar));
				waiting = true;
			}
		};
		
		this.back = new SmallButton((Content.WIDTH >> 1) + 5, Content.HEIGHT - 10 - SmallButton.height(), "Back") {
			@Override
			public void clicked() {
				game.setState(JOIN_STATE);
			}
		};
		
		this.toMenu = new SmallButton((Content.WIDTH - SmallButton.width()) >> 1, Content.HEIGHT - 10 - SmallButton.height(), "Back") {
			@Override
			public void clicked() {
				waiting = false;
				currentAvatar = 0;
				nameBox.clear();
				game.setState(MENU_STATE);
			}
		};
	}

	@Override
	public void render(Screen screen) {
		screen.fill(0, 0, screen.width, screen.height, 0xf6c858);
		screen.blitWrap(Art.back, xback * 1, 0);
		screen.blitWrap(Art.back, 30 + xback + Art.back.width, 0); ++xback;
		
		if(!waiting) {
			Font.write(screen, "Choose your avatar:", ((screen.width - 19 * Font.CHAR_WIDTH) >> 1) + 1, 11, 0x7f8f7f);
			Font.write(screen, "Choose your avatar:", (screen.width - 19 * Font.CHAR_WIDTH) >> 1, 10, 0xdfefdf);
			screen.blit(Art.avatars.get(this.currentAvatar), (screen.width - 64) >> 1, (screen.height - 96) >> 1);
			Font.write(screen, "Name:", 68, this.nameBox.y + 2, 0);
			this.nameBox.render(screen, game.input);
			
			if(this.currentAvatar == 0) {
				screen.blit(Art.arrow, ((screen.width - 64) >> 1) + 64 + 15 + xarrow, ((screen.height - 114) >> 1) + (114 - Art.arrow.height) >> 1);
			} else if(this.currentAvatar == Art.avatars.size() - 1) {
				screen.blitHFlip(Art.arrow, ((screen.width - 64) >> 1) - Art.arrow.width - (15 + xarrow), ((screen.height - 114) >> 1) + (114 - Art.arrow.height) >> 1);
			} else {
				screen.blit(Art.arrow, ((screen.width - 64) >> 1) + 64 + 15 + xarrow, ((screen.height - 114) >> 1) + (114 - Art.arrow.height) >> 1);
				screen.blitHFlip(Art.arrow, ((screen.width - 64) >> 1) - Art.arrow.width - (15 + xarrow), ((screen.height - 114) >> 1) + (114 - Art.arrow.height) >> 1);
			}
			
			this.next.render(screen, game.input);
			this.back.render(screen, game.input);
			
			xarrow = (int)(5 * Math.cos(ang));
			ang += -0.01 * (xarrow - 9);
		} else {
			screen.blit(Art.avatars.get(this.currentAvatar), (screen.width - 64) >> 1, (screen.height - 96) >> 1);
			
			final String msg = kicked ? "You have been kicked!" : "Waiting for the host to start ...";
			Font.write(screen, msg, (screen.width - msg.length() * Font.CHAR_WIDTH) >> 1, screen.height - 15 * 4, 0xfdeadf);
		
			if(kicked) {
				this.toMenu.render(screen, game.input);
			}
		}
	}
	
	private boolean kicked = false;
	private long waitKicked = 0;
	private Thread waiter;
	
	private long waitStarted = 0;
	private Thread swaiter;
	private boolean started = false;

	@Override
	public void update(Input input) {
		if(!waiting) {
			this.nameBox.update(input);
			
			if(!this.nameBox.selected) {
				if(input.keys[KeyEvent.VK_LEFT] && this.currentAvatar > 0) {
					--this.currentAvatar;
					input.keys[KeyEvent.VK_LEFT] = false;
				}
				
				if(input.keys[KeyEvent.VK_RIGHT] && this.currentAvatar < Art.avatars.size() - 1) {
					++this.currentAvatar;
					input.keys[KeyEvent.VK_RIGHT] = false;
				}
				
				this.next.update(input);
				this.back.update(input);
			}
		} else {
			final long elapsed = System.currentTimeMillis() - waitKicked;
			if(elapsed >= 200) {
				if(waiter != null) {
					try {
						waiter.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				waitKicked = System.currentTimeMillis();
				waiter = new Thread() {
					@Override
					public void run() {
						kicked = Database.get().wasKicked(nameBox.getInputText());
					}
				};
				
				waiter.start();
			}
			
			if(kicked) {
				this.toMenu.update(input);
			} else {
				final long selapsed = System.currentTimeMillis() - waitStarted;
				if(selapsed >= 200) {
					if(swaiter != null) {
						try {
							swaiter.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					waitStarted = System.currentTimeMillis();
					swaiter = new Thread() {
						@Override
						public void run() {
							var last = (JoinState) game.getState(JOIN_STATE);
							started = Database.get().roomStarted(last.getID());
						}
					};
					
					swaiter.start();
				}
				
				synchronized(this) {
					if(started) {
						try {
							swaiter.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						game.setState(PLAY_STATE);
					}
				}
			}
		}
	}

}
