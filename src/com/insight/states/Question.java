package com.insight.states;

import java.util.List;
import java.util.ArrayList;

public class Question {
	public List<String> choices;
	public String text;
	public int answer;
	
	public Question() {
		this.choices = new ArrayList<>();
	}
	
	public static Question with(final String text) {
		var question = new Question();
		question.text = text;
		return question;
	}
	
	public Question addChoice(final String choice) {
		this.choices.add(choice);
		return this;
	}
	
	public Question setAnswer(final int answer) {
		this.answer = answer;
		return this;
	}
}
