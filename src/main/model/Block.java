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
    private boolean isLocked;
    private Board board;

    public Block(TetrisPiece.Type blockType, Cell cell) {
        this.cell = cell;
        this.actualX = cell.getActualX();
        this.actualY = cell.getActualY();
        this.rowPos = cell.getRowPos();
        this.colPos = cell.getColPos();
        this.blockType = blockType;
        this.isLocked = true;
        this.velocityX = cell.velocityX;
        this.velocityY = cell.velocityY;

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

    public TetrisPiece.Type getBlockType() {
        return blockType;
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
        }
        isLocked = true;
        cell = c;
        this.actualX = cell.getActualX();
        this.actualY = cell.getActualY();
        this.changeX = cell.getChangeX();
        this.changeY = cell.getChangeY();
        this.rowPos = c.getRowPos();
        this.colPos = c.getColPos();
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
//            setChangeX(cell.getChangeX());
//            setChangeY(cell.getChangeY());

//            if (cell.isCanMove()) {
//                if (!flip) {
//                    changeY += (velocityX * extrapolate);
//                }
//                if (flip) {
//                    changeY -= (velocityX * extrapolate);
//                }
//
//                if (changeX > 60) {
//                    flip = true;
//                }
//                if (changeX < 1) {
//                    flip = false;
//                }
//            }
            lockBlock(cell);

        }

        //System.out.println("Move Counter: " + moveDownCounter);
//        moveDownCounter++;
//        if (moveDownCounter > 6000) {
//            moveDownCounter = 1;
//        }
    }

    @Override
    public void draw(Graphics2D g2) {
        // flag
//        int t = (this.rowPos * 10 + this.colPos);
//        if (t == 1 || t == 2) {
//            System.out.println( t + ": " +  (int)(actualX + changeX));
//        }

        g2.drawImage(img, (int) (actualX + changeX), (int) (actualY + changeY), null);

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
