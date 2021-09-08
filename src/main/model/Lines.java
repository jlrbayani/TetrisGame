package main.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

// represents the amount of line clears the player has achieved
public class Lines extends Entity{

    private int currentNumLines, newNumLines;

    // the constructor requires actualX and actualY to show the currentNumLines at the specified location on the screen
    public Lines(int actualX, int actualY) {
        this.actualX = actualX;
        this.actualY = actualY;
        currentNumLines = 0;
        newNumLines = 0;
    }

    // MODIFIES: this
    // EFFECTS: adds the number of numLines to newNumLines
    public void addLines(int numLines) {
        newNumLines += numLines;
    }

    public int getNewNumLines() {
        return newNumLines;
    }

    // EFFECTS: currentNumLines catches up to newNumLines adding 1 to every updated
    @Override
    public void update() {
        if (!isPaused) {
            if (currentNumLines < newNumLines) {
                currentNumLines += 1;
            }
        }
    }

    // EFFECTS: draws the currentNumLines to the screen
    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
        g2.drawString("LINES", actualX, actualY);

        g2.setColor(Color.BLACK);
        Rectangle2D rect = new Rectangle2D.Double(actualX, actualY + 10, 200, 40);
        g2.fill(rect);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 30));
        g2.drawString(currentNumLines + "", actualX, actualY + 40);
    }
}
