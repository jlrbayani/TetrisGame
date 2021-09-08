package main.model;

import main.persistence.Writable;
import org.json.JSONObject;

import java.awt.*;
import java.awt.geom.Rectangle2D;

// a Score represents how many points a player has gotten and eventually also stores the name of the Player after finishing a game
public class Score extends Entity implements Writable, Comparable<Score> {

    private String name;
    private int currentScore, newScore;

    // constructor for Score which requires actualX and actualY to be defined which refers to where it will show up on the screen
    public Score(int actualX, int actualY) {
        this.actualX = actualX;
        this.actualY = actualY;
        this.currentScore = 0;
        this.newScore = 0;
    }

    // in this constructor it only matters to keep track of its data (name and its score value)
    public Score(String name, int newScore) {
        this.name = name;
        this.newScore = newScore;
        this.currentScore = newScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public int getScoreNow() {
        return newScore;
    }

    public void setNewScore(int score) {
        this.newScore = score;
    }

    // MODIFIES: this
    // EFFECTS: adds to the score value
    public void addToScore(int added) {
        if (added < 0) {
            return;
        }

        this.newScore += added;
    }

    // EFFECTS: at every update currentScore attempts to catchup to newScore and the rate at which it does this depends on the difference between the two values
    @Override
    public void update() {
        int rateAdded;
        if (!isPaused) {
            if (currentScore < newScore) {
                int diff = newScore - currentScore;
                if (diff >= 500) {
                    rateAdded = 100;
                } else if (diff > 100) {
                    rateAdded = 10;
                } else  {
                    rateAdded = 1;
                }
                if (currentScore + rateAdded > newScore) {
                    currentScore = newScore;
                } else {
                    currentScore += rateAdded;
                }
            }
        }
    }

    // EFFECTS: draws the currentScore
    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
        g2.drawString("SCORE", actualX, actualY);

        g2.setColor(Color.BLACK);
        Rectangle2D rect = new Rectangle2D.Double(actualX, actualY + 10, 200, 40);
        g2.fill(rect);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 30));
        g2.drawString(currentScore + "", actualX, actualY + 40);
    }

    // EFFECTS: returns this as a JSONObject
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        if (name == null) {
            name = "";
        }
        json.put("name", name);
        json.put("newScore", newScore);

        return json;
    }

    // compares score to be sorted from greatest to least newScore values
    @Override
    public int compareTo(Score s) {
        return s.newScore - newScore;
    }
}
