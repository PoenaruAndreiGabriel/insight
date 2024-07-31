package com.insight.states;

import com.insight.Content;
import com.insight.Game;
import com.insight.Input;
import com.insight.State;
import com.insight.graphics.*;

import java.util.ArrayList;
import java.util.Random;

public class MinesweeperState extends State {
    private class MineTile {
        public enum Cell {
            Empty("./res/minesweeper/e.png"),
            Flag("./res/minesweeper/flag.png"),
            Bomb("./res/minesweeper/bomb.png"),
            Revealed("./res/minesweeper/empty.png");

            private final Bitmap img;

            private Cell(final String path) {
                this.img = Art.load(path);
            }

            public void render(Screen screen, final int x, final int y) {
                screen.blit(this.img, x, y);
            }
        }

        public Cell type;
        public int nbombs;
        public boolean flagged;
        public boolean revealed;

        public MineTile() {
            this.type = Cell.Empty;
            this.flagged = false;
            this.revealed = false;
            this.nbombs = 0;
        }

        public void render(Screen screen, int x, int y) {
            this.type.render(screen, x, y);
            
            if (this.revealed) {
                if (this.nbombs > 0) {
                    Font.write(screen, String.valueOf(this.nbombs), x + 7, y + 6, 0x334521);
                }
            }
        }
    }

    private long startTime;
    private long endTime;
    private int xback = 0;
    private static final int BOARD_SIZE = 7;
    private static final int MINE_COUNT = 6;
    private static final int TILE_SIZE = 20;
    private final MineTile[] board;
    private final ArrayList<MineTile> mineList;
    private int tilesClicked;
    private boolean gameOver, win;
    private Button back;

    public MinesweeperState(Game game) {
        super(game);

        this.board = new MineTile[BOARD_SIZE * BOARD_SIZE];
        this.mineList = new ArrayList<>();
        this.tilesClicked = 0;
        this.gameOver = false;
        this.startTime = System.currentTimeMillis();

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                MineTile tile = new MineTile();
                this.board[r * BOARD_SIZE + c] = tile;
            }
        }

        setMines();

        this.back = new SmallButton((Content.WIDTH - SmallButton.width()) >> 1, Content.HEIGHT - 10 - SmallButton.height(), "Back") {
            @Override
            public void clicked() {
                resetBoard();
                game.setState(State.MINIGAMES_STATE);
            }
        };
    }

    private void setMines() {
        Random random = new Random();
        int minesLeft = MINE_COUNT;
        while (minesLeft > 0) {
            int r = random.nextInt(BOARD_SIZE);
            int c = random.nextInt(BOARD_SIZE);
            MineTile tile = board[r * BOARD_SIZE + c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                minesLeft--;
            }
        }
    }
    
    public void init() {
        startTime = System.currentTimeMillis();
    }

    private void resetBoard() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                MineTile tile = board[r * BOARD_SIZE + c];
                tile.type = MineTile.Cell.Empty;
                tile.revealed = false;
                tile.flagged = false;
            }
        }
        
        mineList.clear();
        setMines();
        tilesClicked = 0;
        gameOver = false;
        win = false;
    }

    private void revealMines() {
        for (MineTile tile : mineList) {
        	tile.type = MineTile.Cell.Bomb;
        }
        
        gameOver = true;
    }

    private void checkMine(int r, int c) {
        if (r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE) {
            return;
        }

        MineTile tile = board[r * BOARD_SIZE + c];
        if (tile.revealed || tile.flagged) {
            return;
        }

        tile.revealed = true;
        tile.type = MineTile.Cell.Revealed;
        tile.nbombs = countAdjacentMines(r, c);
        tilesClicked++;

        if (mineList.contains(tile)) {
        	tile.revealed = false;
            revealMines();
            return;
        }

        int minesFound = countAdjacentMines(r, c);
        if (minesFound == 0) {
            checkAdjacentTiles(r, c);
        }

        if (tilesClicked == BOARD_SIZE * BOARD_SIZE - MINE_COUNT) {
            win = true;
            endTime = System.currentTimeMillis();
        }
    }

    private void checkAdjacentTiles(int r, int c) {
        checkMine(r - 1, c - 1); // top left
        checkMine(r - 1, c);     // top
        checkMine(r - 1, c + 1); // top right
        checkMine(r, c - 1);     // left
        checkMine(r, c + 1);     // right
        checkMine(r + 1, c - 1); // bottom left
        checkMine(r + 1, c);     // bottom
        checkMine(r + 1, c + 1); // bottom right
    }

    private int countAdjacentMines(int r, int c) {
        int minesFound = 0;

        minesFound += countMine(r - 1, c - 1); // top left
        minesFound += countMine(r - 1, c);     // top
        minesFound += countMine(r - 1, c + 1); // top right
        minesFound += countMine(r, c - 1);     // left
        minesFound += countMine(r, c + 1);     // right
        minesFound += countMine(r + 1, c - 1); // bottom left
        minesFound += countMine(r + 1, c);     // bottom
        minesFound += countMine(r + 1, c + 1); // bottom right

        return minesFound;
    }

    private int countMine(int r, int c) {
        if (r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE) {
            return 0;
        }
        
        return mineList.contains(board[r * BOARD_SIZE + c]) ? 1 : 0;
    }

    @Override
    public void render(Screen screen) {
        screen.fill(0, 0, screen.width, screen.height, 0xf6c858);
        screen.blitWrap(Art.back, xback * 1, 0);
        screen.blitWrap(Art.back, 30 + xback + Art.back.width, 0); ++xback;
        int posx = (320 >> 1) - (BOARD_SIZE * TILE_SIZE / 2);
        int posy = (180 >> 1) - (BOARD_SIZE * TILE_SIZE / 2) + 5;

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                MineTile tile = board[r * BOARD_SIZE + c];
                tile.render(screen, posx + c * TILE_SIZE, posy + r * TILE_SIZE - SmallButton.height());
            }
        }

        if (gameOver) {
            final String msgGameOver = "Game Over!";
            Font.write(screen, msgGameOver, (screen.width - msgGameOver.length() * Font.CHAR_WIDTH) >> 1, (screen.height - 39) >> 1, 0xff0000);
        }

        if (win) {
            final String msgWin = "You win!";
            screen.fill(0, 0, screen.width, screen.height, 0xf6c858);
            screen.blitWrap(Art.back, xback * 1, 0);
            screen.blitWrap(Art.back, 30 + xback + Art.back.width, 0); ++xback;
            Font.write(screen, msgWin, (screen.width - msgWin.length() * Font.CHAR_WIDTH) >> 1, (screen.height - 39) >> 1, 0xff7fff);
            long elapsedTime = (endTime - startTime) / 1000; // Convert to seconds
            final String timeMsg = "Time: " + elapsedTime + "s";
            Font.write(screen, timeMsg, (screen.width - timeMsg.length() * Font.CHAR_WIDTH) >> 1, (screen.height - 10) >> 1, 0xfdeadf);
        }

        this.back.render(screen, game.input);
    }

    @Override
    public void update(Input input) {
        this.back.update(input);
        
        if(!gameOver) {
            final int wd = BOARD_SIZE * TILE_SIZE;
        	final int sx = (Content.WIDTH - wd) >> 1;
        	final int sy = ((Content.HEIGHT - wd) >> 1) + 5 - SmallButton.height();
            
            if((input.mx >= sx && input.mx <= (sx + wd)) &&
            		(input.my >= sy && input.my <= (sy + wd))) {

        		final int tx = (int) ((input.mx - sx) / TILE_SIZE);
            	final int ty = (int) ((input.my - sy) / TILE_SIZE);
            	
            	if(input.mouse[0]) {
            		checkMine(ty, tx);
            		input.mouse[0] = false;
            	} else if(input.mouse[1]) {
            		var tile = this.board[ty * BOARD_SIZE + tx];
            		
            		if (!tile.revealed) {
                        tile.flagged = !tile.flagged;
                        tile.type = tile.flagged ? MineTile.Cell.Flag : MineTile.Cell.Empty;
                    }
            		
            		input.mouse[1] = false;
            	}
            }
        }
    }

}
