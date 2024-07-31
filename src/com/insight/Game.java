package com.insight;

import java.util.ArrayList;
import java.util.List;

import com.insight.states.*;
import com.insight.graphics.Screen;

public class Game {
	private List<State> states;
	private int current;
	
	public final Input input;
	
	public Game(final Input input) {
		this.states = new ArrayList<>();
		this.states.add(new MenuState(this));
		this.states.add(new JoinState(this));
		this.states.add(new CreateState(this));
		this.states.add(new MinigamesState(this));
		this.states.add(new RoomState(this));
		this.states.add(new XOState(this));
		this.states.add(new PrepareState(this));
		this.states.add(new State2048(this));
		this.states.add(new MinesweeperState(this));
		this.states.add(new PlayState(this));
		this.states.add(new ObserveState(this));
		this.current = State.MENU_STATE;
		this.input = input;
	}
	
	public State getState(final int state) {
		return this.states.get(state);
	}
	
	public final State getState() {
		return this.states.get(current);
	}
	
	public void setState(final int state) {
		this.current = state;
		this.states.get(current).init();
	}
	
	public void render(Screen screen) {
		this.states.get(current).render(screen);
	}
	
	public void update() {
		this.states.get(current).update(this.input);
	}
}
