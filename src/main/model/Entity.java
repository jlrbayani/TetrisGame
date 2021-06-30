package main.model;

import java.awt.*;

public abstract class Entity {

    protected double extrapolate, changeX, changeY;
    protected int actualX, actualY, velocityX, velocityY;
    protected boolean isPaused;

    public void setExtrapolation(double extrapolate) {
        this.extrapolate = extrapolate;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void setVelocity(int velX, int velY){
        this.velocityX = velX;
        this.velocityY = velY;
    }

    public abstract void update();
    public abstract void draw(Graphics2D g2);

}
