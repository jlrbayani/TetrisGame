package main.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class TetrisPiece extends Entity{
    public static final int MATRIX_NUM_ROWS = 4;
    public static final int MATRIX_NUM_COLS = 4;

    private int rotation, rowPos, colPos;
    private Type type;
    private ArrayList<Cell> originalMatrix;
    private ArrayList<Cell> actualMatrix;
    private ArrayList<Block> blocks;
    private boolean inPlay;

    public enum Type {
        I, J, L, O, S, T, Z
    }

    public TetrisPiece() {
        this.rotation = 1;
        this.inPlay = false;
        this.type = chooseRandomType();
        setBlocks();
    }

    public TetrisPiece(int numType) {
        this.rotation = 1;
        this.inPlay = false;
        this.type = setType(numType);
        setBlocks();
    }

    private Type chooseRandomType() {
        Random random = new Random();
        return setType(random.nextInt(7) + 1);
    }

    public Type setType(int numType) {
        switch (numType) {
            case 1:
                return Type.I;
            case 2:
                return Type.J;
            case 3:
                return Type.L;
            case 4:
                return Type.O;
            case 5:
                return Type.S;
            case 6:
                return Type.T;
            default:
                return Type.Z;
        }
    }

    public void setBlocks() {
        originalMatrix = new ArrayList<>();
        actualMatrix = new ArrayList<>();
        blocks = new ArrayList<>();
        ArrayList<Integer> truePos = new ArrayList<>();

        switch (this.type) {
            case I:
                truePos.add(1);
                truePos.add(5);
                truePos.add(9);
                truePos.add(13);
                break;
            case J:
                truePos.add(1);
                truePos.add(5);
                truePos.add(8);
                truePos.add(9);
                break;
            case L:
                truePos.add(1);
                truePos.add(5);
                truePos.add(9);
                truePos.add(10);
                break;
            case O:
                truePos.add(5);
                truePos.add(6);
                truePos.add(9);
                truePos.add(10);
                break;
            case S:
                truePos.add(1);
                truePos.add(2);
                truePos.add(4);
                truePos.add(5);
                break;
            case T:
                truePos.add(1);
                truePos.add(4);
                truePos.add(5);
                truePos.add(6);
                break;
            case Z:
                truePos.add(4);
                truePos.add(5);
                truePos.add(9);
                truePos.add(10);
                break;
        }

        originalMatrix = setMatrixBlocks(truePos);
    }

    public ArrayList<Cell> getOriginalMatrix() {
        return originalMatrix;
    }

    public void setPieceToMove(boolean move) {
//        for (Cell c: actualMatrix) {
//            System.out.println("Row: " + c.getRowPos() + " Col: " + c.getColPos());
//            //c.getBlock().setMove(move);
//        }
        for (Block b: blocks) {
            b.setMove(move);
        }
    }

    public ArrayList<Cell> getActualMatrix() {
        return actualMatrix;
    }

    public void clearActualMatrix() {
        actualMatrix = new ArrayList<>();
    }

    public void addToActualMatrix(Cell c) {
        actualMatrix.add(c);
    }

    public void addToBlockMatrix(Block b) {
        blocks.add(b);
    }


    private ArrayList<Cell> setMatrixBlocks(ArrayList<Integer> truePos) {
        ArrayList<Cell> newMatrix = new ArrayList<>();
        for (int row = 0; row < MATRIX_NUM_ROWS; row++) {
            for (int col = 0; col < MATRIX_NUM_COLS; col++) {
                int currentMatrixIndex = row * MATRIX_NUM_ROWS + col;
                newMatrix.add(new Cell(row, col));

                if (truePos.contains(currentMatrixIndex)) {
                    Cell c = newMatrix.get(currentMatrixIndex);
//                    System.out.println("TruePos: " + currentMatrixIndex);
//                    System.out.println("Matrix: " + (c.getRowPos() * MATRIX_NUM_ROWS + c.getColPos()));
                    c.addBlock(new Block(type, c));
                    c.getBlock().lockBlock(c);
                }
            }
        }

        return newMatrix;
    }

    public ArrayList<Cell> rotateRight(ArrayList<Cell> originalMatrix) {
        ArrayList<Cell> rotatedMatrix = new ArrayList<>();
        int rowIndex = MATRIX_NUM_ROWS - 1;
        for (int col = 0; col < MATRIX_NUM_COLS; col++) {
            for (int row = rowIndex; row >= 0; row--) {
                rotatedMatrix.add(originalMatrix.get(row * 4 + col));
            }
        }

        if (rotation == 4) {
            rotation = 1;
        } else {
            rotation += 1;
        }

        return rotatedMatrix;
    }

    public ArrayList<Cell> rotateLeft(ArrayList<Cell> originalMatrix) {
        ArrayList<Cell> rotatedMatrix = new ArrayList<>();
        int colIndex = MATRIX_NUM_COLS - 1;
        for (int col = colIndex; col >= 0; col--) {
            for (int row = 0; row < MATRIX_NUM_ROWS; row++) {
                rotatedMatrix.add(originalMatrix.get(row * 4 + col));
            }
        }

        if (rotation == 1) {
            rotation = 4;
        } else {
            rotation -= 1;
        }

        return rotatedMatrix;
    }

    public void resetRotation() {
        while (rotation != 1) {
            rotateRight();
        }
    }

    public void lockPiece(Board b) {

    }

    public void fastDrop() {

    }

    public void moveLeft() {
    }

    public void moveRight(Board b) {



    }

    public void softDrop() {
    }

    public void rotateRight() {
        originalMatrix = rotateRight(originalMatrix);
    }

    public void rotateLeft() {
        originalMatrix = rotateLeft(originalMatrix);
    }

    public void setInPlay(boolean inPlay) {
        this.inPlay = inPlay;
    }

    public boolean getInPlay() {
        return inPlay;
    }

    @Override
    public void setExtrapolation(double extrapolate) {
        this.extrapolate = extrapolate;
        for (Cell c: originalMatrix) {
            c.setExtrapolation(extrapolate);
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g2) {

    }
}
