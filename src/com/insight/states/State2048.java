package com.insight.states;

import com.insight.Content;
import com.insight.Game;
import com.insight.Input;
import com.insight.State;
import com.insight.graphics.*;
import com.insight.graphics.Button;
import com.insight.graphics.Font;

import java.awt.event.KeyEvent;
import java.util.Random;

public class State2048 extends State {
    private class Board2048 {
        public enum Cell {
            Empty("./res/xo/e.png");

            private final Bitmap img;

            private Cell(final String path) {
                this.img = Art.load(path);
            }

            public void render(Screen screen, final int x, final int y) {
                screen.blit(this.img, x, y);
            }
        }

        public Cell type;

        public Board2048() {
            this.type = Cell.Empty;
        }

        public void render(Screen screen, int x, int y) {
            this.type.render(screen, x, y);
        }
    }
    private static final int BOARD_SIZE = 4;
    private int xback = 0;
    private final Board2048[] board;
    private int[][] boardNumbers;
    private int score;
    private Button back;

    private static final int COLOR_EMPTY = 0xccc0b3;
    private static final int COLOR_2 = 0xeee4da;
    private static final int COLOR_4 = 0xeee1c9;
    private static final int COLOR_8 = 0xf3b27a;
    private static final int COLOR_16 = 0xf59563;
    private static final int COLOR_32 = 0xf67c5f;
    private static final int COLOR_64 = 0xf65e3b;
    private static final int COLOR_128 = 0xedcf72;
    private static final int COLOR_256 = 0xedcc61;
    private static final int COLOR_512 = 0xedc850;
    private static final int COLOR_1024 = 0xedc53f;
    private static final int COLOR_2048 = 0xedc22e;

    public void addValue() {
        int[] valueCoords = generatePositionForNumber();
        int value = generate2Or4();

        boardNumbers[valueCoords[0]][valueCoords[1]] = value;
    }

    public State2048(Game game) {
        super(game);

        this.board = new Board2048[16];
        for(int i = 0; i < this.board.length; ++i) {
            this.board[i] = new Board2048();
        }

        this.boardNumbers = new int[4][4];
        this.resetBoard();

        this.back = new SmallButton((Content.WIDTH - SmallButton.width()) >> 1, Content.HEIGHT - 10 - SmallButton.height(), "Back") {
            @Override
            public void clicked() {
                resetBoard();
                game.setState(State.MINIGAMES_STATE);
            }
        };
    }

    private void resetBoard() {
        // Initialize the board with 0s (empty cells)
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                this.boardNumbers[i][j] = 0;
            }
        }

        addValue();
        addValue();

        this.score = 0;
        this.gameOver = false;
    }

    public int[] generatePositionForNumber() {
        Random random = new Random();
        int[] coords = new int[2];

        // TODO: adauga fundal celulelor
        // TODO: adauga animatie, sau lasa-i-o lui Fabian

        while (true) {
            // Generates a random number (index) between 0 and 3
            int x = random.nextInt(BOARD_SIZE);
            int y = random.nextInt(BOARD_SIZE);

            if (boardNumbers[x][y] == 0) {
                coords[0] = x;
                coords[1] = y;
                return coords;
            }
        }
    }

    public int generate2Or4() {
        Random random = new Random();
        int randomNumber = random.nextInt(100);
        if (randomNumber < 90) {
            // Generate 2 with 90% probability
            return 2;
        } else {
            // Generate 4 with 10% probability
            return 4;
        }
    }

    private void moveLeft() { // use count to move the tiles to the left one by one
        for (int row = 0; row < BOARD_SIZE; row++) {
            int count = 0;
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (this.boardNumbers[row][col] != 0) {
                    this.boardNumbers[row][0 + count] = this.boardNumbers[row][col];
                    // Empty the original tile position once tile is moved
                    if (0 + count != col) {
                        this.boardNumbers[row][col] = 0;
                    }
                    count++;
                }
            }
        }

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE - 1; col++) {
                if (this.boardNumbers[row][col] == this.boardNumbers[row][col + 1])
                // add the two tiles with the same value
                {
                    this.boardNumbers[row][col] += this.boardNumbers[row][col + 1];
                    this.boardNumbers[row][col + 1] = 0;
                    // update the score ONCE!
                    int sum = this.boardNumbers[row][col];
                    this.score += sum;
                    break;
                }
            }
        }

        // after adding two tiles, repeat the first for loop
        // in this method to move all the tiles to the left side
        for (int row = 0; row < BOARD_SIZE; row++) {
            int count = 0;
            for (int col = 0; col < BOARD_SIZE; col++)

            {
                if (this.boardNumbers[row][col] != 0) {
                    this.boardNumbers[row][0 + count] = this.boardNumbers[row][col];
                    // Empty the original tile position once tile is moved
                    if (0 + count != col) {
                        this.boardNumbers[row][col] = 0;
                    }
                    count++;
                }
            }
        }
    }

    private void moveRight() {
        // Use count to move the tiles to the right one by one
        for (int row = 0; row < BOARD_SIZE; row++) {
            int count = 0;
            for (int col = BOARD_SIZE - 1; col >= 0; col--) {
                if (this.boardNumbers[row][col] != 0) {
                    this.boardNumbers[row][BOARD_SIZE - 1 - count] = this.boardNumbers[row][col];
                    // Empty the original tile position after tile is moved
                    if (BOARD_SIZE - 1 - count != col) {
                        this.boardNumbers[row][col] = 0;
                    }
                    count++;
                }
            }
        }

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = BOARD_SIZE - 1; col > 0; col--) {
                if (this.boardNumbers[row][col] == this.boardNumbers[row][col - 1]) {
                    // Add the two tiles with the same value
                    this.boardNumbers[row][col] += this.boardNumbers[row][col - 1];
                    this.boardNumbers[row][col - 1] = 0;
                    // Update the score ONCE!
                    int sum = this.boardNumbers[row][col];
                    this.score += sum;
                    break;
                }
            }
        }

        // After adding two tiles, repeat the same first for loop
        // in this method to move all the tiles to the right side
        for (int row = 0; row < BOARD_SIZE; row++) {
            int count = 0;
            for (int col = BOARD_SIZE - 1; col >= 0; col--) {
                if (this.boardNumbers[row][col] != 0) {
                    this.boardNumbers[row][BOARD_SIZE - 1 - count] = this.boardNumbers[row][col];
                    if (BOARD_SIZE - 1 - count != col) {
                        this.boardNumbers[row][col] = 0;
                    }
                    count++;
                }
            }
        }
    }

    private void moveUp() {
        // Use count to move the tiles to the top one by one
        for (int col = 0; col < BOARD_SIZE; col++) {
            int count = 0;
            for (int row = 0; row < BOARD_SIZE; row++) {
                if (this.boardNumbers[row][col] != 0) {
                    this.boardNumbers[0 + count][col] = this.boardNumbers[row][col];
                    // Empty the original tile position after tile is moved
                    if (0 + count != row) {
                        this.boardNumbers[row][col] = 0;
                    }
                    count++;
                }
            }
        }

        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = 0; row < BOARD_SIZE - 1; row++) {
                if (this.boardNumbers[row][col] == this.boardNumbers[row + 1][col]) {
                    // Add the two tiles with the same value
                    this.boardNumbers[row][col] += this.boardNumbers[row + 1][col];
                    this.boardNumbers[row + 1][col] = 0;
                    // Update the score ONCE!
                    int sum = this.boardNumbers[row][col];
                    this.score += sum;
                    break;
                }
            }
        }

        // After adding two tiles, repeat the same first for loop
        // in this method to move all the tiles to the top
        for (int col = 0; col < BOARD_SIZE; col++) {
            int count = 0;
            for (int row = 0; row < BOARD_SIZE; row++) {
                if (this.boardNumbers[row][col] != 0) {
                    this.boardNumbers[0 + count][col] = this.boardNumbers[row][col];
                    // Empty the original tile position after tile is moved
                    if (0 + count != row) {
                        this.boardNumbers[row][col] = 0;
                    }
                    count++;
                }
            }
        }
    }

    private void moveDown() {
        // Use count to move the tiles to the bottom one by one
        for (int col = 0; col < BOARD_SIZE; col++) {
            int count = 0;
            for (int row = BOARD_SIZE - 1; row >= 0; row--) {
                if (this.boardNumbers[row][col] != 0) {
                    this.boardNumbers[BOARD_SIZE - 1 - count][col] = this.boardNumbers[row][col];
                    // Empty the original tile position after tile is moved
                    if (BOARD_SIZE - 1 - count != row) {
                        this.boardNumbers[row][col] = 0;
                    }
                    count++;
                }
            }
        }

        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = BOARD_SIZE - 1; row > 0; row--) {
                if (this.boardNumbers[row][col] == this.boardNumbers[row - 1][col]) {
                    // Add the two tiles with the same value
                    this.boardNumbers[row][col] += this.boardNumbers[row - 1][col];
                    this.boardNumbers[row - 1][col] = 0;
                    // Update the score ONCE!
                    int sum = this.boardNumbers[row][col];
                    this.score += sum;
                    break;
                }
            }
        }

        // After adding two tiles, repeat the same first for loop
        // in this method to move all the tiles to the bottom
        for (int col = 0; col < BOARD_SIZE; col++) {
            int count = 0;
            for (int row = BOARD_SIZE - 1; row >= 0; row--) {
                if (this.boardNumbers[row][col] != 0) {
                    this.boardNumbers[BOARD_SIZE - 1 - count][col] = this.boardNumbers[row][col];
                    // Empty the original tile position after tile is moved
                    if (BOARD_SIZE - 1 - count != row) {
                        this.boardNumbers[row][col] = 0;
                    }
                    count++;
                }
            }
        }
    }

    private boolean canMoveLeft() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            // Check if there is zero neighboring a nonzero in the grid; if so,
            // the tiles can move to the left; or if they are two tiles with the same value, it can also move to the left
            for (int col = 0; col < BOARD_SIZE - 1; col++) {
                if (this.boardNumbers[row][col] == 0 && this.boardNumbers[row][col + 1] != 0) {
                    return true;
                }
                if (this.boardNumbers[row][col] != 0 && this.boardNumbers[row][col] == this.boardNumbers[row][col + 1]) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canMoveRight() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            // Check if there is zero neighboring a nonzero in the grid; if so,
            // the tiles can move to the right; or if they are two tiles with the same value, it can also move to the right
            for (int col = BOARD_SIZE - 1; col > 0; col--) {
                if (this.boardNumbers[row][col] == 0 && this.boardNumbers[row][col - 1] != 0) {
                    return true;
                }
                if (this.boardNumbers[row][col] != 0 && this.boardNumbers[row][col] == this.boardNumbers[row][col - 1]) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canMoveUp() {
        for (int col = 0; col < BOARD_SIZE; col++) {
            // Check if there is zero neighboring a nonzero in the grid; if so,
            // the tiles can move up; or if they are two tiles with the same value, it can also move up
            for (int row = 0; row < BOARD_SIZE - 1; row++) {
                if (this.boardNumbers[row][col] == 0 && this.boardNumbers[row + 1][col] != 0) {
                    return true;
                }
                if (this.boardNumbers[row][col] != 0 && this.boardNumbers[row][col] == this.boardNumbers[row + 1][col]) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canMoveDown() {
        for (int col = 0; col < BOARD_SIZE; col++) {
            // Check if there is zero neighboring a nonzero in the grid; if so,
            // the tiles can move down; or if they are two tiles with the same value, it can also move down
            for (int row = BOARD_SIZE - 1; row > 0; row--) {
                if (this.boardNumbers[row][col] == 0 && this.boardNumbers[row - 1][col] != 0) {
                    return true;
                }
                if (this.boardNumbers[row][col] != 0 && this.boardNumbers[row][col] == this.boardNumbers[row - 1][col]) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isGameOver() {
        //check if there is no long any valid move left, then the game
        //is over.
        if(!this.canMoveLeft()&&!this.canMoveRight()&&!this.canMoveUp()
                &&!this.canMoveDown()) {
            return gameOver = true;
        }

        //return false when the game continues
        return gameOver = false;
    }

    final int w = 10, s = 25;
    private boolean gameOver = false;

    public int[] determineTileColors(int tileValue) {

        int tileColor;
        int valueColor;
        int[] colors = new int[2];

        if (tileValue == 2 || tileValue == 4) {
            valueColor = 0x776e65;
        } else {
            valueColor = 0xf9f6f2;
        }

        switch (tileValue) {
            case 2:
                tileColor = COLOR_2;
                break;
            case 4:
                tileColor = COLOR_4;
                break;
            case 8:
                tileColor = COLOR_8;
                break;
            case 16:
                tileColor = COLOR_16;
                break;
            case 32:
                tileColor = COLOR_32;
                break;
            case 64:
                tileColor = COLOR_64;
                break;
            case 128:
                tileColor = COLOR_128;
                break;
            case 256:
                tileColor = COLOR_256;
                break;
            case 512:
                tileColor = COLOR_512;
                break;
            case 1024:
                tileColor = COLOR_1024;
                break;
            case 2048:
                tileColor = COLOR_2048;
                break;
            default:
                tileColor = COLOR_EMPTY;
                break;
        }

        colors[0] = tileColor;
        colors[1] = valueColor;

        return colors;
    }


    @Override
    public void render(Screen screen) {
        screen.fill(0, 0, screen.width, screen.height, 0xf6c858);
        screen.blitWrap(Art.back, xback * 1, 0);
        screen.blitWrap(Art.back, 30 + xback + Art.back.width, 0); ++xback;

        if (gameOver) {
            screen.fill(0, 0, screen.width, screen.height, 0xf6c858);
            final String msgGameOver = "Game Over!";
            final String msgScore = "Score: " + score;
            Font.write(screen, msgGameOver, ((screen.width - (msgGameOver.length()) * Font.CHAR_WIDTH) >> 1), ((screen.height - 39) >> 1), 0x8fffff);
            Font.write(screen, msgScore, ((screen.width - msgScore.length() * Font.CHAR_WIDTH) >> 1), ((screen.height - 9) >> 1), 0xff7fff);
        } else {
            int posx = (screen.width >> 1) - ((w + s) * BOARD_SIZE) / 2;
            int posy = (screen.height >> 1) - ((w + s) * BOARD_SIZE) / 2 - 10;
            for (int i = 0; i < BOARD_SIZE; ++i) {
                for (int j = 0; j < BOARD_SIZE; ++j) {
                    // Calculate the position to center the text
                    int textWidth = Font.CHAR_WIDTH * String.valueOf(boardNumbers[i][j]).length();
                    int textHeight = Font.CHAR_HEIGHT;
                    int cellCenterX = posx + j * (w + s);
                    int cellCenterY = posy + i * (w + s);
                    int textX = cellCenterX - (textWidth / 2) + 16;
                    int textY = cellCenterY - (textHeight / 2) + 16;

                    this.board[j + i * BOARD_SIZE].render(screen, posx + j * (w + s), posy + i * (w + s));

                    // Choose color based on tile value
                    int tileColor, valueColor;
                    int[] colors;
                    int tileValue = boardNumbers[i][j];

                    colors = determineTileColors(tileValue);
                    tileColor = colors[0];
                    valueColor = colors[1];

                    screen.fill(cellCenterX + 1, cellCenterY + 1, 30, 30, tileColor);
                    if (tileValue != 0) {
                        Font.write(screen, String.valueOf(tileValue), textX, textY, valueColor);
                    }
                }
            }
        }

        this.back.render(screen, game.input);
    }

    // Define a flag to track if the arrow key was just pressed
    private boolean leftArrowKeyPressed = false;
    private boolean rightArrowKeyPressed = false;
    private boolean upArrowKeyPressed = false;
    private boolean downArrowKeyPressed = false;

    public void gameOverOrAddValue() {
        if (!isGameOver()) {
            addValue();
        }
    }

    @Override
    public void update(Input input) {
        // Check if the arrow keys are pressed
        if(input.keys[KeyEvent.VK_LEFT] && !leftArrowKeyPressed) {
            if (canMoveLeft()) {
                moveLeft();
                gameOverOrAddValue();
            }
            isGameOver();
            leftArrowKeyPressed = true; // Set the flag to true indicating the key is pressed
        } else if(input.keys[KeyEvent.VK_RIGHT] && !rightArrowKeyPressed) {
            if (canMoveRight()) {
                moveRight();
                gameOverOrAddValue();
            }
            isGameOver();
            rightArrowKeyPressed = true;
        } else if(input.keys[KeyEvent.VK_UP] && !upArrowKeyPressed) {
            if (canMoveUp()) {
                moveUp();
                gameOverOrAddValue();
            }
            isGameOver();
            upArrowKeyPressed = true;
        } else if(input.keys[KeyEvent.VK_DOWN] && !downArrowKeyPressed) {
            if (canMoveDown()) {
                moveDown();
                gameOverOrAddValue();
            }
            isGameOver();
            downArrowKeyPressed = true;
        }

        // Check if the arrow keys are released
        if (!input.keys[KeyEvent.VK_LEFT]) {
            leftArrowKeyPressed = false; // Reset the flag if the key is released
        }
        if (!input.keys[KeyEvent.VK_RIGHT]) {
            rightArrowKeyPressed = false;
        }
        if (!input.keys[KeyEvent.VK_UP]) {
            upArrowKeyPressed = false;
        }
        if (!input.keys[KeyEvent.VK_DOWN]) {
            downArrowKeyPressed = false;
        }

        this.back.update(input);
    }
}

// Sursa functii joc 2048: https://github.com/daniel-huang-1230/Game-2048/blob/master/Board.java
// Din link-ul de mai sus au fost folosite urmatoarele functii:
//      -> moveLeft, moveRight, moveUp, moveDown
//      -> canMoveLeft, canMoveRight, canMoveUp, canMoveDown
//      -> isGameOver