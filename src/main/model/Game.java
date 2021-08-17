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

    private boolean keepRunning;
    private boolean isPaused;
    private TetrisFrame frame;
    private Thread gameThread;
    private CountDownTimer cdt;
    private SoundSystem ss;

    private int currentGameSpeed;
    private Board holdBoard, gameBoard, nextBoard;
    private ScoreMultiplier scoreMultiplier;
    private Score score;
    private int lines;
    private boolean canSwap;

    private TetrisPiece pieceInPlay;
    private TetrisPiece heldPiece;
    private LinkedList<TetrisPiece> nextPieces;

    private boolean[] keysHeldDown, keysSinglePress, keys;
    private int[] keysNumCall;
    private ArrayList<Entity> entityList;
    private Sound blockPlace;

    private int numFall, processInput;

    public Game(TetrisFrame frame) {
        this.frame = frame;
        this.ss = frame.getSoundSystem();
        currentGameSpeed = 30;
        lines = 0;
        isPaused = false;
        canSwap = true;
        numFall = 1;
        processInput = 0;

        entityList = new ArrayList<>();
        heldPiece = null;
        pieceInPlay = null;
        initStartingEntities();

        keysHeldDown = new boolean[3];
        keysSinglePress = new boolean[3];
        keys = new boolean[6];
        keysNumCall = new int[3];
        Arrays.fill(keysSinglePress, true);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public synchronized void setExtrapolation(double extrapolate) {
        for (Entity e: entityList) {
            e.setExtrapolation(extrapolate);
        }
    }

    public synchronized void update() {
        if (entityList.contains(cdt) && cdt.getIsFinished() && isPaused) {
            resumeGame();
            isPaused = false;
        }

       for (Entity e: entityList) {
           e.update();
       }

       processInput();
       updateHeldPiece();
       updatePieceInPlay();
       updateNextPieces();
       if (pieceInPlay == null) {
           getNewPieceInPlay();
       }
    }

    public synchronized void pauseGame() {
        if (entityList.contains(cdt) && cdt.getIsFinished()) {
            entityList.remove(cdt);
        }

        for (Entity e: entityList) {
            e.pause();
        }

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
        frame.getCurrentPanel().requestFocusInWindow();
    }

    public synchronized void startCountDown() {
        blockPlace = new Sound("resources/sounds/BlockPlacementSound.wav", "blockPlaced", ss.getCurrentVolume());
        ss.addToSounds(blockPlace);
        ss.playSound(blockPlace.getSoundName());
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


    private synchronized void initStartingEntities() {
        gameBoard = new Board(GAME_COLS, GAME_ROWS, 310, 50, 0, 0);
        gameBoard.setCanMove(true);
        entityList.add(gameBoard);

        nextBoard = new Board(NEXT_COLS, NEXT_ROWS, 750, 100, 0, 0);
        entityList.add(nextBoard);
        nextPieces = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            nextPieces.add(new TetrisPiece(1));
        }

        holdBoard = new Board(HOLD_COLS, HOLD_ROWS, 50, 100, 0, 0);
        entityList.add(holdBoard);

        scoreMultiplier = new ScoreMultiplier(20, 300);
        entityList.add(scoreMultiplier);
    }

    public void getNewPieceInPlay() {
        TetrisPiece newPiece = nextPieces.remove();
        nextPieces.add(new TetrisPiece(1));
        pieceInPlay = newPiece;
        gameBoard.validateStartingPieceCol(pieceInPlay);
    }

    private void updateHeldPiece() {
        if (heldPiece != null) {
            holdBoard.addTetrisPiece(heldPiece);
        }
    }

    private void updatePieceInPlay() {

        if (pieceInPlay != null) {
//            for (Cell c: pieceInPlay.getActualMatrix()) {
//                System.out.println("Row: " + c.getRowPos() + " Col: " + c.getColPos());
//            }
            //System.out.println(pieceInPlay.getActualMatrix().size());
            gameBoard.clearCells(pieceInPlay.getActualMatrix());
            gameBoard.addTetrisPiece(pieceInPlay);
            pieceInPlay.setPieceToMove(true);

            if (!isPaused) {
                if (numFall % currentGameSpeed == 0) {
                    if (!gameBoard.shiftPieceRow(1, pieceInPlay)) {
                        lockPieceInPlay();
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
            nextBoard.addTetrisPiece(nextPieces.get(i));
            nextBoard.shiftPieceRow(6);
        }
        nextBoard.setPieceRow(0);

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

    public void processInput() {
        if (isPaused) {
            return;
        }

        while (keysNumCall[0] > 0) {
            System.out.println("Move Left!");
            gameBoard.shiftPieceCol(-1, pieceInPlay);
            keysNumCall[0]--;
        }
        while (keysNumCall[1] > 0) {
            System.out.println("Move Right!");
            gameBoard.shiftPieceCol(1, pieceInPlay);
            keysNumCall[1]--;
        }
        while (keysNumCall[2] > 0) {
            System.out.println("Soft Drop!");
            if (!gameBoard.shiftPieceRow(1, pieceInPlay)) {
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

    public void keyPressed(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_ESCAPE:
                if (!isPaused) {
                    pauseGame();
                    frame.showPausedPanel();
                }
                break;
            case KeyEvent.VK_SPACE:
                if (keysSinglePress[0] && pieceInPlay != null) {
                    System.out.println("Fast Drop!");
                    //pieceInPlay.lockPiece(gameBoard);
                    pieceInPlay.fastDrop();
                    lockPieceInPlay();
                    keysSinglePress[0] = false;
                }
                break;
            case KeyEvent.VK_UP:
                if (keysSinglePress[1] && pieceInPlay != null) {
                    System.out.println("Rotate Right!");
                    //pieceInPlay.rotateRight();
                    gameBoard.rotatePiece(pieceInPlay,true);
//                    pieceInPlay.printOffsets();
                    keysSinglePress[1] = false;
                }
                break;
            case KeyEvent.VK_Z:
                if (keysSinglePress[2] && pieceInPlay != null) {
                    System.out.println("Rotate Left!");
//                    pieceInPlay.rotateLeft();
                    gameBoard.rotatePiece(pieceInPlay, false);
//                    pieceInPlay.printOffsets();
                    keysSinglePress[2] = false;
                }
                break;
            case KeyEvent.VK_C:
                if (canSwap && pieceInPlay != null) {
                    System.out.println("Swapping!");
                    gameBoard.clearCells(pieceInPlay.getActualMatrix());
                    pieceInPlay.resetRotation();
                    if (heldPiece == null) {
                        heldPiece = pieceInPlay;
                        heldPiece.setPieceToMove(false);
                        getNewPieceInPlay();
                    } else {
                        TetrisPiece swap = heldPiece;
                        heldPiece = pieceInPlay;
                        heldPiece.setPieceToMove(false);
                        pieceInPlay = swap;
                        pieceInPlay.setPieceToMove(true);
                    }
                    holdBoard.clearBoard();
                    canSwap = false;
                    gameBoard.setPieceRow(0);
                }
                break;
        }

        processInput();
    }

    public void piecePlaced() {
        canSwap = true;
    }

    private void lockPieceInPlay() {
        gameBoard.setPieceRow(0);
        piecePlaced();
        pieceInPlay = null;
        System.out.println("Piece In Play Locked!");
    }

    // covers the main game loop
    @Override
    public void run() {
        // in the future, might use nanoTime instead of currentTimeMillis
        double previous = System.currentTimeMillis();
        double timer = System.currentTimeMillis();
        double lag = 0;
        int frames = 0;
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

            //System.out.println(lag / MS_PER_UPDATE);

            //frame.getCurrentPanel().repaint();
            frames++;

            while (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frame.setTitle("Tetris | updates: " + updates + " | fps: " + frames);
                updates = 0;
                frames = 0;
//                if (pieceInPlay != null) {
//                    gameBoard.clearBoard();
//                    pieceInPlay.rotateRight();
//                }
            }

        }

        try {
            this.gameThread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
