package main.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

// ScoreMultiplier is a visualization of the combo meter the user is currently in
// the taller this is, the higher points they receive per point
public class ScoreMultiplier extends Entity{

    private final float MAX_MULTIPLIER_HEIGHT = 600;
    private double currentMultiplier;
    private int multiplierHeight;

    // constructor for ScoreMultiplier requiring actualX and actualY to set its location on the screen
    public ScoreMultiplier(int actualX, int actualY) {
        this.currentMultiplier = 1;
        this.actualX = actualX;
        this.actualY = actualY;

        this.multiplierHeight = 10;
    }

    // MODIFIES: this
    // EFFECTS: adds to the multiplier rectangle height using heightAdded
    // if MAX_MULTIPLIER_HEIGHT is exceeded, multiplier height is set to that value
    public void addToMultiplierHeight(float heightAdded) {
        if (multiplierHeight + heightAdded >= MAX_MULTIPLIER_HEIGHT) {
            heightAdded = MAX_MULTIPLIER_HEIGHT - multiplierHeight;
        }
        multiplierHeight += heightAdded;
        actualY -= heightAdded;
    }

    public double getCurrentMultiplier(){
        return currentMultiplier;
    }

    // EFFECTS: constantly decreases the height of the multiplierHeight and updates the currentMultiplier depending on this height's ratio with the MAX_MULTIPLIER_HEIGHT
    @Override
    public void update() {
        if (!isPaused) {
            if (multiplierHeight > 10) {
                actualY++;
                multiplierHeight--;
            }
            float ratio = multiplierHeight / MAX_MULTIPLIER_HEIGHT;
            if (ratio < 0.2) {
                currentMultiplier = 1;
            } else if (ratio < 0.4) {
                currentMultiplier = 2;
            } else if (ratio < 0.7) {
                currentMultiplier = 4;

            } else if (ratio < 1.0){
                currentMultiplier = 8;
            }
        }
    }

    // EFFECTS: draws the rectangle which represents the height of the multiplier
    @Override
    public void draw(Graphics2D g2) {
        Rectangle2D rectMultiplier = new Rectangle2D.Double(actualX + changeX, actualY + changeY, 20, multiplierHeight);
        g2.setColor(Color.RED);
        g2.fill(rectMultiplier);
    }

}
