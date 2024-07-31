package com.insight;

import com.insight.graphics.Screen;

public abstract class State {
	public static final int MENU_STATE = 0;
	public static final int JOIN_STATE = 1;
	public static final int CREATE_STATE = 2;
	public static final int MINIGAMES_STATE = 3;
	public static final int ROOM_STATE = 4;
	public static final int XO_STATE = 5;
	public static final int PREPARE_STATE = 6;
	public static final int STATE_2048 = 7;
	public static final int MINESWEEPER_STATE = 8;
	public static final int PLAY_STATE = 9;
	public static final int OBSERVE_STATE = 10;
	
	protected Game game;
	
	public State(final Game game) {
		this.game = game;
	}
	
	public void init() {}
	
	public abstract void render(Screen screen);
	public abstract void update(Input input);
}
