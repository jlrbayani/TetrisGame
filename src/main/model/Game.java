package main.model;

import main.ui.SoundSystem;
import main.ui.TetrisFrame;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

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
    private boolean canSwap, addToNumTries;

    private TetrisPiece pieceInPlay;
    private TetrisPiece heldPiece;
    private LinkedList<TetrisPiece> nextPieces;

    private boolean[] keysHeldDown, keysSinglePress;
    private int[] keysNumCall;
    private ArrayList<Entity> entityList;
    private Sound blockPlace, theme;

    private int numFall, processInput, incrementalUpdate, numTries;
    private int rowsCleared, currentLevel, softPoints, fastPoints, currentMultiplierMeter, previousClear;
    private double currentMultiplier;

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
        addToNumTries = false;
        previousClear = INIT_CLEAR;

        // game stats initialization
        rowsCleared = 0;
        currentMultiplier = 1;
        currentLevel = 1;
        softPoints = 0;
        fastPoints = 0;
        currentMultiplierMeter = 1;

        // game entities initialization
        entityList = new ArrayList<>();
        heldPiece = null;
        pieceInPlay = null;
        initStartingEntities();
        initSounds();

        keysHeldDown = new boolean[3];
        keysSinglePress = new boolean[3];
        keysNumCall = new int[3];
        Arrays.fill(keysSinglePress, true);
    }

    private void initSounds() {
        theme = new Sound("resources/sounds/TetrisTheme.wav", "theme", ss.getCurrentVolume());
        theme.setKeepLooping(true);
        ss.addToSounds(theme);

        blockPlace = new Sound("resources/sounds/BlockPlacementSound.wav", "blockPlaced", ss.getCurrentVolume());
        ss.addToSounds(blockPlace);
    }


    public boolean isPaused() {
        return isPaused;
    }

    public synchronized void setExtrapolation(double extrapolate) {
        for (Entity e: entityList) {
            e.setExtrapolation(extrapolate);
        }
    }

    // TODO: implement gameOver Screen
    public synchronized void update() {
        if (entityList.contains(cdt) && cdt.getIsFinished() && isPaused) {
            resumeGame();
            isPaused = false;
        }

        for (Entity e: entityList) {
            e.update();
        }

        if (isOver) {
            score.setCurrentScore(score.getScoreNow());
            updateGameOver();
            return;
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

       for (Entity e: entityList) {
           e.update();
       }


    }

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

    public synchronized void pauseGame() {
        if (entityList.contains(cdt) && cdt.getIsFinished()) {
            entityList.remove(cdt);
        }

        for (Entity e: entityList) {
            e.pause();
        }
        System.out.println("Game Paused!");
        isPaused = true;
        ss.pauseAllSounds();
    }

    public synchronized void resumeGame() {
        for (Entity e: entityList) {
            e.resume();
        }
        System.out.println("Resuming game!");
        ss.resetSounds();
        ss.resumeFromPause();
//        ss.playSound(theme.getSoundName());
        frame.getCurrentPanel().requestFocusInWindow();
    }

    public synchronized void startCountDown() {
        if (!entityList.contains(cdt)) {
            cdt = new CountDownTimer(440, TetrisFrame.HEIGHT / 5);
            entityList.add(cdt);
        }
        isPaused = true;
    }

    public synchronized void startGame() {
        this.keepRunning = true;
        gameThread = new Thread(this, "Game");
        gameThread.start();
        pauseGame();
        startCountDown();
    }

    public synchronized void endGame() {
        keepRunning = false;
    }

    public synchronized void showGameOver() {
        isOver = true;

//        endGame();
    }

    private synchronized void initStartingEntities() {
        gameBoard = new Board(GAME_COLS, GAME_ROWS, 310, 20, 1, 0);
        entityList.add(gameBoard);

        nextBoard = new Board(NEXT_COLS, NEXT_ROWS, 750, 100, 1, 0);
        entityList.add(nextBoard);
        nextPieces = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            nextPieces.add(new TetrisPiece());
        }

        holdBoard = new Board(HOLD_COLS, HOLD_ROWS, 50, 100, 1, 0);
        entityList.add(holdBoard);

        score = new Score(45, 300);
        entityList.add(score);

        lines = new Lines(45, 400);
        entityList.add(lines);

        level = new Level(45, 500);
        entityList.add(level);

        scoreMultiplier = new ScoreMultiplier(288, 640);
        entityList.add(scoreMultiplier);

    }

    public void getNewPieceInPlay() {
        TetrisPiece newPiece = nextPieces.remove();
        nextPieces.add(new TetrisPiece());
        pieceInPlay = newPiece;
        numTries = 0;
        // gameBoard.validateStartingPieceCol(pieceInPlay);
//        gameBoard.setPieceCol((int) (pieceInPlay.getDimensions().getWidth()));
        if (!gameBoard.initPieceToBoard(pieceInPlay)) {
//            System.out.println(numTries);
            showGameOver();
        }
    }

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

    private synchronized void updatePieceInPlay() {

        if (pieceInPlay != null) {
//            for (Cell c: pieceInPlay.getActualMatrix()) {
//                System.out.println("Row: " + c.getRowPos() + " Col: " + c.getColPos());
//            }
            //System.out.println(pieceInPlay.getActualMatrix().size());


//            gameBoard.clearCells(pieceInPlay.getActualMatrix());
//            gameBoard.addTetrisPiece(pieceInPlay);

//            gameBoard.addPieceToBoard(pieceInPlay, pieceInPlay.getActualMatrix());
            if (!isPaused) {
                //gameBoard.updatePieceInBoard(pieceInPlay);
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

                if (numFall > 12000) {
                    numFall = 0;
                }
            }

        }
    }

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

    public synchronized ArrayList<Entity> getEntityList() {
        return entityList;
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

    public synchronized void processInput() {
        if (isPaused) {
            return;
        }

        while (keysNumCall[0] > 0) {
//            System.out.println("Move Left!");
//            gameBoard.shiftPieceCol(-1, pieceInPlay);
            gameBoard.movePieceSide(pieceInPlay, -1);
            if (pieceInPlay != null) {
                if (pieceInPlay.getLeftEdgeCol() == 0) {
                    gameBoard.setCanMoveLeft(true);
                }
            }
            keysNumCall[0]--;
        }
        while (keysNumCall[1] > 0) {
//            System.out.println("Move Right!");
//            gameBoard.shiftPieceCol(1, pieceInPlay);
            gameBoard.movePieceSide(pieceInPlay, 1);
            if (pieceInPlay != null) {
                if (pieceInPlay.getRightEdgeCol() == gameBoard.getNumCols() - 1) {
                    gameBoard.setCanMoveRight(true);
                }
            }

            keysNumCall[1]--;
        }
        while (keysNumCall[2] > 0) {
//            System.out.println("Soft Drop!");
//            if (!gameBoard.shiftPieceRow(1, pieceInPlay)) {
//                lockPieceInPlay();
//            }
            softPoints += SLOW_DROP_PER_CELL;
            if (!gameBoard.movePieceDown(pieceInPlay)) {
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

//        if (keysHeldDown[0] && pieceInPlay != null) {
//            System.out.println("Move Left");
//            gameBoard.shiftPieceCol(-1);
//            //pieceInPlay.moveLeft();
//        }
//
//        if (keysHeldDown[1] && pieceInPlay != null) {
//            System.out.println("Move Right!");
//            gameBoard.shiftPieceCol(1);
//            //pieceInPlay.moveRight(gameBoard);
//        }
//
//        if (keysHeldDown[2] && pieceInPlay != null) {
//            System.out.println("Soft Drop!");
//            gameBoard.shiftPieceRow(1);
//            //pieceInPlay.softDrop();
//        }
        if (processInput > 12000) {
            processInput = 0;
        }
    }

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
                    gameBoard.rotatePieceInPlay(pieceInPlay, true);
                    keysSinglePress[1] = false;
                }
                break;
            case KeyEvent.VK_Z:
                if (keysSinglePress[2] && pieceInPlay != null) {
                    gameBoard.rotatePieceInPlay(pieceInPlay, false);
                    keysSinglePress[2] = false;
                }
                break;
            case KeyEvent.VK_C:
                if (canSwap && pieceInPlay != null) {
//                    System.out.println("Swapping!");
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

        processInput();
    }

    public void piecePlaced() {
        canSwap = true;
//        ss.playSound(blockPlace.getSoundName());
//        ss.resetSound(blockPlace);
    }

    private synchronized void lockPieceInPlay() {
        rowsCleared = gameBoard.getLineClear();
        gameBoard.applyLightFade(pieceInPlay);
        piecePlaced();
        pieceInPlay = null;
        gameBoard.setPieceRow(1);
    }

    private synchronized void calculateScore() {
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

//        if (linePoints != 0) {
//            System.out.println("");
//            System.out.println("linePoints:    " + linePoints);
//            System.out.println("rowsCleared:   " + rowsCleared);
//            System.out.println("previousClear: " + previousClear);
//            System.out.println("heightAdded:   " + heightAdded);
//        }

        int totalScore = (int) ((softPoints + fastPoints + linePoints) * currentMultiplier);
        score.addToScore(totalScore);
        softPoints -= softPoints;
        fastPoints -= fastPoints;
        rowsCleared = 0;

    }

    private synchronized void updateGameSpeed() {
        currentLevel = level.getCurrentLevel();
        if (currentLevel == 20) {
            currentGameSpeed = LEVEL_20;
        } else {
            currentGameSpeed = LEVEL_1 - ((currentLevel - 1) * 3);
        }
    }

    private synchronized void addToLines() {
        lines.addLines(rowsCleared);
        currentLevel = level.getCurrentLevel();
        if (lines.getNewNumLines() - (currentLevel * 10) >= 0) {
            level.increaseLevel();
            currentLevel = level.getCurrentLevel();
        }
    }

    private void showGameOverScreen() {
        frame.setTitle("Tetris");
    }


    // covers the main game loop
    @Override
    public void run() {
        // in the future, might use nanoTime instead of currentTimeMillis
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


            //frame.getCurrentPanel().repaint();
            numLoopRuns++;

            while (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frame.setTitle("Tetris | updates: " + updates + " | loopRuns: " + numLoopRuns);
                updates = 0;
                numLoopRuns = 0;
            }

        }

        try {
            this.gameThread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        showGameOverScreen();
    }
}
