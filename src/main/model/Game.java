package main.model;

import main.ui.TetrisFrame;

import java.util.ArrayList;

public class Game implements Runnable{
    private final double MS_PER_UPDATE = 16.5;
    private final int GAME_COLS = 10;
    private final int GAME_ROWS = 21;
    private final int NEXT_COLS = 4;
    private final int NEXT_ROWS = 16;
    private final int HOLD_COLS = 4;
    private final int HOLD_ROWS = 4;

    private boolean keepRunning;
    private TetrisFrame frame;
    private Thread gameThread;

    private double currentGameSpeed;

    private Board holdBoard, gameBoard, nextBoard;
    private Score score;
    private int lines;

    private ArrayList<Entity> entityList;

    public Game(TetrisFrame frame) {
        this.frame = frame;
        currentGameSpeed = 1;
        score = new Score();
        lines = 0;

        entityList = new ArrayList<>();
        initStartingEntities();
    }

    public void setExtrapolation(double extrapolate) {
        for (Entity e: entityList) {
            e.setExtrapolation(extrapolate);
        }
    }

    public void update() {
       for (Entity e: entityList) {
           e.update();
       }
    }

    public synchronized void startGame() {
        this.keepRunning = true;
        gameThread = new Thread(this, "Game");
        gameThread.start();
    }

    public synchronized void endGame() {
        keepRunning = false;

        try {
            this.gameThread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void initStartingEntities() {
        gameBoard = new Board(GAME_COLS, GAME_ROWS, 300, 50);
        entityList.add(gameBoard);

//        nextBoard = new Board(NEXT_COLS, NEXT_ROWS);
//        entityList.add(nextBoard);
//
//        holdBoard = new Board(HOLD_COLS, HOLD_ROWS);
//        entityList.add(holdBoard);
    }


    public ArrayList<Entity> getEntityList() {
        return entityList;
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

            // processInput();

            while (lag >= MS_PER_UPDATE) {
                update();
                updates++;
                lag -= MS_PER_UPDATE;
            }

            //System.out.println(lag / MS_PER_UPDATE);
            setExtrapolation(lag / MS_PER_UPDATE);
            frame.getCurrentPanel().repaint();
            frames++;

            while (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frame.setTitle("Tetris | updates: " + updates + " | fps: " + frames);
                updates = 0;
                frames = 0;
            }

        }
    }
}
