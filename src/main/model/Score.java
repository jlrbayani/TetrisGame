package main.model;

import main.persistence.Writable;
import org.json.JSONObject;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Score extends Entity implements Writable, Comparable<Score> {

    private String name;
    private int currentScore, newScore;

    public Score(int actualX, int actualY) {
        this.actualX = actualX;
        this.actualY = actualY;
        this.currentScore = 0;
        this.newScore = 0;
    }

    public Score(String name, int newScore) {
        this.name = name;
        this.newScore = newScore;
        this.currentScore = 0;
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

    public void addToScore(int added) {
        if (added < 0) {
            return;
        }

        this.newScore += added;
    }

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

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("newScore", newScore);

        return json;
    }

    @Override
    public int compareTo(Score s) {
        return s.newScore - newScore;
    }
}
