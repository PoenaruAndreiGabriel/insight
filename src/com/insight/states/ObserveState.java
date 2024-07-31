package com.insight.states;

import com.insight.Content;
import com.insight.Database;
import com.insight.Game;
import com.insight.Input;
import com.insight.State;
import com.insight.graphics.Art;
import com.insight.graphics.Button;
import com.insight.graphics.Font;
import com.insight.graphics.Screen;
import com.insight.graphics.SmallButton;
import java.util.ArrayList;
import java.util.List;

public class ObserveState extends State {
	static final int TIME_TO_WAIT = 30;
	private Question cquestion;
	private boolean waiting, finished;
	private int timer;
	private long last;
	private Button next, back;
	private List<String> top3;
	private List<Integer> scores;
	
	public ObserveState(Game game) {
		super(game);
		
		next = new SmallButton((Content.WIDTH - SmallButton.width()) >> 1, Content.HEIGHT - SmallButton.height() - 15, "Next") {
			@Override
			public void clicked() {
		        var last = (CreateState) game.getState(CREATE_STATE);
		        Database.get().setTimer(last.getID(), TIME_TO_WAIT);
				Database.get().nextQuestion(last.getID());
				if(Database.get().roomFinished(last.getID())) {
					finished = true;
					scores = Database.get().getScores(last.getID());
					top3 = Database.get().top3(last.getID());
				} else {
					cquestion = Database.get().questionFor(last.getID());
					waiting = true;
					timer = TIME_TO_WAIT;
				}
			}
		};
		
		back = new SmallButton((Content.WIDTH - SmallButton.width()) >> 1, Content.HEIGHT - SmallButton.height() - 15, "Back") {
			@Override
			public void clicked() {
		        game.setState(MENU_STATE);
			}
		};
	}
	
	public void init() {
		waiting = true;
		last = System.currentTimeMillis();
        var lastr = (CreateState) game.getState(CREATE_STATE);
		timer = Database.get().getTimer(lastr.getID());
		cquestion = Database.get().questionFor(lastr.getID());
		finished = false;
		
		if(cquestion == null) {
	        game.setState(MENU_STATE);
		}
		
		top3 = new ArrayList<>();
		scores = new ArrayList<>();
	}

    private int xback = 0;
    static final int wincol[] = new int[] {
    		0xeffe00,
			0xaaaaaa,
			0xffaa00
    };
    
	@Override
	public void render(Screen screen) {
		screen.fill(0, 0, screen.width, screen.height, 0xf6c858);
        screen.blitWrap(Art.back, xback * 1, 0);
        screen.blitWrap(Art.back, 30 + xback + Art.back.width, 0);
        ++xback;
		
		if(waiting) {
			Font.write(screen, String.valueOf(timer) + "s left", 10, 10, 0xeffffe);
			Font.write(screen, "Question: ", (screen.width - 10 * Font.CHAR_WIDTH) >> 1, 50, 0xeffffe);
			Font.write(screen, cquestion.text, (screen.width - cquestion.text.length() * Font.CHAR_WIDTH) >> 1, 70, 0xeffffe);
		} else {
			if(finished) {
				final String message = "The game has finished";
				final String message2 = "* Click `Back` to go back to the menu";
				
				Font.write(screen, message, (screen.width - message.length() * Font.CHAR_WIDTH) >> 1, 10, 0xeffffe);
				Font.write(screen, message2, (screen.width - message2.length() * Font.CHAR_WIDTH) >> 1, 30, 0xeffffe);
				
				Font.write(screen, "Top 3", (screen.width - 5 * Font.CHAR_WIDTH) >> 1, 50, 0xeffffe);
				
				for(int i = 0; i < top3.size(); ++i) {
					final String mmessage = (i + 1) + ". " + top3.get(i) + " - " + scores.get(i);
					Font.write(screen, mmessage, (screen.width - mmessage.length() * Font.CHAR_WIDTH) >> 1, 70 + 20 * i, wincol[i]);
				}
				
				back.render(screen, game.input);
			} else {
				final String message = "* Click `Next` to start the next question !";
				Font.write(screen, message, (screen.width - message.length() * Font.CHAR_WIDTH) >> 1, 75, 0xeffffe);
				
				next.render(screen, game.input);
			}
		}
	}

	@Override
	public void update(Input input) {
		if(timer > 0) {
			if(System.currentTimeMillis() - last >= 1000) {
				--timer;
				var lastr = (CreateState) game.getState(CREATE_STATE);
				Database.get().setTimer(lastr.getID(), timer);
				last = System.currentTimeMillis();
			}
		} else {
			waiting = false;
			if(!finished) next.update(input);
		}
		
		if(finished) {
			back.update(input);
		}
	}

}
