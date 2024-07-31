package com.insight.states;

import com.insight.Content;
import com.insight.Game;
import com.insight.Input;
import com.insight.State;
import com.insight.graphics.Art;
import com.insight.graphics.Bitmap;
import com.insight.graphics.Button;
import com.insight.graphics.Font;
import com.insight.graphics.Screen;
import com.insight.graphics.SmallButton;

public class XOState extends State {
	private class XOCell {
		public enum XOT {
			X("./res/xo/x.png") {
				@Override
				public XOT next() {
					return O;
				}
			}, 
			
			O("./res/xo/o.png") {
				@Override
				public XOT next() {
					return X;
				}
			}, 
			
			Empty("./res/xo/e.png") {
				@Override
				public XOT next() {
					return X;
				}
			};
			
			private final Bitmap img;
			
			private XOT(final String path) {
				this.img = Art.load(path);
			}
			
			public void render(Screen screen, final int x, final int y, final int color) {
				screen.blit(this.img, x, y, color);
			}
			
			public abstract XOT next();
		}
		
		public XOT type;
		
		public XOCell() {
			this.type = XOT.Empty;
		}
		
		public void render(Screen screen, int x, int y, int turn) {
			this.type.render(screen, x, y, turn == 1 ? 0 : 0xffffff);
		}
	}
	
	private int xback = 0, xturn = 0;
	private final XOCell[] board;
	private XOCell.XOT turn;
	private Button back;

	public XOState(Game game) {
		super(game);
		
		this.board = new XOCell[9];
		for(int i = 0; i < this.board.length; ++i) {
			this.board[i] = new XOCell();
		}
		
		this.back = new SmallButton((Content.WIDTH - SmallButton.width()) >> 1, Content.HEIGHT - 10 - SmallButton.height(), "Back") {
			@Override
			public void clicked() {
				for(int i = 0; i < 9; ++i) 
					board[i].type = XOCell.XOT.Empty;
				
				turn = XOCell.XOT.X;
				over = draw = false;
				xturn = moves = 0;
				
				game.setState(State.MINIGAMES_STATE);
			}
		};
		
		this.turn = XOCell.XOT.X;
	}
	
	double aang = 0.0, bang = 0.0;
	final int w = 10, s = 25;

	@Override
	public void render(Screen screen) {
		screen.fill(0, 0, screen.width, screen.height, 0xf6c858);
		screen.blitWrap(Art.back, xback * 1, 0);
		screen.blitWrap(Art.back, 30 + xback + Art.back.width, 0); ++xback;
		
		if(!over) {
			int posx = (screen.width >> 1) - ((w + s) * 3) / 2;
			int posy = (screen.height >> 1) - ((w + s) * 3) / 2;
			for(int i = 0; i < 3; ++i) {
				for(int j = 0; j < 3; ++j) {
					this.board[j + i * 3].render(screen, posx + j * (w + s), posy + i * (w + s), this.xturn);
				}
			}
			
			final String msg = (this.turn == XOCell.XOT.X) ? "X's turn" : "O's turn";
			Font.write(screen, msg, (screen.width - msg.length() * Font.CHAR_WIDTH) >> 1, 15, this.xturn * 0xffffff);
		} else {
			final String wmsg = draw ? "Draw!" : (this.turn == XOCell.XOT.X ? "X won!" : "O won!");
			final int sx = (screen.width - wmsg.length() * Font.CHAR_WIDTH * 3) >> 1;
			for(int wi = 0; wi < wmsg.length(); ++wi) {
				if(wi % 2 == 0) {
					Font.write(screen, wmsg.charAt(wi), sx + Font.CHAR_WIDTH * wi * 3 + (int) (2 * Math.cos(bang)), 65 + (int) (5 * Math.sin(aang)), 0xfdeadf, 3);
				} else {
					Font.write(screen, wmsg.charAt(wi), sx + Font.CHAR_WIDTH * wi * 3 + (int) (2 * Math.cos(aang)), 65 + (int) (5 * Math.sin(bang)), 0xfdeadf, 3);
				}
			}
			
			aang += 0.05;
			bang -= 0.05;
		}
		
		this.back.render(screen, game.input);
	}
	
	private boolean over = false, draw = false;
	private int moves = 0;

	private void place(final int j, final int i, final XOCell.XOT type) {
		if(this.board[j + i * 3].type == XOCell.XOT.Empty) {
			this.board[j + i * 3].type = this.turn;
			this.xturn = (this.xturn + 1) % 2;
			this.turn = this.turn.next();
			++moves;
		}
		
		int count = 0;
		for(int y = 0; y < 3; y++){
            if(board[j + y * 3].type == type) ++count;
        }
		
		if(count == 3) {
			this.turn = type;
			over = true;
			return;
		}
        
		count = 0;
		for(int x = 0; x < 3; x++){
            if(board[x + i * 3].type == type) ++count;
        }
		
		if(count == 3) {
			this.turn = type;
			over = true;
			return;
		}
        
        if(i == j){
        	count = 0;
            for(int x = 0; x < 3; x++){
                if(board[x * 4].type == type) ++count;
            }
            
            if(count == 3) {
            	this.turn = type;
            	over = true;
            	return;
            }
        }
            
        if(i + j == 2){
        	count = 0;
            for(int x = 0; x < 3; x++){
                if(board[x + (2 - x) * 3].type == type) ++count;
            }
            
            if(count == 3) {
            	this.turn = type;
            	over = true;
            	return;
            }
        }
		
		if(moves == 9) {
			over = true;
			draw = true;
			return;
		}
	}
		
	@Override
	public void update(Input input) {
		this.back.update(input);
		
		if(!over) {
			if(input.mouse[0]) {
				int posx = (Content.WIDTH >> 1) - ((w + s) * 3) / 2;
				int posy = (Content.HEIGHT >> 1) - ((w + s) * 3) / 2;
				
				for(int i = 0; i < 3; ++i) {
					final int y = posy + i * (w + s);
					for(int j = 0; j < 3; ++j) {
						final int x = posx + j * (w + s);
						if(input.mx >= x && input.mx <= (x + 32) &&
								input.my >= y && input.my <= (y + 32)) {
							this.place(j, i, turn);
						}
					}
				}
				
				input.mouse[0] = false;
			}
		}
	}

}