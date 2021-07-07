package main.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Block extends Entity{

    private int colPos, rowPos;
    private int move;
    private BufferedImage img;
    private Cell cell;
    private TetrisPiece.Type blockType;
    private boolean isLocked;

    public enum Dir {
        DOWN, LEFT, RIGHT
    }

    public Block(TetrisPiece.Type blockType, int rowPos, int colPos, int offsetX, int offsetY) {

    }


    public Block(TetrisPiece.Type blockType, Cell cell) {
        this.cell = cell;
        this.actualX = cell.getActualX();
        this.actualY = cell.getActualY();
        this.rowPos = cell.getRowPos();
        this.colPos = cell.getColPos();
        this.blockType = blockType;
        this.isLocked = true;

        this.velocityX = 2;

        setBlockImg(this.blockType);
    }

    private void setBlockImg(TetrisPiece.Type blockType) {
        switch (blockType) {
            case I:
                try {
                    img = ImageIO.read(new File("resources/blocks/blockI.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Z:
                try {
                    img = ImageIO.read(new File("resources/blocks/blockZ.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
    }

    public BufferedImage getImg() {
        return img;
    }

    public void removeCell() {
        isLocked = false;
        cell = null;
    }

    public void lockBlock(Cell c) {
        isLocked = true;
        cell = c;
        this.actualX = cell.getActualX();
        this.actualY = cell.getActualY();
        this.rowPos = c.getRowPos();
        this.colPos = c.getColPos();

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
        //System.out.println(extrapolate);
        //changeX  += (velocityX * extrapolate);
        //System.out.println("changeX: " + changeX);
        //System.out.println("sum: " + (int)(actualX + changeX));
        //changeX += 2;

        actualX += velocityX * extrapolate;
        if (!isLocked) {

        }

    }

    @Override
    public void draw(Graphics2D g2) {
        // flag
//        int t = (this.rowPos * 10 + this.colPos);
//        if (t == 1 || t == 2) {
//            System.out.println( t + ": " +  (int)(actualX + changeX));
//        }

        g2.drawImage(img, (int) Math.floor(actualX), actualY, null);

    }
}
