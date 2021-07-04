package main.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Cell extends Entity{

    public static final int SIZE = 30;
    private int colPos, rowPos;
    private Block block;
    private BufferedImage bI;

    public Cell(int rowPos, int colPos, int actualX, int actualY){
        this.rowPos = rowPos;
        this.colPos = colPos;
        this.actualX = actualX;
        this.actualY = actualY;

        velocityX = 2;

        this.block = null;
        addBlock(new Block(TetrisPiece.Type.I, this));
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
        if (isFilled()) {
            block.setExtrapolation(extrapolate);
        }
    }

    @Override
    public void update() {
        changeX += (velocityX * extrapolate);
        //changeX += 30;
        if (isFilled()) {
            block.update();
        }
        //System.out.println(extrapolate);
        //changeX += 2;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!isFilled()) {
            g2.setColor(Color.WHITE);
            Rectangle2D rect = new Rectangle2D.Double(actualX + changeX, actualY, SIZE, SIZE);
            g2.draw(rect);
            //g2.drawRect(actualX, actualY, SIZE, SIZE);
        } else {
            block.draw(g2);
        }

    }


    public boolean isFilled() {
        return block != null;
    }

    public void addBlock(Block block) {
        this.block = block;
    }

    public void removeBlock() {
        if (block != null) {
            block.removeCell();
        }
        this.block = null;
    }

}
