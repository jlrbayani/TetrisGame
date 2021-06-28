package main.model;

import java.awt.*;

public class Block implements Entity{

    private double fallSpeed;
    private double xPos, yPos;
    private double extrapolate;
    private int colPos, rowPos;
    private int blockType;

    public enum Dir {
        DOWN, LEFT, RIGHT
    }

    public Block(TetrisPiece.Type blockType) {
        this.fallSpeed = 1;

    }



    @Override
    public void setExtrapolation(double extrapolate) {
        this.extrapolate = extrapolate;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g2) {

    }
}
