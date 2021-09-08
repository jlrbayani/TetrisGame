package main.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

// represents a Block that builds up a TetrisPiece
public class Block extends Entity{

    private int colPos, rowPos;
    private BufferedImage img;
    private Cell cell;
    private TetrisPiece.Type blockType;
    private boolean isLocked;

    // the Block constructor that needs a specified blockType and needs a cell to be passed in
    // a block can only exist in a cell
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

    // MODIFIES: this
    // EFFECTS: sets the image to be used for the block
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
            img = ImageIO.read(getClass().getResource("/blocks/block" + s +".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: removes the cell association form this block
    public void removeCell() {
        isLocked = false;
        cell = null;
    }

    // MODIFIES: this
    // EFFECTS: locks the block in Cell c
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

    // EFFECTS: sets the extrapolation value for this block
    @Override
    public void setExtrapolation(double extrapolate) {
        this.extrapolate = extrapolate;
    }

    // MODIFIES: this
    // EFFECTS: if cell is not null, then block is locked to its position
    @Override
    public void update() {
        if (cell != null) {
            lockBlock(cell);
        }
    }

    // EFFECTS: draws the the img of the specified blockType on the screen
    @Override
    public void draw(Graphics2D g2) {
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
