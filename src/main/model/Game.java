package main.model;

import main.ui.TetrisFrame;

public class Game implements Runnable{
    private final double MS_PER_UPDATE = 15;
    private boolean keepRunning;
    private TetrisFrame frame;
    private Thread gameThread;

    public Game(TetrisFrame frame) {
        this.frame = frame;

        this.keepRunning = true;
        gameThread = new Thread(this, "Game");
        gameThread.start();
    }

    public void render(double extrapolate) {}

    public void update() {

    }

    public void endGame() {
        keepRunning = false;
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

            render(lag/MS_PER_UPDATE);
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
