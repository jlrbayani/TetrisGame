package main.model;

import java.awt.*;

public class CountDownTimer extends Entity {

    private final int UPDATES_IN_SECOND = 60;
    private int currentUpdates, timer;
    private boolean isFinished;

    public CountDownTimer(int actualX, int actualY) {
        this.actualX = actualX;
        this.actualY = actualY;

        currentUpdates = 0;
        timer = 3;
        isFinished = false;
    }

    public boolean getIsFinished() {
        return isFinished;
    }

    @Override
    public void update() {
        currentUpdates++;
        if (currentUpdates % UPDATES_IN_SECOND == 0) {
            timer--;
        }
        if (timer <= 0) {
            isFinished = true;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawString(timer + "", actualX, actualY);
    }
}
