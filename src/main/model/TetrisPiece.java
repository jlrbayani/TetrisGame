package main.model;

import java.util.ArrayList;
import java.util.Random;

// TetrisPiece are the containers for Blocks and are what players take control of when playing the game
public class TetrisPiece {

    private int matrixNumRows;
    private int matrixNumCols;
    private int rotation;
    private Type type;
    private ArrayList<Cell> originalMatrix;
    private ArrayList<Cell> actualMatrix;
    private ArrayList<Integer> truePos;

    // an enum to represent the different types of TetrisPieces
    public enum Type {
        I, J, L, O, S, T, Z
    }

    // constructor that creates a random TetrisPiece out of the 7 types
    public TetrisPiece() {
        this.rotation = 1;
        this.type = chooseRandomType();
        setBlocks();
    }

    // constructor that creates a TetrisPiece based off the numType
    public TetrisPiece(int numType) {
        this.rotation = 1;
        this.type = setType(numType);
        setBlocks();
    }

    // constructor that creates a TetrisPiece based off the enum Type
    public TetrisPiece(Type type) {
        this.rotation = 1;
        this.type = type;
        setBlocks();
    }

    // EFFECTS: returns an enum Type which is randomized from 1 to 7
    private Type chooseRandomType() {
        Random random = new Random();
        return setType(random.nextInt(7) + 1);
    }

    // EFFECTS: based off the numType, returns a Type
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

    // MODIFIES: this
    // EFFECTS: this sets the initialization of TetrisPiece and determines the shape of it based of Type
    // originalMatrix is filled with this information based off truePos
    public void setBlocks() {
        originalMatrix = new ArrayList<>();
        actualMatrix = new ArrayList<>();
        truePos = new ArrayList<>();

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
    }


    public ArrayList<Cell> getOriginalMatrix() {
        return originalMatrix;
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

    // EFFECTS: based off actualMatrix this returns the highest col val representing the right edge
    public int getRightEdgeCol() {
        int edge = 0;

        for (Cell c: actualMatrix) {
            if (c.getColPos() > edge) {
                edge = c.getColPos();
            }
        }

        return edge;
    }

    // EFFECTS: based off actualMatrix this returns the lowest col val representing the left edge
    public int getLeftEdgeCol() {
        int edge = Integer.MAX_VALUE;

        for (Cell c: actualMatrix) {
            if (c.getColPos() < edge) {
                edge = c.getColPos();
            }
        }

        return edge;
    }

    // EFFECTS: returns an ArrayList<Cell> which would contain the positions of the original shape of the TetrisPiece
    private ArrayList<Cell> setMatrixBlocks(ArrayList<Integer> truePos) {
        ArrayList<Cell> newMatrix = new ArrayList<>();
        for (int row = 0; row < matrixNumRows; row++) {
            for (int col = 0; col < matrixNumCols; col++) {
                int currentMatrixIndex = row * matrixNumCols + col;
                newMatrix.add(new Cell(row, col));

                if (truePos.contains(currentMatrixIndex)) {
                    Cell c = newMatrix.get(currentMatrixIndex);
                    Block b = new Block(type, c);
                    c.addBlock(b);
                    c.getBlock().lockBlock(c);
                }
            }
        }

        return newMatrix;
    }

    // MODIFIES: this
    // EFFECTS: referring to board, actualMatrix is adjusted in relation to oldMatrix
    public void setActualMatrix(Board board, ArrayList<Cell> oldMatrix) {
        for (int i = 0; i < truePos.size(); i++) {
            int currentPos = truePos.get(i);

            int[] diff = getRowAndColDiff(oldMatrix, originalMatrix, currentPos);

            int rowDiff = diff[0];
            int colDiff = diff[1];
            if (actualMatrix.size() == 0) {
                return;
            }
            Cell oldActual = actualMatrix.get(0);
            actualMatrix.remove(0);
            try {
                actualMatrix.add(board.getCell(oldActual.getRowPos() + rowDiff, oldActual.getColPos() + colDiff));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    // EFFECTS: returns an int[] which contains the column and row difference between the old list of cells and the rotated list of cells at the current index truePos
    public int[] getRowAndColDiff(ArrayList<Cell> old, ArrayList<Cell> rotated, int truePos) {
        int[] arr = {0, 0};
        int oldIndex = 0, newIndex = 0;

        for (int i = 0; i < old.size(); i++) {
            if (old.get(i).getIndex(matrixNumCols) == truePos) {
                oldIndex = i;
            }
        }

        for (int i = 0; i < rotated.size(); i++) {
            if (rotated.get(i).getIndex(matrixNumCols) == truePos) {
                newIndex = i;
            }
        }

        int oldRow = calcRowOfIndex(oldIndex);
        int oldCol = calcColOfIndex(oldIndex);

        int newRow = calcRowOfIndex(newIndex);
        int newCol = calcColOfIndex(newIndex);

        arr[0] = newRow - oldRow;
        arr[1] = newCol - oldCol;

        return arr;
    }

    // EFFECTS: returns the new row based off the index
    public int calcRowOfIndex(int index) {
        int row = 0;
        while (index >= matrixNumCols) {
            index -= matrixNumCols;
            row++;
        }
        return row;
    }

    // EFFECTS: returns the new column based off the index
    public int calcColOfIndex(int index) {
        int col = index;
        while (col >= matrixNumCols) {
            col -= matrixNumCols;
        }
        return col;
    }

    // MODIFIES: this
    // EFFECTS: this returns the ArrayList<Cell> which represents a rotated originalMatrix 90 degrees to the right
    // this also adjusts the rotation of this TetrisPiece
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

    // MODIFIES: this
    // EFFECTS: this returns the ArrayList<Cell> which represents a rotated originalMatrix 90 degrees to the left
    // this also adjusts the rotation of this TetrisPiece
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

    // MODIFIES: this
    // EFFECTS: rotates this until rotation is reached to 1 which represents the originalMatrix built from truePos
    public void resetRotation() {
        while (rotation != 1) {
            rotateRight();
        }
    }

    // MODIFIES: this
    // EFFECTS: rotates this until rotation reaches the rotation parameter
    public void setRotation(int rotation) {
        while (this.rotation != rotation) {
            rotateRight();
        }
    }

    // EFFECTS: returns a TetrisPiece which copies everything about this, copies rotation and actualMatrix
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

    public void rotateRight() {
        originalMatrix = rotateRight(originalMatrix);

    }

    public void rotateLeft() {
        originalMatrix = rotateLeft(originalMatrix);
    }

    public int getMatrixNumRows() {
        return matrixNumRows;
    }

    public int getMatrixNumCols() {
        return matrixNumCols;
    }

    public Type getType() {
        return type;
    }

}
