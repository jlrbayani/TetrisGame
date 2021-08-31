package main.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ScoreMultiplier extends Entity{

    private final int BASE_POINTS = 100;
    private final int MAX_MULTIPLIER = 8;
    private final float MAX_MULTIPLIER_HEIGHT = 600;
    private double currentMultiplier;
    private int multiplierHeight;
    private float alpha;

    public ScoreMultiplier(int actualX, int actualY) {
        this.currentMultiplier = 1;
        this.actualX = actualX;
        this.actualY = actualY;
        this.alpha = (float) 1;

        this.multiplierHeight = 10;
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

    public void addToMultiplierHeight(float heightAdded) {
        if (!(multiplierHeight + heightAdded <= MAX_MULTIPLIER_HEIGHT)) {
            heightAdded = MAX_MULTIPLIER_HEIGHT - heightAdded - multiplierHeight;
        }
        multiplierHeight += heightAdded;
        actualY -= heightAdded;
    }

    public double getCurrentMultiplier(){
        return currentMultiplier;
    }

    @Override
    public void update() {
        if (!isPaused) {
            if (multiplierHeight > 10) {
                actualY++;
                multiplierHeight--;
            }
            float ratio = multiplierHeight / MAX_MULTIPLIER_HEIGHT;
            if (ratio < 0.2) {
//                alpha = (float) 0.2;
                currentMultiplier = 1;
            } else if (ratio < 0.4) {
                currentMultiplier = 2;
//                alpha = (float) 0.4;
            } else if (ratio < 0.7) {
                currentMultiplier = 4;
//                alpha = (float) 0.7;

            } else if (ratio < 1.0){
                currentMultiplier = 8;
//                alpha = (float) 1;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
//        g2.setColor(Color.BLUE);
//        g2.drawString("SCORE", actualX, actualY);
//        g2.drawString(currentScore + "", actualX, actualY + 15);

        AlphaComposite alphaCom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        AlphaComposite pastAlpha = (AlphaComposite) g2.getComposite();
        g2.setComposite(alphaCom);
        Rectangle2D rectMultiplier = new Rectangle2D.Double(actualX + changeX, actualY + changeY, 20, multiplierHeight);
        g2.setColor(Color.RED);
        g2.fill(rectMultiplier);
        g2.setComposite(pastAlpha);
    }

}
