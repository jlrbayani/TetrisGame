package main.model;

import java.awt.*;

public class Score implements Entity{

    private int currentScore;
    private double currentMultiplier;
    private double extrapolate;

    public Score() {
        this.currentScore = 0;
        this.currentMultiplier = 1;

    }

    @Override
    public void setExtrapolation(double extrapolate) {

    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g2) {

    }

}
