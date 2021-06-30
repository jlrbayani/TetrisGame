package main.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Cell extends Entity{

    public static final int SIZE = 30;
    private int colPos, rowPos;
    private boolean isFilled;
    private Block block;
    private BufferedImage bI;

    public Cell(int colPos, int rowPos, int actualX, int actualY){
        this.colPos = colPos;
        this.rowPos = rowPos;
        this.actualX = actualX;
        this.actualY = actualY;

        velocityX = 2;

        this.isFilled = true;
        this.block = null;
        addBlock(new Block(TetrisPiece.Type.I));
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
        changeX  += (velocityX * extrapolate);
        if (isFilled) {
            block.update();
        }
        //System.out.println(extrapolate);
        //changeX += 2;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!isFilled) {
            g2.setColor(Color.BLACK);
            Rectangle2D rect = new Rectangle2D.Double(actualX + changeX, actualY, SIZE, SIZE);
            g2.draw(rect);
            //g2.drawRect(actualX, actualY, SIZE, SIZE);
        } else {
            block.draw(g2);
        }


    }

    public void addBlock(Block block) {
        isFilled = true;
        this.block = block;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }
}
