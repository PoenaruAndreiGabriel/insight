package com.insight.states;

import com.insight.graphics.*;
import com.insight.Content;
import com.insight.Database;
import com.insight.Input;
import com.insight.State;
import com.insight.Game;

public class JoinState extends State {
	private boolean codeFound, wasClicked, joinable;
	private String inputText;
	private Button submit;
	private Button back;
	private TextBox box;

	public JoinState(Game game) {
		super(game);
		
		wasClicked = false;
		this.box = new TextBox((Content.HEIGHT - 15) >> 1, 15, 6);

		this.submit = new BigButton((Content.WIDTH - BigButton.width()) >> 1, ((Content.HEIGHT + SmallButton.height()) >> 1) + 10, "Submit") {
			@Override
			public void clicked() {
				inputText = box.getInputText();
				codeFound = Database.get().searchRoom(inputText);
				wasClicked = true;
				joinable = Database.get().roomJoinable(inputText);
				if (codeFound && joinable) {
					game.setState(ROOM_STATE);
				}
			}
		};
		
		this.back = new SmallButton((Content.WIDTH - SmallButton.width()) >> 1, Content.HEIGHT - 10 - SmallButton.height(), "Back") {
			@Override
			public void clicked() {
				wasClicked = false;
				game.setState(State.MENU_STATE);
			}
		};
	}
	
	public String getID() {
		return inputText;
	}

	@Override
	public void render(Screen screen) {
		screen.fill(0, 0, screen.width, screen.height, 0xf6c858);

		if (wasClicked && !codeFound) {
			final String msg = "Join code '" + inputText + "' is not valid!";
			Font.write(screen, msg, (screen.width - msg.length() * Font.CHAR_WIDTH) >> 1, Font.CHAR_HEIGHT * 6, 0);
		} else if (wasClicked && !joinable) {
			final String msg = "Room '" + inputText + "' is not open yet!";
			Font.write(screen, msg, (screen.width - msg.length() * Font.CHAR_WIDTH) >> 1, Font.CHAR_HEIGHT * 6, 0);
		}

		this.box.render(screen, game.input);
		this.back.render(screen, game.input);
		this.submit.render(screen, game.input);
	}
	
	public void init() {
		codeFound = wasClicked = joinable = false;
		inputText = null;
		box.clear();
	}

	@Override
	public void update(Input input) {
		this.box.update(input);
		this.back.update(input);
		this.submit.update(input);
	}

	// Method to get the input text stored in the variable
	public String getInputText() {
		return inputText;
	}
}