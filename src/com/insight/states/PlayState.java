package com.insight.states;

import java.util.Arrays;

import com.insight.Content;
import com.insight.Database;
import com.insight.Game;
import com.insight.Input;
import com.insight.State;
import com.insight.graphics.Bitmap;
import com.insight.graphics.Button;
import com.insight.graphics.Font;
import com.insight.graphics.Screen;
import com.insight.graphics.SmallButton;

public class PlayState extends State {
	static final int TIME_TO_WAIT = 30;
	private Question cquestion;
	private boolean answering = true;
	private int timer, choice;
	private long last;
	private boolean finished;
	private Button back;
	
	private abstract class ChoiceButton extends Button {
		public static final int WIDTH = 112;
		public static final int HEIGHT = 32;
		public int color;
		
		public ChoiceButton(int x, int y) {
			super(x, y, "");
			this.image = new Bitmap(WIDTH, HEIGHT);
		}
		
		public void setColor(final int color) {
			Arrays.fill(this.image.pixels, color);
		}
		
		public void setLabel(final String label) {
			this.label = label;
		}
		
		public void render(Screen screen, Input input, boolean highlight) {
			if(this.hovered(input)) {
				screen.blit(this.image, this.x + 1, this.y + 1, 0x7f7f7f);
				screen.blit(this.image, this.x, this.y);
				Font.write(screen, label, x + 1 + ((WIDTH - label.length() * Font.CHAR_WIDTH) >> 1), y + 1 + ((HEIGHT - Font.CHAR_HEIGHT) >> 1), 0xdfdfdf);
			} else {
				screen.blit(this.image, this.x, this.y);
				Font.write(screen, label, x + ((WIDTH - label.length() * Font.CHAR_WIDTH) >> 1), y + ((HEIGHT - Font.CHAR_HEIGHT) >> 1), 0xffffff);
			}
			
			if(highlight) {
				screen.fill(this.x - 1, this.y, 1, HEIGHT + 1, 0xeffffe);
				screen.fill(this.x, this.y + HEIGHT, WIDTH, 1, 0xeffffe);
				screen.fill(this.x + WIDTH, this.y, 1, HEIGHT + 1, 0xeffffe);
				screen.fill(this.x - 1, this.y - 1, WIDTH + 2, 1, 0xeffffe);
			}
		}
	}
	
	ChoiceButton choice1;
	ChoiceButton choice2;
	ChoiceButton choice3;
	ChoiceButton choice4;
	
	public PlayState(Game game) {
		super(game);
		
		choice1 = new ChoiceButton(30, Content.HEIGHT - 84) {
			@Override
			public void clicked() {
				choice = 0;
			}
		};
		
		choice2 = new ChoiceButton(30, Content.HEIGHT - 42) {
			@Override
			public void clicked() {
				choice = 1;
			}
		};
		
		choice3 = new ChoiceButton(Content.WIDTH - 142, Content.HEIGHT - 84) {
			@Override
			public void clicked() {
				choice = 2;
			}
		};
		
		choice4 = new ChoiceButton(Content.WIDTH - 142, Content.HEIGHT - 42) {
			@Override
			public void clicked() {
				choice = 3;
			}
		};
		
		back = new SmallButton((Content.WIDTH - SmallButton.width()) >> 1, Content.HEIGHT - SmallButton.height() - 15, "Back") {
			@Override
			public void clicked() {
		        game.setState(MENU_STATE);
			}
		};
		
		choice1.setColor(0xff0000);
		choice2.setColor(0xffff00);
		choice3.setColor(0x0000ff);
		choice4.setColor(0x00ff00);
	}
	
	public void init() {
		answering = true;
		finished = false;
		place = 1;
		last = System.currentTimeMillis();
		var lastr = (JoinState) game.getState(JOIN_STATE);
		timer = Database.get().getTimer(lastr.getID());
		cquestion = Database.get().questionFor(lastr.getID());
		choice = 0;
		
		choice1.setLabel(cquestion.choices.get(0));
		choice2.setLabel(cquestion.choices.get(1));
		choice3.setLabel(cquestion.choices.get(2));
		choice4.setLabel(cquestion.choices.get(3));
	}
	
	private int place = 1;

	@Override
	public void render(Screen screen) {
		screen.fill(0, 0, screen.width, screen.height, 0xf6c858);
		
		if(finished) {
			final String message = "Your place: " + place;
			Font.write(screen, message, (screen.width - message.length() * Font.CHAR_WIDTH) >> 1, (screen.height - Font.CHAR_HEIGHT) >> 1, 0xeffffe);
			back.render(screen, game.input);
			return;
		}
		
		if(answering) {
			Font.write(screen, String.valueOf(timer) + "s left", 10, 10, 0xeffffe);
			Font.write(screen, "Question: ", (screen.width - 10 * Font.CHAR_WIDTH) >> 1, 30, 0xeffffe);
			Font.write(screen, cquestion.text, (screen.width - cquestion.text.length() * Font.CHAR_WIDTH) >> 1, 50, 0xeffffe);
			
			choice1.render(screen, game.input, choice == 0);
			choice2.render(screen, game.input, choice == 1);
			choice3.render(screen, game.input, choice == 2);
			choice4.render(screen, game.input, choice == 3);
		} else {
			final String msg = "Waiting for next question ...";
			Font.write(screen, msg, (screen.width - msg.length() * Font.CHAR_WIDTH) >> 1, 50, 0xeffffe);
		}
	}

	@Override
	public void update(Input input) {
		if(finished) {
			back.update(input);
			return;
		}
		
		if(answering) {
			if(timer > 0) {
				if(System.currentTimeMillis() - last >= 1000) {
					var lastr = (JoinState) game.getState(JOIN_STATE);
					timer = Database.get().getTimer(lastr.getID());
					last = System.currentTimeMillis();
				}
			} else {
				answering = false;
				var lastr = (RoomState) game.getState(ROOM_STATE);
				var user = lastr.nameBox.sb.toString();
				var lastrr = (JoinState) game.getState(JOIN_STATE);
				var id = lastrr.getID();
				
				if(choice == cquestion.answer) {
					Database.get().addScore(user, id);					
				}
			}
			
			choice1.update(input);
			choice2.update(input);
			choice3.update(input);
			choice4.update(input);
		} else {
			var lastr = (JoinState) game.getState(JOIN_STATE);
			var nquest = Database.get().questionFor(lastr.getID());
			if(nquest == null || Database.get().roomFinished(lastr.getID())) {
				finished = true;
				var lastrr = (RoomState) game.getState(ROOM_STATE);
				place = Database.get().placeFor(lastrr.nameBox.sb.toString(), lastr.getID());
				return;
			}
			
			if(!cquestion.text.equals(nquest.text)) {
				answering = true;
				cquestion = nquest;
				timer = Database.get().getTimer(lastr.getID());
				
				choice1.setLabel(cquestion.choices.get(0));
				choice2.setLabel(cquestion.choices.get(1));
				choice3.setLabel(cquestion.choices.get(2));
				choice4.setLabel(cquestion.choices.get(3));
			}
		}
	}

}
