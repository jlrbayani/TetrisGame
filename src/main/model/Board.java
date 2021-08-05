package main.model;

import java.awt.*;
import java.util.ArrayList;

public class Board extends Entity {

    private int numCols;
    private int numRows;
    private ArrayList<Cell> boardList;
    private int actualX, actualY;
    private double extrapolate;
    private int pieceRow, pieceCol;


    public Board(int numCols, int numRows, int actualX, int actualY, int pieceRow, int pieceCol) {
        this.numCols = numCols;
        this.numRows = numRows;

        this.actualX = actualX;
        this.actualY = actualY;

        this.pieceRow = pieceRow;
        this.pieceCol = pieceCol;

        initBoard();
        // for testing purposes
        //printBoard();
    }

    @Override
    public void setExtrapolation(double extrapolate) {
        if (!isPaused) {
            for (Cell c : boardList) {
                c.setExtrapolation(extrapolate);
            }
        }
    }

    @Override
    public void update() {
        if (!isPaused) {
            for (Entity e : boardList) {
                e.update();
            }

        }
    }

    @Override
    public void draw(Graphics2D g2) {
        for (Entity e: boardList) {
            e.draw(g2);
        }
    }

    public void initBoard() {
        boardList = new ArrayList<>();
        int currentX = actualX;
        int currentY = actualY;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                boardList.add(new Cell(i, j, currentX, currentY, this));
                currentX += Cell.SIZE;
            }
            currentY += Cell.SIZE;
            currentX = actualX;
        }
    }

    public void addTetrisPiece(TetrisPiece tp) {
        ArrayList<Cell> matrix = tp.getOriginalMatrix();
        for (int row = 0; row < TetrisPiece.MATRIX_NUM_ROWS; row++) {
            for (int col = 0; col < TetrisPiece.MATRIX_NUM_COLS; col++) {
                Cell currentCell = matrix.get(row * TetrisPiece.MATRIX_NUM_COLS + col);
                if (currentCell.isFilled()) {
                    Cell actualCell = boardList.get((row + pieceRow) * numCols + col);
//                    if (actualCell == null) {
//                        System.out.println("Actual is null at: " + row * numRows + col);
//                    } else {
//                        System.out.println("Current Matrix Cell Index: " + currentCell.getIndex(TetrisPiece.MATRIX_NUM_ROWS));
//                        System.out.println("Actual Cell: " + actualCell.getIndex(numCols));
//                    }
                    if (actualCell != null) {
                        actualCell.addBlock(currentCell.getBlock());
                        actualCell.getBlock().lockBlock(actualCell);
                        //System.out.println(currentCell.getBlock());
                        tp.addToBlockMatrix(currentCell.getBlock());
                    }
                }
            }
        }
    }

    public void setPieceRow(int pieceRow) {
        this.pieceRow = pieceRow;
    }

    public void setPieceCol(int pieceCol) {
        this.pieceCol = pieceCol;
    }

    public void shiftPieceRow(int offset) {
        this.pieceRow += offset;
    }

    public int getPieceRow() {
        return pieceRow;
    }

    public void shiftPieceCol(int offset) {
        this.pieceCol += offset;
    }

    public int getPieceCol() {
        return pieceCol;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }


    public void clearBoard() {
        for (Cell c: boardList) {
            c.removeBlock();
        }
    }

    public void printBoard() {
        System.out.println("Board: ");
        for (int i = 0; i < boardList.size(); i++) {
            if (i % (numCols) == 0 && i > 0) {
                System.out.println();
            }
            System.out.format("%3d  ", (boardList.get(i).getRowPos() * numCols + boardList.get(i).getColPos()));
            //System.out.print((boardList.get(i).getRowPos() + (boardList.get(i).getColPos() * (numCols))) + "       ");
        }

//        System.out.println();
//        System.out.println("Board: ");
//        for (int i = 0; i < numRows; i++) {
//            for (int j = 0; j < numCols; j++) {
//                System.out.print(j + (i * numCols) + "    ");
//            }
//            System.out.println();
//        }
    }

    public Cell getCell(int rowPos, int colPos) {
        return boardList.get(rowPos * numCols + colPos);
    }

    @Override
    public String toString() {
        return "Board{" +
                "numCols=" + numCols +
                ", numRows=" + numRows +
                '}';
    }
}
