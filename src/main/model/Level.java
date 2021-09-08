package main.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

// Level represents how the fast the gameSpeed is at that point in time and also adds on to the score the player attains
public class Level extends Entity{

    private int currentLevel;

    // constructor for Level requires actualX and actualY to have the specified location to be shown on the screen
    public Level(int actualX, int actualY) {
        this.actualX = actualX;
        this.actualY = actualY;
        currentLevel = 1;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    // MODIFIES: this
    // EFFECTS: increases the currentLevel by 1
    public void increaseLevel() {
        if (currentLevel == 20) {
            return;
        }
        currentLevel++;
    }

    // update method not required as of now
    @Override
    public void update() {}

    // EFFECTS: draws the currentLevel the player is in right now
    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
        g2.drawString("LEVEL", actualX, actualY);

        g2.setColor(Color.BLACK);
        Rectangle2D rect = new Rectangle2D.Double(actualX, actualY + 10, 200, 40);
        g2.fill(rect);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 30));
        g2.drawString(currentLevel + "", actualX, actualY + 40);
    }
}
