package main.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class TetrisPiece extends Entity{

    private int matrixNumRows;
    private int matrixNumCols;
    private int rotation;
    private int topOffset, bottomOffset, leftOffset, rightOffset;
    private Dimension dimensions;
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

    public TetrisPiece(Type type) {
        this.rotation = 1;
        this.inPlay = false;
        this.type = type;
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
        topOffset = 0;
        bottomOffset = 0;
        leftOffset = 0;
        rightOffset = 0;

        switch (this.type) {
            case I:
                matrixNumRows = 4;
                matrixNumCols = 4;
                truePos.add(1);
                truePos.add(5);
                truePos.add(9);
                truePos.add(13);
                break;
            case J:
                matrixNumRows = 3;
                matrixNumCols = 3;
                truePos.add(1);
                truePos.add(4);
                truePos.add(6);
                truePos.add(7);
                break;
            case L:
                matrixNumRows = 3;
                matrixNumCols = 3;
                truePos.add(1);
                truePos.add(4);
                truePos.add(7);
                truePos.add(8);
                break;
            case O:
                matrixNumRows = 2;
                matrixNumCols = 2;
                truePos.add(0);
                truePos.add(1);
                truePos.add(2);
                truePos.add(3);
                break;
            case S:
                matrixNumRows = 3;
                matrixNumCols = 3;
                truePos.add(1);
                truePos.add(2);
                truePos.add(3);
                truePos.add(4);
                break;
            case T:
                matrixNumRows = 3;
                matrixNumCols = 3;
                truePos.add(1);
                truePos.add(3);
                truePos.add(4);
                truePos.add(5);
                break;
            case Z:
                matrixNumRows = 3;
                matrixNumCols = 3;
                truePos.add(0);
                truePos.add(1);
                truePos.add(4);
                truePos.add(5);
                break;
        }

        originalMatrix = setMatrixBlocks(truePos);
        dimensions = calculateDimensions();
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
        for (int row = 0; row < matrixNumRows; row++) {
            for (int col = 0; col < matrixNumCols; col++) {
                int currentMatrixIndex = row * matrixNumCols + col;
                newMatrix.add(new Cell(row, col));

                if (truePos.contains(currentMatrixIndex)) {
                    Cell c = newMatrix.get(currentMatrixIndex);
//                    System.out.println("TruePos: " + currentMatrixIndex);
//                    System.out.println("Matrix: " + (c.getRowPos() * MATRIX_NUM_ROWS + c.getColPos()));
                    Block b = new Block(type, c);
                    blocks.add(b);
                    c.addBlock(b);
                    c.getBlock().lockBlock(c);
                }
            }
        }

        return newMatrix;
    }

    public ArrayList<Cell> rotateRight(ArrayList<Cell> originalMatrix) {
        ArrayList<Cell> rotatedMatrix = new ArrayList<>();
        int rowIndex = matrixNumRows - 1;
        for (int col = 0; col < matrixNumCols; col++) {
            for (int row = rowIndex; row >= 0; row--) {
                rotatedMatrix.add(originalMatrix.get(row * matrixNumCols + col));
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
        int colIndex = matrixNumCols - 1;
        for (int col = colIndex; col >= 0; col--) {
            for (int row = 0; row < matrixNumRows; row++) {
                rotatedMatrix.add(originalMatrix.get(row * matrixNumCols + col));
            }
        }

        if (rotation == 1) {
            rotation = 4;
        } else {
            rotation -= 1;
        }

        return rotatedMatrix;
    }

    // MODIFIES: none
    // EFFECTS: Calculates the actual dimensions of the TetrisPiece rather than the matrix width and height to eventually help with offsetting its position on the board
    public Dimension calculateDimensions() {

        return new Dimension(calculateWidth(), calculateHeight());
    }

    public int calculateWidth() {
        int width = 0;
        int blocksFound = 0;
        int startingWidth = 0;
        int endingWidth = 1;
        for (int col = 0; col < matrixNumCols; col++) {
            for (int row = 0; row < matrixNumRows; row++) {
                Cell currentCell = originalMatrix.get(row * matrixNumCols + col);
//                System.out.println("Row: " + currentCell.getRowPos());
//                System.out.println("Col: " + currentCell.getColPos());
                if (currentCell.isFilled()) {
                    if(blocksFound == 0) {
                        startingWidth = col;
//                        System.out.println("Starting Width Index: " + startingWidth);
                    }
                    if(blocksFound == 3) {
                        endingWidth += col;
//                        System.out.println("Ending Width Index: " + endingWidth);
                    }
                    blocksFound++;
                }

                if (blocksFound == 4) {
                    break;
                }
            }
            if (blocksFound == 4) {
                break;
            }
        }
        if (startingWidth > 0) {
            leftOffset = 0;
            leftOffset += startingWidth;
        }
        if (endingWidth < matrixNumCols) {
            rightOffset = 0;
            rightOffset += matrixNumCols - endingWidth;
        }
//        System.out.println("leftOffset: " + leftOffset);
//        System.out.println("rightOffset: " + rightOffset);
        width = endingWidth - startingWidth;

        return width;
    }

    public int calculateHeight() {
        int height = 0;
        int blocksFound = 0;
        int startingHeight = 0;
        int endingHeight = 1;
        for (int row = 0; row < matrixNumRows; row++) {
            for (int col = 0; col < matrixNumCols; col++) {
                Cell currentCell = originalMatrix.get(row * matrixNumCols + col);
                if (currentCell.isFilled()) {
                    if(blocksFound == 0) {
                        startingHeight = row;
//                        System.out.println("Starting Height: " + startingHeight);
                    }
                    if(blocksFound == 3) {
                        endingHeight += row;
//                        System.out.println("Ending Height: " + endingHeight);
                    }

                    blocksFound++;
                }

                if (blocksFound == 4) {
                    break;
                }
            }
            if (blocksFound == 4) {
                break;
            }
        }

        if (startingHeight > 0) {
            topOffset = 0;
            topOffset += startingHeight;
        }

        if (endingHeight < matrixNumRows - 1) {
            bottomOffset = 0;
//            System.out.println("matrixNumRows - endingHeight: " + (matrixNumRows - endingHeight));
            bottomOffset += matrixNumRows - endingHeight;
        }
//        System.out.println("BottomOffset: " + bottomOffset);
        height = endingHeight - startingHeight;

        return height;
    }

    public void printOffsets() {
        System.out.println("leftOffset  : " + leftOffset);
        System.out.println("rightOffset : " + rightOffset);
        System.out.println("topOffset   : " + topOffset);
        System.out.println("BottomOffset: " + bottomOffset);
    }

    private void resetOffsets() {
        topOffset = 0;
        bottomOffset = 0;
        leftOffset = 0;
        rightOffset = 0;
    }

    public void printOriginalMatrix() {
        System.out.println("Original Matrix: ");
        int num = 0;
        for (Cell c: originalMatrix) {
            System.out.format("%3d   ", c.getRowPos() * matrixNumCols + c.getColPos());
            num++;
            if (num % matrixNumCols == 0) {
                System.out.println("");
            }
        }
    }


    public void resetRotation() {
        while (rotation != 1) {
            rotateRight();
        }
    }

    public void setRotation(int rotation) {
        while (this.rotation != rotation) {
            rotateRight();
        }
    }

    public TetrisPiece copyPiece() {
        TetrisPiece tpCopy = new TetrisPiece(type);
        tpCopy.setRotation(rotation);
        for (Cell c: actualMatrix) {
            tpCopy.addToActualMatrix(new Cell(c.getRowPos(), c.getColPos()));
        }

        return tpCopy;
    }

    public int getRotation() {
        return rotation;
    }

    public void lockPiece(Board board) {
        for(Block b: blocks) {
            b.lockBlock(board.getCell(b.getRowPos(), b.getColPos()));
        }
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
        resetOffsets();
        dimensions = calculateDimensions();
    }

    public void rotateLeft() {
        originalMatrix = rotateLeft(originalMatrix);
        resetOffsets();
        dimensions = calculateDimensions();
    }

    public void setActualMatrix(ArrayList<Cell> cells) {
        for (Cell c: cells) {
            addToActualMatrix(c);
        }
    }

    public void setInPlay(boolean inPlay) {
        this.inPlay = inPlay;
    }

    public boolean getInPlay() {
        return inPlay;
    }

    public int getMatrixNumRows() {
        return matrixNumRows;
    }

    public int getMatrixNumCols() {
        return matrixNumCols;
    }

    public Dimension getDimensions() {
        return dimensions;
    }

    public int getTopOffset() {
        return topOffset;
    }

    public int getBottomOffset() {
        return bottomOffset;
    }

    public int getLeftOffset() {
        return leftOffset;
    }

    public int getRightOffset() {
        return rightOffset;
    }

    public Type getType() {
        return type;
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
