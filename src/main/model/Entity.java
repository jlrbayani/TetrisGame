package main.model;

import java.awt.*;

// this abstract Entity class serves as a basis and reference for every object in the game that must be rendered, updated, and has extrapolated values
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

    public int getVelocityX() {
        return velocityX;
    }

    public int getVelocityY() {
        return velocityY;
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

    // EFFECTS: to be implemented by the child object, update will be called to change any of the values that is required for that object
    public abstract void update();

    // EFFECTS: to be implemented by the child object, draw will be called whenever an entity needs to be shown on screen and the values used in draw coincides with the values
    // changed in update()
    public abstract void draw(Graphics2D g2);

}
