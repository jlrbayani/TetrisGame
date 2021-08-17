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
    private boolean canMove;


    public Board(int numCols, int numRows, int actualX, int actualY, int pieceRow, int pieceCol) {
        this.numCols = numCols;
        this.numRows = numRows;

        this.actualX = actualX;
        this.actualY = actualY;

        this.pieceRow = pieceRow;
        this.pieceCol = pieceCol;
        canMove = false;

        initBoard();
        numMoveLeniency = 0;
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

    public boolean isValidCell(int row, int col) {

        return true;
    }

    public void clearCells(ArrayList<Cell> cells) {
        for (Cell c: cells) {
            //System.out.println("clearCells: " + c);
//            System.out.println("clearCells --> row: " + c.getRowPos() + " col: " + c.getColPos() + " index: " + c.getIndex(numCols));
            boardList.get(c.getIndex(numCols)).removeBlock();
        }
    }

    public void addTetrisPiece(TetrisPiece tp) {
        ArrayList<Cell> matrix = tp.getOriginalMatrix();
        tp.clearActualMatrix();
        int matrixNumRows = tp.getMatrixNumRows();
        int matrixNumCols = tp.getMatrixNumCols();
        //ArrayList<Cell> cellsToAddBlocks = new ArrayList<>();
        for (int row = 0; row < matrixNumRows; row++) {
            for (int col = 0; col < matrixNumCols; col++) {
                Cell currentCell = matrix.get(row * matrixNumCols + col);
                if (currentCell.isFilled()) {
                    int index = (row + pieceRow) * numCols + col + pieceCol;
                    if (index > boardList.size() - 1) {
                        pieceRow--;
                        index = (row + pieceRow) * numCols + col + pieceCol;
                    }
                    if (index < 0) {
                        index = 0;
                    }
                    Cell actualCell = boardList.get(index);
//                    if (actualCell == null) {
//                        System.out.println("Actual is null at: " + row * numRows + col);
//                    } else {
//                        System.out.println("Current Matrix Cell Index: " + currentCell.getIndex(TetrisPiece.MATRIX_NUM_ROWS));
//                        System.out.println("Actual Cell: " + actualCell.getIndex(numCols));
//                    }

                    if (actualCell != null) {
                        actualCell.addBlock(currentCell.getBlock());
                        //actualCell.getBlock().lockBlock(actualCell);
                        //System.out.println(currentCell.getBlock());
                        //tp.addToBlockMatrix(currentCell.getBlock());
//                        if (numRows == 21)
//                            System.out.println("addTetrisPiece: " + actualCell);
                        tp.addToActualMatrix(actualCell);
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

    public boolean shiftPieceRow(int offset, TetrisPiece tp) {
//        System.out.println("");
//        System.out.println("Shifting Down: ");
//        System.out.println("PieceRow                --> " + pieceRow);
//        System.out.println("NumRows                 --> " + numRows);
//        System.out.println("BottomOffset            --> " + tp.getBottomOffset());
//        System.out.println("PieceRow + bottomOffset --> " + (pieceRow + tp.getBottomOffset()));
//        System.out.println("TopOffset               --> " + tp.getTopOffset());
//        System.out.println("PieceRow + TopOffset    --> " + (pieceRow + tp.getTopOffset()));
//        System.out.println("Height                  --> " + (int) tp.getDimensions().getHeight());
        if (pieceRow + tp.getDimensions().getHeight() + tp.getTopOffset() == numRows) {
//            System.out.println("pieceRow + tp.getDimensions().getHeight() + tp.getBottomOffset()+ tp.getTopOffset(): " + (pieceRow + tp.getDimensions().getHeight() + tp.getBottomOffset() + tp.getTopOffset()));
            return false;
        }
        if (!checkMoveDown(tp)) {
            return false;
        }
        this.pieceRow += offset;
        return true;
    }

    public void shiftPieceRow(int offset) {
        if (pieceRow == numRows) {
            return;
        }
        this.pieceRow += offset;
    }

    public int getPieceRow() {
        return pieceRow;
    }

    public void shiftPieceCol(int offset, TetrisPiece tp) {
//        System.out.println("LeftOffset                --> " + tp.getLeftOffset());
//        System.out.println("PieceCol + leftOffset     --> " + (pieceCol + tp.getLeftOffset()));
//        System.out.println("RightOffset               --> " + tp.getRightOffset());
//        System.out.println("PieceCol + rightOffset    --> " + (pieceCol + tp.getRightOffset()));
        int leftOffset = tp.getLeftOffset();
        int rightOffset = tp.getRightOffset();
        int pieceWidth = (int) tp.getDimensions().getWidth();

        if ((pieceCol + leftOffset <= 0 && offset < 0)) {
            return;
        }
        if ((rightOffset == 0 && leftOffset != 0) || (rightOffset >= 0 && leftOffset >= 0)) {
            if (pieceCol + pieceWidth + leftOffset >= numCols && offset > 0) {
//                System.out.println("");
//                System.out.println("First Case");
//                System.out.println("pieceCol + tp.getDimensions().getWidth() + tp.getLeftOffset(): " + (pieceCol + tp.getDimensions().getWidth() + tp.getLeftOffset()));
                return;
            }
        } else if (leftOffset == 0) {
            if (pieceCol + pieceWidth >= numCols && offset > 0) {
//                System.out.println("");
//                System.out.println("Second Case");
//                System.out.println("pieceCol + tp.getDimensions().getWidth(): " + (pieceCol + tp.getDimensions().getWidth()));
                return;
            }
        } else {
            if ((pieceCol + pieceWidth + rightOffset + leftOffset >= numCols && offset > 0)) {
//                System.out.println("");
//                System.out.println("Last Case");
//                System.out.println("pieceCol + tp.getDimensions().getWidth() + tp.getRightOffset() + tp.getLeftOffset(): " + (pieceCol + tp.getDimensions().getWidth() + tp.getRightOffset() + tp.getLeftOffset()));
                return;
            }
        }

        if (!checkMoveSide(tp, offset)) {
            return;
        }


//        if (tp.getLeftOffset() >= tp.getRightOffset()) {
//            if ((pieceCol + tp.getDimensions().getWidth() - tp.getRightOffset() + tp.getLeftOffset() >= numCols && offset > 0)) {
//                return;
//            }
//        } else if (tp.getLeftOffset() < tp.getRightOffset()) {
//            if ((pieceCol + tp.getDimensions().getWidth() + tp.getRightOffset() + tp.getLeftOffset() >= numCols && offset > 0)) {
//                return;
//            }
//        }

        this.pieceCol += offset;
//        System.out.println("");
//        System.out.println("Shifting To Side ");
//        System.out.println("New PieceCol              --> " + pieceCol);
//        System.out.println("NumCols                   --> " + numCols);
//        System.out.println("Width                     --> " + pieceWidth);
    }

    public void shiftPieceCol(int offset) {
        if ((pieceCol == 0 && offset < 0) || (pieceCol == numCols && offset > 0)) {
            return;
        }
        this.pieceCol += offset;
    }

    public int numMoveLeniency;

    public void rotatePiece(TetrisPiece tp, boolean rotateRight) {
        if (tp == null) {
            return;
        }
        int widthBefore = (int) tp.getDimensions().getWidth();
        TetrisPiece copy = tp.copyPiece();
        if (rotateRight) {
            tp.rotateRight();
        } else {
            tp.rotateLeft();
        }

//        boolean found = findOptimalPlacement(copy);
//        if (found) {
//            if (rotateRight) {
//                tp.rotateRight();
//            } else {
//                tp.rotateLeft();
//            }
//        } else {
//            return;
//        }

//        while (!checkRotation(tp) && numMoveLeniency < 3) {
//            if (rotateRight) {
//                tp.rotateLeft();
//            } else {
//                tp.rotateRight();
//            }
//            System.out.println("pieceRow: " + pieceRow);
//            pieceRow--;
//            numMoveLeniency++;
//            rotatePiece(tp, rotateRight);
//        }

        int oldPieceCol = pieceCol;
        if (!checkRotation(tp)) {
            if (rotateRight) {
                tp.rotateLeft();
            } else {
                tp.rotateRight();
            }
//            pieceRow--;
//            rotatePiece(tp, rotateRight);
            System.out.println("Return to rotation before");
            if (pieceCol != oldPieceCol) {
                System.out.println("PieceCol changed!");
                pieceCol = oldPieceCol;
            }
            return;
        }

        int newWidth = (int) tp.getDimensions().getWidth();

//        System.out.println("");
//        System.out.println("NewWidth:               " + newWidth);
//        System.out.println("PieceCol:               " + pieceCol);
//        System.out.println("WidthBefore:            " + widthBefore);
//        System.out.println("PieceCol + newWidth:    " + (pieceCol + newWidth));
//        System.out.println("NumCols:                " + numCols);
//        System.out.println("PieceCol:               " + pieceCol);
//        System.out.println("WidthBefore:            " + widthBefore);

        if (widthBefore <= newWidth && (pieceCol + newWidth > numCols)) {
            //pieceCol -= newWidth - widthBefore;
            pieceCol = numCols - newWidth;
            System.out.println("First Case");
//            System.out.println("New PieceCol:       " + pieceCol);
        }
        if (widthBefore <= newWidth && (pieceCol - newWidth < 0) && pieceCol < newWidth - widthBefore) {
            System.out.println("pieceCol - newWidth: " + (pieceCol - newWidth));
            pieceCol = 0;
            System.out.println("Second Case");
//            System.out.println("New PieceCol:       " + pieceCol);
        }

    }

    public boolean findOptimalPlacement(TetrisPiece tp) {
        ArrayList<Cell> originalMatrix = tp.getOriginalMatrix();
        int matrixNumRows = tp.getMatrixNumRows();
        int matrixNumCols = tp.getMatrixNumCols();
        int width = (int) tp.getDimensions().getWidth();
        int height = (int) tp.getDimensions().getHeight();

        if (!validatePiecePlacement(tp)) {
            if (checkMoveUp(tp)) {
                System.out.println("Validating to Up");
                pieceRow--;
                return true;
            }
            if (checkMoveSide(tp, -1)) {
                System.out.println("Validating to Left");
                pieceCol--;
                return true;
            }
            if (checkMoveSide(tp, 1)) {
                System.out.println("Validating to Right");
                pieceCol++;
                return true;
            }
        } else {
            return true;
        }

        return false;
    }
    public boolean validatePiecePlacement(TetrisPiece tp) {
        ArrayList<Cell> originalMatrix = tp.getOriginalMatrix();
        int matrixNumRows = tp.getMatrixNumRows();
        int matrixNumCols = tp.getMatrixNumCols();
        for (int row = 0; row < matrixNumRows; row++) {
            for (int col = 0; col < matrixNumCols; col++) {
                Cell currentCell = originalMatrix.get(row * matrixNumCols + col);
                if (currentCell.isFilled()) {
                    int index = (row + pieceRow) * numCols + col + pieceCol;
                    if (index >= (numRows * numCols)) {
                        return false;
                    }
                    Cell actualCell = boardList.get(index);
                    if (actualCell.isFilled() && !tp.getActualMatrix().contains(actualCell)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean checkRotation(TetrisPiece tp) {

        return validatePiecePlacement(tp);
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

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
        setCellsCanMove();
    }

    public void setCellsCanMove() {
        for (Cell c: boardList) {
            c.setCanMove(canMove);
        }
    }

    public Cell getCell(int rowPos, int colPos) {
        return boardList.get(rowPos * numCols + colPos);
    }

    public boolean checkIsValidPieceRow(TetrisPiece tp) {
        for (Cell c: tp.getActualMatrix()) {

        }

        return true;
    }

    public void validateStartingPieceCol(TetrisPiece tp) {
        int width = (int) tp.getDimensions().getWidth();
        if (pieceCol + width >= numCols) {
            pieceCol = numCols - width;
        }
        if (pieceCol < 0) {
            pieceCol = 0;
        }
    }

    public boolean checkMoveUp(TetrisPiece tp) {
        ArrayList<Cell> cells = tp.getActualMatrix();
//        System.out.println(cells);
        for (Cell c: cells) {
            int index = c.getIndex(numCols) - numCols;
            if (index < 0) {
                return false;
            }
            Cell cellBelow = boardList.get(index);
            if (cellBelow.isFilled() && !cells.contains(cellBelow)) {
                return false;
            }
        }

        return true;
    }


    public boolean checkMoveDown(TetrisPiece tp) {
        ArrayList<Cell> cells = tp.getActualMatrix();
//        System.out.println(cells);
        for (Cell c: cells) {
            Cell cellBelow = boardList.get(c.getIndex(numCols) + (numCols));
            if (cellBelow.isFilled() && !cells.contains(cellBelow)) {
                return false;
            }
        }

        return true;
    }

    public boolean checkMoveSide(TetrisPiece tp, int move) {
        ArrayList<Cell> cells = tp.getActualMatrix();
//        System.out.println(cells);
        for (Cell c: cells) {
            Cell adjacentCell = boardList.get(c.getIndex(numCols) + move);
            if (adjacentCell.isFilled() && !cells.contains(adjacentCell)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "Board{" +
                "numCols=" + numCols +
                ", numRows=" + numRows +
                '}';
    }

}
