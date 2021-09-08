package main.model;

import java.awt.*;

// the CountDownTimer handles when a game is started or resumed such that the user has enough time (3 seconds) to get ready for the gameplay to begin
public class CountDownTimer extends Entity {

    private final int UPDATES_IN_SECOND = 60;
    private int currentUpdates, timer;
    private boolean isFinished;

    // constructor for the CountDownTimer which only requires its actualX and actualY values to be shown on the panel
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

    // EFFECTS: decreases timer every second
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

    // EFFECTS: draws the current value of timer
    @Override
    public void draw(Graphics2D g2) {
        if (!isFinished) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 80));
            g2.drawString(timer + "", actualX, actualY);
        }
    }
}
