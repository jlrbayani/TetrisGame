package main.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Block extends Entity{

    private int colPos, rowPos;
    private BufferedImage img;
    private Cell cell;
    private TetrisPiece.Type blockType;
    private boolean isLocked, flip, move;
    private Board board;
    private int moveDownCounter;

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
        this.flip = false;
        this.move = false;
        this.velocityX = 2;
        this.moveDownCounter = 1;

        setBlockImg(this.blockType);
    }

    private void setBlockImg(TetrisPiece.Type blockType) {
        String s = "";
        switch (blockType) {
            case I:
                s = "I";
                break;
            case J:
                s = "J";
                break;
            case L:
                s = "L";
                break;
            case O:
                s = "O";
                break;
            case S:
                s = "S";
                break;
            case T:
                s = "T";
                break;
            case Z:
                s = "Z";
                break;
        }

        try {
            img = ImageIO.read(new File("resources/blocks/block" + s +".png"));
        } catch (IOException e) {
            e.printStackTrace();
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
        if (c == null) {
            return;
//            System.out.println("Cell is null: ");
//            System.out.println("Block row: " + getRowPos());
//            System.out.println("Block col: " + getColPos());
        }
        isLocked = true;
        cell = c;
        this.actualX = cell.getActualX();
        this.actualY = cell.getActualY();
        this.rowPos = c.getRowPos();
        this.colPos = c.getColPos();
        move = false;
    }

    public int getColPos() {
        return colPos;
    }

    public int getRowPos() {
        return rowPos;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setMove(boolean move) {
        this.move = move;
    }

    public void moveBlockDown() {
        if (cell != null && board != null) {
            if (cell.getRowPos() < board.getNumRows() - 1) {
                int newRow = cell.getRowPos() + 1;
                Cell newCell = board.getCell(getRowPos(), newRow);
                System.out.println("New Cell Index: " + newCell.getIndex(21));
                if (!newCell.isFilled()) {
                    System.out.println(this.toString());
                    cell.removeBlock();
                    newCell.addBlock(this);
                }
            }
        }
    }

    public void moveBlockRight() {

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

        //actualX += velocityX * extrapolate;
//        if (!isLocked) {
//
//        }

//        if (board.getNumRows() == 21)
//            System.out.println(this);
        if (cell != null) {
//            if (move && moveDownCounter % 120 == 0) {
//                System.out.println("Passive move down!");
//                moveBlockDown();
//            }
            lockBlock(cell);
        }

//        if (!flip) {
//            changeX += (velocityX * extrapolate);
//        }
//        if (flip) {
//            changeX -= (velocityX * extrapolate);
//        }

//        if (changeX > 200) {
//            flip = true;
//        }
//        if (changeX < 1) {
//            flip = false;
//        }

        //System.out.println("Move Counter: " + moveDownCounter);
        moveDownCounter++;
        if (moveDownCounter > 6000) {
            moveDownCounter = 1;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        // flag
//        int t = (this.rowPos * 10 + this.colPos);
//        if (t == 1 || t == 2) {
//            System.out.println( t + ": " +  (int)(actualX + changeX));
//        }

        g2.drawImage(img, (int) (actualX + changeX), actualY, null);

    }

    @Override
    public String toString() {
        return "Block{" +
                "colPos=" + colPos +
                ", rowPos=" + rowPos +
                ", cell=" + cell +
                ", isLocked= " + isLocked +
                '}';
    }
}
