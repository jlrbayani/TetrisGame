package main.model;

import java.awt.*;

public class Cell implements Entity{

    public static final int SIZE = 30;
    private int colPos, rowPos;
    private int actualX, actualY;
    private double extrapolate;
    private boolean isFilled;
    private Block block;

    public Cell(int colPos, int rowPos, int actualX, int actualY){
        this.colPos = colPos;
        this.rowPos = rowPos;
        this.actualX = actualX;
        this.actualY = actualY;

        this.isFilled = false;
        this.block = null;
    }

    public int getColPos() {
        return colPos;
    }

    public int getRowPos() {
        return rowPos;
    }

    @Override
    public void setExtrapolation(double extrapolate) {
        this.extrapolate = extrapolate;
    }

    @Override
    public void update() {
        actualX += 3;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!isFilled) {
            g2.setColor(Color.BLACK);
            g2.drawRect(actualX, actualY, SIZE, SIZE);
        }


    }
}
