package main.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Cell extends Entity{

    public static final int SIZE = 30;
    private int colPos, rowPos;
    private Block block;
    private float alpha;
    private BufferedImage bI;
    private boolean isGhost, flip;

    public Cell(int rowPos, int colPos, int actualX, int actualY){
        this.rowPos = rowPos;
        this.colPos = colPos;
        this.actualX = actualX;
        this.actualY = actualY;
        this.alpha = 1;

        velocityX = 2;
        isGhost = false;
        flip = false;

        this.block = null;
        //addBlock(new Block(TetrisPiece.Type.I, this));
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
//        if (!flip) {
//            changeX = (velocityX * extrapolate);
//            actualX += changeX;
//        }
//        if (flip) {
//            changeX = (velocityX * extrapolate);
//            actualX -= changeX;
//        }
//
//        if (actualX > 550) {
//            flip = true;
//        }
//        if (actualX < 300) {
//            flip = false;
//        }

        System.out.println(changeX);
        if (!flip) {
            changeX += (velocityX * extrapolate);
        }
        if (flip) {
            changeX -= (velocityX * extrapolate);
        }

        if (changeX > 200) {
            flip = true;
        }
        if (changeX < 1) {
            flip = false;
        }

        //changeX += 30;
        if (isFilled()) {
            block.update();
        }
        //System.out.println(extrapolate);
        //changeX += 2;
    }

    @Override
    public void draw(Graphics2D g2) {
        Rectangle2D rect = new Rectangle2D.Double(actualX + changeX, actualY, SIZE, SIZE);
        AlphaComposite alphaCom;
        if (!isFilled()) {
            g2.setColor(Color.BLACK);
            alphaCom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(alphaCom);
            g2.fill(rect);
            //g2.drawRect(actualX, actualY, SIZE, SIZE);
        } else {
            block.draw(g2);
        }

        g2.setColor(Color.WHITE);
        AlphaComposite pastAlpha = (AlphaComposite) g2.getComposite();

        if (isGhost) {
            g2.setStroke(new BasicStroke(3.0f));
            setAlpha(0.7f);
        } else {
            setAlpha(0.3f);
        }

        alphaCom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2.setComposite(alphaCom);
        g2.draw(rect);
        g2.setComposite(pastAlpha);
        setAlpha(1.0f);
    }

    public void setIsGhost(boolean isGhost) {
        this.isGhost = isGhost;
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

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

}
