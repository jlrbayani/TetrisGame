package main.model;

import main.ui.SoundSystem;
import main.ui.TetrisFrame;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

// Game controls everything about the gameplay of Tetris. The game loop resides here which updates and renders the game.
public class Game implements Runnable{
    private final double MS_PER_UPDATE = 16.5;
    public static final int GAME_COLS = 10;
    public static final int GAME_ROWS = 21;
    public static  final int NEXT_COLS = 4;
    public static  final int NEXT_ROWS = 16;
    public static  final int HOLD_COLS = 4;
    public static  final int HOLD_ROWS = 4;

    public static final int FAST_DROP_PER_CELL = 2;
    public static final int SLOW_DROP_PER_CELL = 1;
    public static final int INIT_CLEAR = 0;
    public static final int LINE_CLEAR = 100;
    public static final int TETRIS = 500;
    public static final int BACK_TO_BACK_TETRIS = 1200;

    public static final int LEVEL_1 = 60;
    public static final int LEVEL_20 = 3;

    private boolean keepRunning;
    private boolean isOver;
    private boolean isPaused;
    private boolean isQuit;
    private TetrisFrame frame;
    private Thread gameThread;
    private CountDownTimer cdt;
    private SoundSystem ss;

    private int currentGameSpeed;
    private Board holdBoard, gameBoard, nextBoard;
    private ScoreMultiplier scoreMultiplier;
    private Score score;
    private Level level;
    private Lines lines;
    private boolean canSwap;

    private TetrisPiece pieceInPlay;
    private TetrisPiece heldPiece;
    private LinkedList<TetrisPiece> nextPieces;

    private boolean[] keysHeldDown, keysSinglePress;
    private int[] keysNumCall;
    private ArrayList<Entity> gameEntities;
    private Sound blockPlace, theme, rotate;

    private int numFall, processInput, incrementalUpdate, numTries;
    private int rowsCleared, currentLevel, softPoints, fastPoints, previousClear;
    private double currentMultiplier;

    // constructor for Game which initializes all starting entities and variables required to start a game
    // has a reference to the original TetrisFrame
    public Game(TetrisFrame frame) {
        this.frame = frame;
        this.ss = frame.getSoundSystem();
        isOver = false;
        currentGameSpeed = LEVEL_1;
        isPaused = false;
        canSwap = true;
        numFall = 1;
        incrementalUpdate = 1;
        processInput = 0;
        numTries = 0;
        previousClear = INIT_CLEAR;
        isQuit = false;

        // game stats initialization
        rowsCleared = 0;
        currentMultiplier = 1;
        currentLevel = 1;
        softPoints = 0;
        fastPoints = 0;


        // game entities initialization
        gameEntities = new ArrayList<>();
        heldPiece = null;
        pieceInPlay = null;
        initStartingEntities();
        initSounds();

        // initializes user input
        keysHeldDown = new boolean[3];
        keysSinglePress = new boolean[3];
        keysNumCall = new int[3];
        Arrays.fill(keysSinglePress, true);
    }

    // MODIFIES: this
    // EFFECTS: initializes all the sounds for the game
    private void initSounds() {
        theme = new Sound("/sounds/TetrisTheme.wav", "theme", ss.getCurrentVolume());
        theme.setKeepLooping(true);
        ss.addToSounds(theme);

        blockPlace = new Sound("/sounds/BlockPlacementSound.wav", "blockPlaced", ss.getCurrentVolume());
        ss.addToSounds(blockPlace);

        // rotate click found from https://mixkit.co/
        rotate = new Sound("/sounds/rotateClick.wav", "rotate", ss.getCurrentVolume());
        ss.addToSounds(rotate);
    }


    public boolean isPaused() {
        return isPaused;
    }

    // MODIFIES: this
    // EFFECTS: sets the extrapolation for all entities in gameEntities with extrapolate
    public synchronized void setExtrapolation(double extrapolate) {
        for (Entity e: gameEntities) {
            e.setExtrapolation(extrapolate);
        }
    }

    // MODIFIES: this
    // EFFECTS: updates the different entities in the game and checks for any pauses, userInputs and other changes needed to keep track of the game state
    public synchronized void update() {
        if (gameEntities.contains(cdt) && cdt.getIsFinished() && isPaused) {
            resumeGame();
            isPaused = false;
        }

        for (Entity e: gameEntities) {
            e.update();
        }


        if (isOver) {
            score.setCurrentScore(score.getScoreNow());
            updateGameOver();
        } else {
            processInput();
            updateHeldPiece();
            updatePieceInPlay();
            updateNextPieces();
            if (pieceInPlay == null) {
                getNewPieceInPlay();
            }
            addToLines();
            calculateScore();
            updateGameSpeed();
        }

    }

    // MODIFIES: this
    // EFFECTS: updates the screen when the game is in the state of gameOver
    public synchronized void updateGameOver() {
        if (!gameBoard.boardContainsBlock()) {
            endGame();

        }

        if (incrementalUpdate % 3 == 0) {
            gameBoard.removeFirstBlock();
        }
        incrementalUpdate++;
        if (incrementalUpdate > 12000) {
            incrementalUpdate = 0;
        }

    }

    // MODIFIES: this
    // EFFECTS: pauses the game state and pauses all entities and sounds in the game
    public synchronized void pauseGame() {
        if (gameEntities.contains(cdt) && cdt.getIsFinished()) {
            gameEntities.remove(cdt);
        }

        for (Entity e: gameEntities) {
            e.pause();
        }

        isPaused = true;
        ss.pauseAllSounds();
    }

    // MODIFIES: this
    // EFFECTS: resumes everything in the game from the last paused state
    public synchronized void resumeGame() {
        for (Entity e: gameEntities) {
            e.resume();
        }
        ss.resetSounds();
        ss.resumeFromPause();
        ss.playSound(theme.getSoundName());
        frame.getCurrentPanel().requestFocusInWindow();
    }

    // MODIFIES: this
    // EFFECTS: starts the countdown from 3 to 1 after the starting or resuming of a game
    public synchronized void startCountDown() {
        if (!gameEntities.contains(cdt)) {
            cdt = new CountDownTimer(440, TetrisFrame.HEIGHT / 5);
            gameEntities.add(cdt);
        }
        isPaused = true;
    }

    // MODIFIES: this
    // EFFECTS: starts the game thread and kicks off the game loop
    public synchronized void startGame() {
        this.keepRunning = true;
        gameThread = new Thread(this, "Game");
        gameThread.start();
        pauseGame();
        startCountDown();
    }

    // MODIFIES: this
    // EFFECTS: ends the game and resets all the sounds
    public synchronized void endGame() {
        keepRunning = false;
        ss.resetSounds();
        ss.clearAllSounds();
    }

    // MODIFIES: this
    // EFFECTS: this allows the player to quit the game at the pause state or when the game is over
    public synchronized void quitGame() {
        isQuit = true;
        endGame();
    }

    public synchronized void startGameOverSequence() {
        isOver = true;
    }

    // MODIFIES: this
    // EFFECTS: initializes the starting game entities
    private synchronized void initStartingEntities() {
        gameBoard = new Board(GAME_COLS, GAME_ROWS, 310, 20, 1, 0);
        gameEntities.add(gameBoard);

        nextBoard = new Board(NEXT_COLS, NEXT_ROWS, 750, 100, 1, 0);
        gameEntities.add(nextBoard);
        nextPieces = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            nextPieces.add(new TetrisPiece());
        }

        holdBoard = new Board(HOLD_COLS, HOLD_ROWS, 50, 100, 1, 0);
        gameEntities.add(holdBoard);

        score = new Score(45, 300);
        gameEntities.add(score);

        lines = new Lines(45, 400);
        gameEntities.add(lines);

        level = new Level(45, 500);
        gameEntities.add(level);

        scoreMultiplier = new ScoreMultiplier(288, 640);
        gameEntities.add(scoreMultiplier);

    }

    // MODIFIES: this
    // EFFECTS: gets a new pieceInPlay and checks if it is valid to be added to gameBoard
    public void getNewPieceInPlay() {
        TetrisPiece newPiece = nextPieces.remove();
        nextPieces.add(new TetrisPiece());
        pieceInPlay = newPiece;
        numTries = 0;
        if (!gameBoard.initPieceToBoard(pieceInPlay)) {
            startGameOverSequence();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates the heldPiece and the holdBoard
    private void updateHeldPiece() {
        if (heldPiece != null) {
            if (heldPiece.getType() != TetrisPiece.Type.L && heldPiece.getType() != TetrisPiece.Type.I && heldPiece.getType() != TetrisPiece.Type.T) {
                holdBoard.setPieceCol(1);
            } else {
                holdBoard.setPieceCol(0);
            }
            if (heldPiece.getType() != TetrisPiece.Type.I) {
                holdBoard.setPieceRow(1);
            }
            holdBoard.addTetrisPiece(heldPiece);

        }
    }

    // MODIFIES: this
    // EFFECTS: updates the pieceInPlay and the gameBoard
    private void updatePieceInPlay() {

        if (pieceInPlay != null) {
            if (!isPaused) {
                gameBoard.updateGhostCells(pieceInPlay);
                if (numFall % currentGameSpeed == 0) {
                    // numTries allows the player to rotate and move as much as they can for a short time period
                    if (!gameBoard.movePieceDown(pieceInPlay) || numTries > 0) {
                        if (numTries >= 2) {
                            if (!gameBoard.movePieceDown(pieceInPlay)) {
                                lockPieceInPlay();
                            } else {
                                numTries = 0;
                            }
                        } else {
                            numTries++;
                        }
                    }
                }

                numFall++;

                // ensures that numFall doesn't overflow
                if (numFall > 12000) {
                    numFall = 0;
                }
            }

        }
    }

    // MODIFIES: this
    // EFFECTS: updates the next pieces coming into nextBoard
    private void updateNextPieces() {
        if (nextPieces.size() < 3) {
            return;
        }

        nextBoard.clearBoard();
        for (int i = 0; i < 3; i++) {
            TetrisPiece nextPiece = nextPieces.get(i);
            if (nextPiece.getType() != TetrisPiece.Type.L && nextPiece.getType() != TetrisPiece.Type.I && nextPiece.getType() != TetrisPiece.Type.T) {
                nextBoard.setPieceCol(1);
            }
            nextBoard.addTetrisPiece(nextPiece);
            nextBoard.shiftPieceRow(5);
            nextBoard.setPieceCol(0);
        }
        nextBoard.setPieceRow(1);

    }

    public synchronized ArrayList<Entity> getGameEntities() {
        return gameEntities;
    }

    public boolean[] getKeysHeldDown() {
        return keysHeldDown;
    }

    public boolean[] getKeysSinglePress() {
        return keysSinglePress;
    }

    public int[] getKeysNumCall() {
        return keysNumCall;
    }

    // MODIFIES: this
    // EFFECTS: given the arrays (keysNumCall and keysHeldDown), this processes input and correctly updates these arrays
    // this handles input that can be held down
    // this includes moving pieceInPlay left, right, or down
    public void processInput() {
        if (isPaused) {
            return;
        }

        while (keysNumCall[0] > 0) {
            if (gameBoard.movePieceSide(pieceInPlay, -1)) {
                playRotateClick();
            }
            if (pieceInPlay != null) {
                if (pieceInPlay.getLeftEdgeCol() == 0) {
                    gameBoard.setCanMoveLeft(true);
                }
            }
            keysNumCall[0]--;
        }
        while (keysNumCall[1] > 0) {
            if (gameBoard.movePieceSide(pieceInPlay, 1)) {
                playRotateClick();
            }
            if (pieceInPlay != null) {
                if (pieceInPlay.getRightEdgeCol() == gameBoard.getNumCols() - 1) {
                    gameBoard.setCanMoveRight(true);
                }
            }

            keysNumCall[1]--;
        }
        while (keysNumCall[2] > 0) {
            softPoints += SLOW_DROP_PER_CELL;
            if (gameBoard.movePieceDown(pieceInPlay)) {
                playRotateClick();
            } else {
                lockPieceInPlay();
            }
            keysNumCall[2]--;
        }

        processInput++;
        if (processInput % currentGameSpeed != 0) {
            return;
        }

        if (keysHeldDown[0]) {
            keysNumCall[0]++;
        }

        if (keysHeldDown[1]) {
            keysNumCall[1]++;
        }

        if (keysHeldDown[2]) {
            keysNumCall[2]++;
        }

        if (processInput > 12000) {
            processInput = 0;
        }
    }

    // MODIFIES: this
    // EFFECTS: this handles user input where you usually only press it once
    // - pausing the game, fast drop, rotating left or right, holding or swapping a piece
    public synchronized void keyPressed(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_ESCAPE:
                if (!isPaused) {
                    pauseGame();
                    frame.showPausedPanel();
                }
                break;
            case KeyEvent.VK_SPACE:
                if (keysSinglePress[0] && pieceInPlay != null) {
                    int numCellsDropped = gameBoard.fastDropPiece(pieceInPlay);
                    fastPoints += numCellsDropped * FAST_DROP_PER_CELL;
                    scoreMultiplier.addToMultiplierHeight(100);
                    gameBoard.setCanMoveVertically(true);
                    lockPieceInPlay();
                    keysSinglePress[0] = false;
                }
                break;
            case KeyEvent.VK_UP:
                if (keysSinglePress[1] && pieceInPlay != null) {
                    if (gameBoard.rotatePieceInPlay(pieceInPlay, true)) {
                        playRotateClick();
                    }
                    keysSinglePress[1] = false;
                }
                break;
            case KeyEvent.VK_Z:
                if (keysSinglePress[2] && pieceInPlay != null) {
                    if (gameBoard.rotatePieceInPlay(pieceInPlay, false)) {
                        playRotateClick();
                    }
                    keysSinglePress[2] = false;
                }
                break;
            case KeyEvent.VK_C:
                if (canSwap && pieceInPlay != null) {
                    gameBoard.clearCells(pieceInPlay.getActualMatrix());
                    pieceInPlay.resetRotation();
                    gameBoard.setPieceRow(1);
                    if (heldPiece == null) {
                        heldPiece = pieceInPlay;
                        getNewPieceInPlay();
                    } else {
                        TetrisPiece swap = heldPiece;
                        heldPiece = pieceInPlay;
                        pieceInPlay = swap;
                        gameBoard.initPieceToBoard(pieceInPlay);
                    }
                    holdBoard.clearBoard();
                    canSwap = false;
                }
                break;
        }

    }

    // EFFECTS: plays the sound rotating and moving the pieceInPlay
    private void playRotateClick() {
        ss.resetSound(rotate);
        ss.playSound(rotate.getSoundName());
    }

    // MODIFIES: this
    // EFFECTS: when a piece is placed, it allows canSwap to be true and uses the sound for blockPlace
    public void piecePlaced() {
        canSwap = true;
        ss.resetSound(blockPlace);
        ss.playSound(blockPlace.getSoundName());
    }

    // MODIFIES: this
    // EFFECTS: this ensures that the pieceInPlay is seen as successful and various visual aspects run to show to the user that it their action was complete (such as the light fade effect(
    private synchronized void lockPieceInPlay() {
        rowsCleared = gameBoard.getLineClear();
        gameBoard.applyLightFade(pieceInPlay);
        piecePlaced();
        pieceInPlay = null;
        gameBoard.setPieceRow(1);
    }

    // MODIFIES: this
    // EFFECTS: calculates the current score depending on numLines cleared, level, and scoreMultiplier
    private void calculateScore() {
        currentMultiplier = scoreMultiplier.getCurrentMultiplier();
        int linePoints = 0;
        int heightAdded = 0;

        if (rowsCleared == 4 && (previousClear == TETRIS || previousClear == BACK_TO_BACK_TETRIS)) {
            linePoints += BACK_TO_BACK_TETRIS;
            heightAdded = 400;
            previousClear = BACK_TO_BACK_TETRIS;
        } else if (rowsCleared == 4 && (previousClear == LINE_CLEAR || previousClear == INIT_CLEAR)) {
            linePoints += TETRIS;
            heightAdded = 200;
            previousClear = TETRIS;
        } else if (rowsCleared != 0){
            linePoints += LINE_CLEAR * rowsCleared;
            heightAdded = 100;
            previousClear = LINE_CLEAR;
        }

        scoreMultiplier.addToMultiplierHeight(heightAdded);

        int totalScore = (int) ((softPoints + fastPoints + linePoints) * currentMultiplier);
        score.addToScore(totalScore);
        softPoints -= softPoints;
        fastPoints -= fastPoints;
        rowsCleared = 0;

    }

    // MODIFIES: this
    // EFFECTS: updates the game speed depending on the number of lines cleared, level increases every 10 lines cleared
    private void updateGameSpeed() {
        currentLevel = level.getCurrentLevel();
        if (currentLevel == 20) {
            currentGameSpeed = LEVEL_20;
        } else {
            currentGameSpeed = LEVEL_1 - ((currentLevel - 1) * 3);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds to lines depending on the rowsCleared
    private void addToLines() {
        lines.addLines(rowsCleared);
        currentLevel = level.getCurrentLevel();
        if (lines.getNewNumLines() - (currentLevel * 10) >= 0) {
            level.increaseLevel();
            currentLevel = level.getCurrentLevel();
        }
    }

    // MODIFIES: frame
    // EFFECTS: shows a new panel that is the gameOverPanel for the user to interact with, denotes finishing a game as well
    private void showGameOverScreen() {
        frame.setTitle("Tetris");
        frame.showGameOverPanel(score);
    }


    // MODIFIES: this
    // EFFECTS: covers the main game loop
    // throughout a game's lifetime, all of the gameplay is ran through here
    // there is 60 updates per second
    // rendering is tied to the rendering of gamePanel which the EDT decides when to show to the screen/frame
    // the game thread is killed after the loop finishes running
    @Override
    public void run() {
        double previous = System.currentTimeMillis();
        double timer = System.currentTimeMillis();
        double lag = 0;
        int numLoopRuns = 0;
        int updates = 0;

        while (keepRunning) {
            double current = System.currentTimeMillis();
            double elapsed = current - previous;
            previous = current;
            lag += elapsed;

            while (lag >= MS_PER_UPDATE) {
                setExtrapolation(lag / MS_PER_UPDATE);
                update();
                updates++;
                lag -= MS_PER_UPDATE;
            }

            numLoopRuns++;

            while (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frame.setTitle("Tetris | updates: " + updates + " | loopRuns: " + numLoopRuns);
                updates = 0;
                numLoopRuns = 0;
            }

        }

        if (!isQuit) {
            showGameOverScreen();
        }

        try {
            this.gameThread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}
