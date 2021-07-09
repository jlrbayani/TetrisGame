package main.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Score extends Entity{

    private final int BASE_POINTS = 100;
    private final int MAX_MULTIPLIER = 8;
    private int currentScore;
    private double currentMultiplier, multiplierHeight;
    private int multiplierX, multiplierY;

    public Score(int scoreX, int scoreY) {
        this.currentScore = 0;
        this.currentMultiplier = 1;
        this.actualX = scoreX;
        this.actualY = scoreY;

        this.multiplierX = 290;
        this.multiplierY = 50;
        this.multiplierHeight = 630;
    }

    public void clearLinesScore(int lines) {
        this.currentScore += lines * BASE_POINTS * currentMultiplier;
    }

    public void increaseCurrentMultiplier() {
        currentMultiplier *= 2;
        if (currentMultiplier > 8) {
            currentMultiplier = 8;
        }
    }

    public void decreaseCurrentMultiplier() {
        if (currentMultiplier > 1) {
            currentMultiplier -= 0.1;
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.BLUE);
        g2.drawString("SCORE", actualX, actualY);
        g2.drawString(currentScore + "", actualX, actualY + 15);

        Rectangle2D rectMultiplier = new Rectangle2D.Double(multiplierX + changeX, multiplierY, 20, multiplierHeight);
        g2.setColor(Color.RED);
        g2.fill(rectMultiplier);
    }

}
