package main.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

// represents a Cell which build up a Board object and helps store Block objects for the creation of TetrisPieces
public class Cell extends Entity{

    public static final int SIZE = 30;
    private int colPos, rowPos;
    private Block block;
    private float alpha, lightFade;
    private boolean isGhost, flip, flipRight, flipLeft, canMoveVertically, canMoveRight, canMoveLeft, isLit;
    private Board board;

    // constructor for the Cell in which it belongs to a Board object
    public Cell(int rowPos, int colPos, int actualX, int actualY, Board board){
        this.rowPos = rowPos;
        this.colPos = colPos;
        this.actualX = actualX;
        this.actualY = actualY;
        this.alpha = 1;

        velocityX = 1;
        velocityY = 2;
        isGhost = false;
        flip = false;
        flipRight = false;
        flipLeft = false;
        canMoveVertically = false;
        canMoveLeft = false;
        canMoveRight = false;
        isLit = false;
        lightFade = 0;

        this.block = null;
        this.board = board;
        //addBlock(new Block(TetrisPiece.Type.I, this));
    }

    // constructor for a cell that only has rowPos and colPos values
    public Cell(int rowPos, int colPos) {
        this.rowPos = rowPos;
        this.colPos = colPos;
    }

    public int getColPos() {
        return colPos;
    }

    public int getRowPos() {
        return rowPos;
    }

    public boolean getCanMove() {
        return canMoveVertically;
    }

    // MODIFIES: this, block
    // EFFECTS: sets the extrapolate value of this cell as extrapolate and propagates that value to the block if this isFilled()
    @Override
    public void setExtrapolation(double extrapolate) {
        this.extrapolate = extrapolate;
        if (isFilled()) {
            block.setExtrapolation(extrapolate);
        }
    }

    // MODIFIES: this
    // EFFECTS: at every update a cell can possibly shift down, left, or right depending on their respective boolean values
    // if the cell isLit then it also adjusts the lightFade used in rendering it
    // if the cell isFilled() then it also updates the block inside it
    @Override
    public void update() {
        if (canMoveVertically) {
            if (!flip) {
                changeY += (velocityY);
            }
            if (flip) {
                changeY -= (velocityY);
            }

            if (changeY > 10) {
                flip = true;
            }
            if (changeY < 1) {
                flip = false;
                canMoveVertically = false;
            }
        }

        if (canMoveRight) {
            if (!flipRight) {
                changeX += (velocityX);
            }
            if (flipRight) {
                changeX -= (velocityX);
            }
            if (changeX > 5) {
                flipRight = true;
            }
            if (changeX < 1) {
                flipRight = false;
                canMoveRight = false;
            }
        } else if (canMoveLeft) {
            if (!flipLeft) {
                changeX -= (velocityX);
            }
            if (flipLeft) {
                changeX += (velocityX);
            }
            if (changeX < -5) {
                flipLeft = true;
            }
            if (changeX > 0) {
                flipLeft = false;
                canMoveLeft = false;
            }
        }

        if (isLit) {
            lightFade -= 0.05;
            if (lightFade < 0) {
                lightFade = 0;
                isLit = false;
            }
        }

        if (isFilled()) {
            block.update();

        }
    }

    // EFFECTS: draws the cell and the necessary effects for it, delegates its draw to its block if this cell isFilled()
    public void draw(Graphics2D g2) {
        if (rowPos == 0 && board.getNumRows() == 21) {
            return;
        }
        Rectangle2D rect = new Rectangle2D.Double(actualX + changeX, actualY + changeY, SIZE, SIZE);
        AlphaComposite alphaCom;
        if (isFilled()) {
            block.draw(g2);
        } else if (isLit) {
            AlphaComposite pastAlpha = (AlphaComposite) g2.getComposite();
            g2.setColor(Color.WHITE);
            alphaCom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, lightFade);
            g2.setComposite(alphaCom);
            g2.fill(rect);
            g2.setComposite(pastAlpha);
        } else {
            g2.setColor(Color.BLACK);
            alphaCom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(alphaCom);
            g2.fill(rect);
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
        g2.setStroke(new BasicStroke(1.0f));
    }

    public void setIsGhost(boolean isGhost) {
        this.isGhost = isGhost;
    }

    public boolean getIsGhost() { return isGhost; }

    public boolean isFilled() {
        return block != null;
    }

    // MODIFIES: this, block
    // EFFECTS: adds the block to this cell and this cell's variables are sent to block using lockBlock
    public void addBlock(Block block) {
        this.block = block;
        block.lockBlock(this);
    }

    // MODIFIES: this, block
    // EFFECTS: removes the block from this cell and disassociates this cell from the block
    public void removeBlock() {
        if (block != null) {
            block.removeCell();
        }
        this.block = null;

    }

    public boolean canMoveVertically() {
        return canMoveVertically;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public int getIndex(int maxCol) {
        return rowPos * maxCol + colPos;
    }

    public Block getBlock() {
        return block;
    }

    public void setCanMoveVertically(boolean canMoveVertically) {
        this.canMoveVertically = canMoveVertically;
    }

    public void setCanMoveRight(boolean canMoveRight) {
        this.canMoveRight = canMoveRight;
    }

    public void setCanMoveLeft(boolean canMoveLeft) {
        this.canMoveLeft = canMoveLeft;
    }

    public void setIsLit(boolean isLit) {
        this.isLit = isLit;
    }

    public boolean isLit() {
        return isLit;
    }

    public void setLightFade(float fade) {
        lightFade = fade;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "colPos=" + colPos +
                ", rowPos=" + rowPos +
                ", board=" + board +
                '}';
    }
}
