package main.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Cell extends Entity{

    public static final int SIZE = 30;
    private int colPos, rowPos;
    private Block block;
    private float alpha, lightFade;
    private boolean isGhost, flip, flipRight, flipLeft, canMoveVertically, canMoveRight, canMoveLeft, isLit;
    private Board board;

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

    @Override
    public void setExtrapolation(double extrapolate) {
        this.extrapolate = extrapolate;
        if (isFilled()) {
            block.setExtrapolation(extrapolate);
        }
    }

    @Override
    public void update() {
        if (canMoveVertically) {
            if (!flip) {
                changeY += (velocityY * extrapolate);
            }
            if (flip) {
                changeY -= (velocityY * extrapolate);
            }

            if (changeY > 10) {
                flip = true;
            }
            if (changeY < 1) {
                flip = false;
                canMoveVertically = false;
            }
//            System.out.println("changeY: " + changeY);
        }

        if (canMoveRight) {
            if (!flipRight) {
                changeX += (velocityX * extrapolate);
            }
            if (flipRight) {
                changeX -= (velocityX * extrapolate);
            }
//            System.out.println("canMoveRight changeX:           " + changeX);
//            System.out.println("canMoveRight changeX + actualX: " + (actualX + changeX));
            if (changeX > 5) {
                flipRight = true;
            }
            if (changeX < 1) {
                flipRight = false;
                canMoveRight = false;
            }
        } else if (canMoveLeft) {
            if (!flipLeft) {
                changeX -= (velocityX * extrapolate);
            }
            if (flipLeft) {
                changeX += (velocityX * extrapolate);
            }
//            System.out.println("canMoveLeft changeX: " + changeX);
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
//            block.setBoard(board);
//            if (changeX != block.getChangeX()) {
//                block.setChangeX(changeX);
//            }
//            if (changeY != block.getChangeY()) {
//                block.setChangeY(changeY);
//            }
            block.update();
//            System.out.println("Cell x: " + changeX);
//            System.out.println("Block x: " + block.getChangeX());
//            System.out.println("Cell y: " + changeY);
//            System.out.println("Block y: " + block.getChangeY());

        }
    }

    @Override
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

    public void addBlock(Block block) {
//        if (block == null) {
//            return;
//        }
        this.block = block;
        block.lockBlock(this);
    }

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
