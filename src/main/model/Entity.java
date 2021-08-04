package main.model;

import java.awt.*;

public abstract class Entity {

    protected double extrapolate;
    protected double changeX;
    protected double changeY;
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

    public int getActualX() {
        return actualX;
    }

    public int getActualY() {
        return actualY;
    }

    public double getChangeX() {
        return changeX;
    }

    public double getChangeY() {
        return changeY;
    }

    public void setChangeX(double changeX) {
        this.changeX = changeX;
    }

    public void setChangeY(double changeY) {
        this.changeY = changeY;
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public abstract void update();
    public abstract void draw(Graphics2D g2);

}
