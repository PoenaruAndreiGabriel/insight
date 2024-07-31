package com.insight.states;

import java.util.List;
import java.util.ArrayList;

import com.insight.Content;
import com.insight.Database;
import com.insight.Game;
import com.insight.Input;
import com.insight.Player;
import com.insight.State;
import com.insight.graphics.Art;
import com.insight.graphics.Button;
import com.insight.graphics.CheckBox;
import com.insight.graphics.Font;
import com.insight.graphics.Screen;
import com.insight.graphics.SmallButton;
import com.insight.graphics.TextBox;

public class PrepareState extends State {
    private Button add, view, host, back, delete, start, quit, kick;
    private TextBox question, choice1, choice2, choice3, choice4;
    private CheckBox cchoice1, cchoice2, cchoice3, cchoice4;
    private boolean waitingForPlayers;
    private List<Question> questions;
    private List<CheckBox> qchecks;
    private boolean viewQuestions;
    private int xback = 0;
    
    public void init() {
    	waitingForPlayers = false;
    	this.questions = new ArrayList<>();
        this.qchecks = new ArrayList<>();
        this.viewQuestions = false;
        question.clear();
        choice1.clear();
        choice2.clear();
        choice3.clear();
        choice4.clear();
        checks.clear();
        players.clear();
        cchoice1.checked = false;
        cchoice2.checked = false;
        cchoice3.checked = false;
        cchoice4.checked = false;
    }

    public PrepareState(Game game) {
        super(game);

        this.add = new SmallButton(((Content.WIDTH) >> 1) - (SmallButton.width()) - 5, 7, "Add") {
            @Override
            public void clicked() {
                if(question.getInputText() == null || question.getInputText().isEmpty()) return;

                var q = Question.with(question.getInputText())
                        .addChoice(choice1.getInputText())
                        .addChoice(choice2.getInputText())
                        .addChoice(choice3.getInputText())
                        .addChoice(choice4.getInputText());

                if(cchoice1.checked) q.setAnswer(1);
                else if(cchoice2.checked) q.setAnswer(2);
                else if(cchoice3.checked) q.setAnswer(3);
                else if(cchoice4.checked) q.setAnswer(4);

                questions.add(q);
                qchecks.add(new CheckBox(10 + (q.text.length() + 5) * Font.CHAR_WIDTH, 10 + (questions.size() - 1) * 15));
            }
        };

        this.view = new SmallButton(((Content.WIDTH) >> 1) + 5, 7, "View") {
            @Override
            public void clicked() {
                viewQuestions = true;
            }
        };

        this.host = new SmallButton(((Content.WIDTH) >> 1) - (SmallButton.width()) - 5, Content.HEIGHT - 7 - SmallButton.height(), "Host") {
            @Override
            public void clicked() {
            	if(questions.isEmpty()) return;
            	
                var last = (CreateState) game.getState(CREATE_STATE);
                Database.get().setTimer(last.getID(), 30);
                Database.get().addQuestions(last.getID(), questions);
                Database.get().setJoinable(last.getID());
                waitingForPlayers = true;
            }
        };

        this.start = new SmallButton(((Content.WIDTH) >> 1) - ((SmallButton.width() * 3) >> 1) - 5, Content.HEIGHT - 7 - SmallButton.height(), "Start") {
            @Override
            public void clicked() {
            	var last = (CreateState) game.getState(CREATE_STATE);
            	Database.get().setStarted(last.getID());
            	game.setState(OBSERVE_STATE);
            }
        };

        this.kick = new SmallButton((Content.WIDTH - SmallButton.width()) >> 1, Content.HEIGHT - 7 - SmallButton.height(), "Kick") {
            @Override
            public void clicked() {
                var ids = new ArrayList<Player>();
                var chks = new ArrayList<CheckBox>();
                for(int i = 0; i < checks.size(); ++i) {
                    if(!checks.get(i).checked) {
                        ids.add(players.get(i));
                        chks.add(checks.get(i));
                    } else {
                        Database.get().kickPlayer(players.get(i).id);
                    }
                }

                players = null;
                players = ids;

                checks = null;
                checks = chks;
            }
        };

        this.quit = new SmallButton(((Content.WIDTH) >> 1) + 5 + (SmallButton.width() >> 1), Content.HEIGHT - 7 - SmallButton.height(), "Quit") {
            @Override
            public void clicked() {
                var last = (CreateState) game.getState(CREATE_STATE);
                Database.get().removeRoom(last.getID());
                game.setState(MENU_STATE);
            }
        };

        this.back = new SmallButton(((Content.WIDTH) >> 1) + 5, Content.HEIGHT - 7 - SmallButton.height(), "Back") {
            @Override
            public void clicked() {
                if(!viewQuestions) {
                    game.setState(CREATE_STATE);
                } else {
                    viewQuestions = false;
                }
            }
        };


        this.delete = new SmallButton(((Content.WIDTH) >> 1) - (SmallButton.width()) - 5, Content.HEIGHT - 7 - SmallButton.height(), "Delete") {
            @Override
            public void clicked() {
                if (!questions.isEmpty()) {
                	var nquestions = new ArrayList<Question>();
                	var nchecks = new ArrayList<CheckBox>();
                    for(int i = 0; i < questions.size(); ++i) {
                    	if(!qchecks.get(i).checked) {
                    		nquestions.add(questions.get(i));
                    		nchecks.add(qchecks.get(i));
                    	}
                    }
                    
                    questions = null;
                    questions = nquestions;
                    
                    qchecks = null;
                    qchecks = nchecks;
                    
                    for(int i = 0; i < qchecks.size(); ++i) {
                		qchecks.get(i).x = 10 + (questions.get(i).text.length() + 5) * Font.CHAR_WIDTH;
                		qchecks.get(i).y = 10 + i * 15;
                    }
                }
            }
        };


        this.question = new TextBox(7 + SmallButton.height() + 15, 15, 30);
        this.question.x = (Content.WIDTH - this.question.width) >> 1;

        final int border = 40;

        this.choice1 = new TextBox(0, 15, 15);
        this.choice1.x = border;
        this.choice1.y = ((Content.HEIGHT - 15) >> 1);

        this.choice2 = new TextBox(0, 15, 15);
        this.choice2.x = (Content.WIDTH - this.choice2.width - border);
        this.choice2.y = ((Content.HEIGHT - 15) >> 1);

        this.choice3 = new TextBox(0, 15, 15);
        this.choice3.x = border;
        this.choice3.y = ((Content.HEIGHT + 55) >> 1);

        this.choice4 = new TextBox(0, 15, 15);
        this.choice4.x = (Content.WIDTH - this.choice4.width - border);
        this.choice4.y = ((Content.HEIGHT + 55) >> 1);

        this.cchoice1 = new CheckBox(this.choice1.x - 20, this.choice1.y + 1);
        this.cchoice3 = new CheckBox(this.choice3.x - 20, this.choice3.y);
        this.cchoice2 = new CheckBox(this.choice2.x + this.choice2.width + 7, this.choice2.y + 1);
        this.cchoice4 = new CheckBox(this.choice4.x + this.choice4.width + 7, this.choice4.y + 1);
    }

    private List<CheckBox> checks = new ArrayList<CheckBox>();
    private List<Player> players = new ArrayList<Player>();

    @Override
    public void render(Screen screen) {
        screen.clear(0xf6c858);
        screen.blitWrap(Art.back, xback * 1, 0);
        screen.blitWrap(Art.back, 30 + xback + Art.back.width, 0);
        ++xback;

        if(!this.waitingForPlayers) {
            if(!this.viewQuestions) {
                this.add.render(screen, game.input);
                this.view.render(screen, game.input);
                this.host.render(screen, game.input);

                this.cchoice1.render(screen);
                this.cchoice2.render(screen);
                this.cchoice3.render(screen);
                this.cchoice4.render(screen);

                this.choice1.render(screen, game.input);
                this.choice2.render(screen, game.input);
                this.choice3.render(screen, game.input);
                this.choice4.render(screen, game.input);
                this.question.render(screen, game.input);
            } else {
                for(int i = 0; i < questions.size(); ++i) {
                    Font.write(screen, i + 1 +  ". " + questions.get(i).text, 10, 10 + i * (Font.CHAR_HEIGHT + 7), 0xfdeadf);
                }
                
                for(var check : this.qchecks) {
                	check.render(screen);
                }

                this.delete.render(screen, game.input);
            }

            this.back.render(screen, game.input);
        } else {
            var last = (CreateState) game.getState(CREATE_STATE);
            final String uniqueId = last.getID();

            Font.write(screen, uniqueId, ((screen.width - uniqueId.length() * Font.CHAR_WIDTH * 2) >> 1) - 1, 9, 0x132413, 2);
            Font.write(screen, uniqueId, (screen.width - uniqueId.length() * Font.CHAR_WIDTH * 2) >> 1, 7, 0xffffff, 2);

            final String joined = "There are currently: " + players.size() + " players.";
            Font.write(screen, joined, (screen.width - joined.length() * Font.CHAR_WIDTH) >> 1, 30, 0xfdeadf);

            for(int i = 0; i < players.size(); ++i) {
                Font.write(screen, players.get(i).username, 25, 45 + i * 17, 0);
            }

            for(int i = 0; i < checks.size(); ++i) {
                checks.get(i).y = 44 + i * 17;
                checks.get(i).render(screen);
            }

            this.kick.render(screen, game.input);
            this.quit.render(screen, game.input);
            this.start.render(screen, game.input);
        }
    }

    @Override
    public void update(Input input) {
        if(!this.waitingForPlayers) {
            if(!this.viewQuestions) {
                this.add.update(input);
                this.view.update(input);
                this.host.update(input);

                this.cchoice1.update(input);
                this.cchoice2.update(input);
                this.cchoice3.update(input);
                this.cchoice4.update(input);

                this.choice1.update(game.input);
                this.choice2.update(game.input);
                this.choice3.update(game.input);
                this.choice4.update(game.input);
                this.question.update(input);
            } else {
            	for(var check : this.qchecks) {
                	check.update(input);
                }
            	
                this.delete.update(input);
            }

            this.back.update(input);
        } else {
            var last = (CreateState) game.getState(CREATE_STATE);
            this.players = Database.get().getPlayers(last.getID());
            for(int i = 0; i < players.size() - checks.size(); ++i) {
                checks.add(new CheckBox(10, 0));
            }

            for(var check : checks) {
                check.update(input);
            }

            this.kick.update(input);
            this.quit.update(input);
            this.start.update(input);
        }
    }

}
