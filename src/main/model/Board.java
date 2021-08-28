package main.model;

import java.awt.*;
import java.util.ArrayList;

public class Board extends Entity {

    private int numCols;
    private int numRows;
    private ArrayList<Cell> boardList;
    private ArrayList<Cell> ghostList;
    private int actualX, actualY;
    private double extrapolate;
    private int pieceRow, pieceCol;
    private boolean canMove;

    private static final int[][] wallKickRightDefault = {
                                                    {-1, 0}, {-1, -1}, {0, 2}, {-1, 2},   // 1 --> 2
                                                    {1, 0}, {1, 1}, {0, -2}, {1, -2},     // 2 --> 3
                                                    {1, 0}, {1, -1}, {0, 2}, {1, 2},      // 3 --> 4
                                                    {-1, 0}, {-1, 1}, {0, -2}, {-1, 2}    // 4 --> 1
                                                    };

    private static final int[][] wallKickRightI = {
                                                    {-2, 0}, {1, 0}, {-2, 1}, {1, -2},   // 1 --> 2
                                                    {-1, 0}, {2, 0}, {-1, -2}, {2, 1},   // 2 --> 3
                                                    {2, 0}, {-1, 0}, {2, -1}, {-1, 2},   // 3 --> 4
                                                    {1, 0}, {-2, 0}, {1, 2}, {-2, -1}    // 4 --> 1
                                                    };


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
        for (Cell c: ghostList) {
            if (boardList.contains(c)) {
                c.draw(g2);
            }
        }
    }

    public void initBoard() {
        boardList = new ArrayList<>();
        ghostList = new ArrayList<>();
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

    public void checkLineClear() {
        for (int row = 0; row < numRows; row++) {
            ArrayList<Cell> checkCells = new ArrayList<>();
            for (int col = 0; col < numCols; col++) {
                Cell currentCell = boardList.get(row * numCols + col);
                if (currentCell.isFilled()) {
                    checkCells.add(currentCell);
                }
            }

            if (checkCells.size() == numCols) {
                clearCells(checkCells);
                shiftBlocks(row);
            }
        }
    }

    public void shiftBlocks(int endRow) {
        int currentRow = endRow;
        while (currentRow > 0) {
            currentRow--;
            for (int col = 0; col < numCols; col++) {
                Cell currentCell = boardList.get(currentRow * numCols + col);
                Cell cellBelow = boardList.get((currentRow + 1) * numCols + col);
                if (currentCell.isFilled()) {
                    Block b = new Block(currentCell.getBlock().getBlockType(), currentCell);
                    cellBelow.addBlock(b);
                    currentCell.removeBlock();
                }
            }
        }
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

    public boolean initPieceToBoard(TetrisPiece tp) {
        ArrayList<Cell> matrix = tp.getOriginalMatrix();
        int matrixNumRows = tp.getMatrixNumRows();
        int matrixNumCols = tp.getMatrixNumCols();
        tp.clearActualMatrix();
        ArrayList<Cell> moveTo = new ArrayList<>();

        for (int row = 0; row < matrixNumRows; row++) {
            for (int col = 0; col < matrixNumCols; col++) {
                Cell currentCell = matrix.get(row * matrixNumCols + col);
                if (currentCell.isFilled()) {
                    int index = (row + pieceRow) * numCols + col + pieceCol;
                    Cell actualCell = boardList.get(index);
                    if (actualCell != null) {
                        actualCell.addBlock(currentCell.getBlock());
                        tp.addToActualMatrix(actualCell);
                    }
                }
            }
        }

        return tp.getActualMatrix().size() == 4;
    }

    public synchronized void fastDropPiece(TetrisPiece tp) {
        addPieceToBoard(tp, ghostList);
    }

    public synchronized void updateGhostCells(TetrisPiece tp) {
        if (tp == null) {
            return;
        }
        TetrisPiece copy = tp.copyPiece();
        for (Cell c: ghostList) {
            c.setIsGhost(false);
        }
        ghostList = new ArrayList<>();
        ArrayList<Cell> ghostCellsBefore = copy.getActualMatrix();
        ArrayList<Cell> ghostCellsCurrent = copy.getActualMatrix();
        int index = 0;
        while (checkPieceMove(tp, ghostCellsCurrent)) {
//            System.out.println(ghostCellsCurrent.size());
            ghostCellsBefore = ghostCellsCurrent;
            ghostCellsCurrent = getMoveTo(ghostCellsCurrent, 0, 1);
            index++;
        }

//        System.out.println("");
        for (Cell c: ghostCellsBefore) {
            c.setIsGhost(true);
//            System.out.println(c);
        }
        ghostList = ghostCellsBefore;
    }

    public synchronized void updatePieceInBoard(TetrisPiece tp) {
        for (Cell c: tp.getActualMatrix()) {
            Block b = c.getBlock();
            boardList.get(c.getIndex(numCols)).addBlock(b);
        }
    }

    public synchronized void rotatePieceInPlay(TetrisPiece tp, boolean rotateRight) {
        if (tp.getType() == TetrisPiece.Type.O) {
            return;
        }
//        System.out.println("Before rotate: ");
//        for (Cell c: tp.getActualMatrix()) {
//            System.out.println(c);
//        }
//        System.out.println("");
//        System.out.println("Original Matrix: ");
//        tp.printOriginalMatrix();
//        System.out.println("");

//        System.out.println("Actual Matrix: ");
//        tp.printActualMatrix();
        TetrisPiece tester = tp.copyPiece();
//        System.out.println("Copy Rotation Before: " + tester.getRotation());
        if (rotateRight) {
            tester.rotateRight();
        } else {
            tester.rotateLeft();
        }
//        System.out.println("Rotated Original Matrix: ");
//        tester.printOriginalMatrix();
//        System.out.println("");

//        System.out.println("Copy Rotation After: " + tester.getRotation());
        tester.setActualMatrix(this, tp.getOriginalMatrix(), rotateRight);
//        System.out.println("Actual Matrix: ");
//        tester.printActualMatrix();
        ArrayList<Cell> moveTo = tester.getActualMatrix();
        if (tp.getType() == TetrisPiece.Type.I && checkRowOverflow(tp, moveTo)) {
            //System.out.println("Kicking from wall!");
            moveTo = applyEdgeKickI(tp, moveTo);
        }

        if (checkPieceMove(tp, moveTo) && !checkRowOverflow(tp, moveTo)) {
//            tp.rotateRight();
            tp.setRotation(tester.getRotation());
            addPieceToBoard(tp, moveTo);
        } else {
            for (int numTries = 0; numTries < 4; numTries++) {
                moveTo = applyWallKick(tp, tester, numTries, rotateRight);
                if (checkPieceMove(tp, moveTo) && !checkRowOverflow(tp, moveTo)) {
                    tp.setRotation(tester.getRotation());
                    addPieceToBoard(tp, moveTo);
                    break;
                }
            }
        }
//        System.out.println("");
//
//        System.out.println("After rotate: ");
//        for (Cell c: tp.getActualMatrix()) {
//            System.out.println(c);
//        }
//        System.out.println("");
//        System.out.println(tp.getRotation());

    }

    public ArrayList<Cell> applyEdgeKickI(TetrisPiece tp , ArrayList<Cell> moveTo) {
        ArrayList<Cell> actualMatrix = tp.getActualMatrix();
        int index = 0;
        while (index < moveTo.size()) {
            int diff;
            try {
                Cell currentCell = actualMatrix.get(index);
                Cell moveCell = moveTo.get(index);
                if (moveCell == null) {
                    return null;
                }
                diff = currentCell.getColPos() - moveCell.getColPos();
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
            System.out.println("diff: " + diff);
//            if (diff > 4 || diff < -4) {
//                System.out.println("");
//                System.out.println("pushed!");
//                Cell currentCell = moveTo.get(index);
//                moveTo.remove(0);
//                diff = numCols - Math.abs(diff);
//                System.out.println( boardList.get(currentCell.getIndex(numCols) + diff));
//                moveTo.add(0, boardList.get(currentCell.getIndex(numCols) + diff));
            if (diff > 4) {
                moveTo = getMoveToOverflow(moveTo, diff, 0);
                for (Cell c: moveTo) {
                    System.out.println(c);
                }
            } else if (diff < -4){
                moveTo = getMoveToOverflow(moveTo, -(numCols - diff), 0);
                for (Cell c: moveTo) {
                    System.out.println(c);
                }
            }
            index++;
        }

        return moveTo;
    }

    public boolean checkRowOverflow(TetrisPiece tp, ArrayList<Cell> moveTo) {
        if (moveTo.size() != 4) {
            return true;
        }
        int index = 0;
        ArrayList<Cell> actualMatrix = tp.getActualMatrix();
//        System.out.println("");
        while (index < moveTo.size()) {
            int diff;
            try {
                Cell currentCell = actualMatrix.get(index);
                Cell moveCell = moveTo.get(index);
                if (moveCell == null) {
                    return true;
                }
                diff = currentCell.getColPos() - moveCell.getColPos();
            } catch (NullPointerException e) {
                e.printStackTrace();
                return true;
            }
//            System.out.println("diff: " + diff);
            if (diff > 4 || diff < -4) {
//                System.out.println(numCols - (Math.abs(diff)));
                return true;
            }

            index++;
        }

        return false;
    }

    public ArrayList<Cell> applyWallKick(TetrisPiece tp, TetrisPiece copy, int numTries, boolean rotateRight) {
        int currentRotation = tp.getRotation();
        int kickIndex = ((currentRotation - 1) * 4) + numTries;
        ArrayList<Cell> moveTo;
        int rowChange, colChange;

//        System.out.println("kickIndex: " + kickIndex);
        if (tp.getType() == TetrisPiece.Type.I) {
            if (rotateRight) {
                colChange = wallKickRightI[kickIndex][0];
                rowChange = wallKickRightI[kickIndex][1];
            } else {
                colChange = -wallKickRightI[kickIndex][0];
                rowChange = -wallKickRightI[kickIndex][1];
            }
        } else {
            if (rotateRight) {
                colChange = wallKickRightDefault[kickIndex][0];
                rowChange = wallKickRightDefault[kickIndex][1];
            } else {
                colChange = -wallKickRightDefault[kickIndex][0];
                rowChange = -wallKickRightDefault[kickIndex][1];
            }
        }

        moveTo = getMoveTo(copy, colChange, rowChange);

        return moveTo;
    }

    public synchronized void movePieceSide(TetrisPiece tp, int move) {
        ArrayList<Cell> moveTo = getMoveTo(tp, move, 0);
        if (checkPieceMove(tp, moveTo)) {
            addPieceToBoard(tp, moveTo);
        }

    }

    public synchronized boolean movePieceDown(TetrisPiece tp) {
        ArrayList<Cell> moveTo = getMoveTo(tp, 0, 1);
        if (checkPieceMove(tp, moveTo)) {
            addPieceToBoard(tp, moveTo);
            return true;
        }

        return false;
    }


    // REQUIRES: moveTo must be verified using checkPieceMove
    public synchronized void addPieceToBoard(TetrisPiece tp, ArrayList<Cell> moveTo) {
        ArrayList<Cell> oldMatrix = tp.getActualMatrix();
        clearCells(oldMatrix);
        tp.clearActualMatrix();

        for (Cell c: moveTo) {
            Cell newCell = getCell(c.getRowPos(), c.getColPos());
            newCell.addBlock(new Block(tp.getType(), newCell));
            tp.addToActualMatrix(newCell);
        }

    }

    public synchronized ArrayList<Cell> getMoveTo(TetrisPiece tp, int colChange, int rowChange) {
        return getMoveTo(tp.getActualMatrix(), colChange, rowChange);
    }

    public synchronized ArrayList<Cell> getMoveTo(ArrayList<Cell> actualMatrix, int colChange, int rowChange) {
        ArrayList<Cell> moveTo = new ArrayList<>();

        for (Cell c: actualMatrix) {
            if (c == null) {
                return new ArrayList<>();
            }

            int moveSide = c.getColPos() + pieceCol + colChange;

            if (moveSide >= numCols || moveSide < 0) {
                return new ArrayList<>();
            }

            int index = (c.getRowPos() + pieceRow + rowChange) * numCols + (moveSide);

            if (index >= numCols * numRows || index < 0) {
                return new ArrayList<>();
            }

            moveTo.add(boardList.get(index));
        }

        return moveTo;
    }

    public synchronized ArrayList<Cell> getMoveToOverflow(ArrayList<Cell> actualMatrix, int colChange, int rowChange) {
        ArrayList<Cell> moveTo = new ArrayList<>();

        for (Cell c: actualMatrix) {
            if (c == null) {
                return new ArrayList<>();
            }

            int moveSide = c.getColPos() + pieceCol + colChange;

            int index = (c.getRowPos() + pieceRow + rowChange) * numCols + (moveSide);

            if (index >= numCols * numRows || index < 0) {
                return new ArrayList<>();
            }

            moveTo.add(boardList.get(index));
        }

        return moveTo;
    }

    public boolean checkPieceMove(TetrisPiece tp, ArrayList<Cell> moveTo) {
        if (moveTo == null || moveTo.size() == 0 || tp.getActualMatrix().size() != moveTo.size()) {
            return false;
        }

        for (Cell moveToCurrentCell : moveTo) {
            //            System.out.println("diff: " + Math.abs(moveToCurrentCell.getColPos() - actualCurrentCell.getColPos()));
//            if (Math.abs(moveToCurrentCell.getColPos() - actualCurrentCell.getColPos()) > numCols - 2) {
//                return false;
//            }

            if (moveToCurrentCell == null || (moveToCurrentCell.isFilled() && !tp.getActualMatrix().contains(moveToCurrentCell))) {
                return false;
            }
        }

//        for (Cell c: moveTo) {
//            colIndex = c.getColPos();
//            if (c.isFilled() && !tp.getActualMatrix().contains(c)) {
//                return false;
//            }
//        }

        return true;
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

    public void wallKickRotRight() {

    }

    // TODO: find a way to implement a better way of checking filled cells and consequently bounds/walls
    public void wallKickRotationRight(TetrisPiece tp) {
        ArrayList<Cell> oldCells = new ArrayList<>();
        boolean found = false;
        int oldWidth = (int) tp.getDimensions().getWidth();

        for (Cell c: tp.getActualMatrix()) {
            oldCells.add(boardList.get(c.getIndex(numCols)));
        }
        System.out.println("Check: ");
        cellCheckValidity(tp);

//        for (Cell c: oldCells) {
//            System.out.println("OldCells: " + c.getIndex(numCols));
//        }
//        System.out.println("");

        switch (tp.getType()) {
            case O:
                tp.rotateRight();
                return;
            case I:
                tp.rotateRight();
                System.out.println("Check: ");
                cellCheckValidity(tp);
                if (adjustBoundsRotate(tp, oldWidth)) {
                    System.out.println("Bounds adjusted!");
                } else {
                    System.out.println("Bounds NOT adjusted!");
                }
                if (tp.getRotation() == 1) {
                    if (validatePiecePlacement(tp)) {
                        if (adjustBoundsRotate(tp, oldWidth)) {
                            System.out.println("Bounds adjusted!");
                        } else {
                            System.out.println("Bounds NOT adjusted!");
                        }
                        found = true;
                    } else {
                        for (int i = 0; i < 4; i++) {
                            System.out.println("");
                            System.out.println("Case:        " + i);
                            System.out.println("");
                            System.out.println("kickCol:     " +  wallKickRightI[i][0]);
                            System.out.println("kickRow:     " + wallKickRightI[i][1]);
                            System.out.println("kick before: ");
                            printPieceRowAndCol();
                            shiftPieceCol(wallKickRightI[i][0], tp);
                            shiftPieceRow(wallKickRightI[i][1], tp);
//                            pieceCol += getWallKickRightI[i][0];
//                            pieceRow += getWallKickRightI[i][1];
                            System.out.println("kick applied: ");
                            printPieceRowAndCol();
//                            if (pieceCol < 0) {
//                                System.out.println("Case: " + i + ".1");
//                                pieceCol = 0;
//                            } else if (pieceCol + (int) tp.getDimensions().getWidth() > numCols) {
//                                System.out.println("Case: " + i + ".2");
//                                pieceCol = numCols - (int) tp.getDimensions().getWidth();
//                            }
                            if (validatePiecePlacement(tp)) {
                                found = true;
                                printPieceRowAndCol();
                                break;
                            } else {
//                                pieceCol -= getWallKickRightI[i][0];
//                                pieceRow -= getWallKickRightI[i][1];
                                shiftPieceCol(-wallKickRightI[i][0], tp);
                                shiftPieceRow(-wallKickRightI[i][1], tp);
                                System.out.println("kick removed: ");
                                printPieceRowAndCol();
                            }
                        }
                    }
                } else if (tp.getRotation() == 2) {
                    if (validatePiecePlacement(tp)) {
                        found = true;
                    } else {
                        for (int i = 4; i < 8; i++) {
                            System.out.println("");
                            System.out.println("Case:        " + i);
                            System.out.println("");
                            System.out.println("kickCol:     " +  wallKickRightI[i][0]);
                            System.out.println("kickRow:     " + wallKickRightI[i][1]);
                            System.out.println("kick before: ");
                            printPieceRowAndCol();
//                            pieceCol += getWallKickRightI[i][0];
//                            pieceRow += getWallKickRightI[i][1];
                            shiftPieceCol(wallKickRightI[i][0], tp);
                            shiftPieceRow(wallKickRightI[i][1], tp);
                            System.out.println("kick applied: ");
                            printPieceRowAndCol();
                            if (validatePiecePlacement(tp)) {
                                found = true;
                                printPieceRowAndCol();
                                break;
                            } else {
//                                pieceCol -= getWallKickRightI[i][0];
//                                pieceRow -= getWallKickRightI[i][1];
                                shiftPieceCol(-wallKickRightI[i][0], tp);
                                shiftPieceRow(-wallKickRightI[i][1], tp);
                                System.out.println("kick removed: ");
                                printPieceRowAndCol();
                            }
                        }
                    }
                } else if (tp.getRotation() == 3) {
                    if (validatePiecePlacement(tp)) {
                        found = true;
                    } else {
                        for (int i = 8; i < 12; i++) {
                            System.out.println("Case: " + i);
                            shiftPieceCol(wallKickRightI[i][0], tp);
                            shiftPieceRow(wallKickRightI[i][1], tp);
//                            pieceCol += getWallKickRightI[i][0];
//                            pieceRow += getWallKickRightI[i][1];
                            if (pieceCol < 0) {
                                pieceCol = 0;
                            } else if (pieceCol + (int) tp.getDimensions().getWidth()  > numCols) {
                                pieceCol = numCols - (int) tp.getDimensions().getWidth();
                            }
                            if (validatePiecePlacement(tp)) {
                                found = true;
                                break;
                            } else {
//                                pieceCol -= getWallKickRightI[i][0];
//                                pieceRow -= getWallKickRightI[i][1];
                                shiftPieceCol(-wallKickRightI[i][0], tp);
                                shiftPieceRow(-wallKickRightI[i][1], tp);
                            }
                        }
                    }
                } else {
                    if (validatePiecePlacement(tp)) {
                        found = true;
                    } else {
                        for (int i = 12; i < 15; i++) {
                            System.out.println("Case: " + i);
                            shiftPieceCol(wallKickRightI[i][0], tp);
                            shiftPieceRow(wallKickRightI[i][1], tp);
//                            pieceCol += getWallKickRightI[i][0];
//                            pieceRow += getWallKickRightI[i][1];
                            if (validatePiecePlacement(tp)) {
                                found = true;
                                break;
                            } else {
//                                pieceCol -= getWallKickRightI[i][0];
//                                pieceRow -= getWallKickRightI[i][1];
                                shiftPieceCol(-wallKickRightI[i][0], tp);
                                shiftPieceRow(-wallKickRightI[i][1], tp);
                            }
                        }
                    }
                }

                break;
            default:
                if (tp.getRotation() == 1) {


                } else if (tp.getRotation() == 2) {

                } else if (tp.getRotation() == 3) {

                } else {

                }
        }

//        for (Cell c: oldCells) {
//            System.out.println("OldCells: " + c.getIndex(numCols));
//        }
//        System.out.println("");



        if (!found) {
            System.out.println("Not Found");
            tp.rotateLeft();
            tp.setActualMatrix(oldCells);
            System.out.println("After return: ");
            for (Cell c: oldCells) {
                System.out.println(c);
            }


        }
    }

    private void printPieceRowAndCol() {
        System.out.println("");
        System.out.println("pieceCol: " + pieceCol);
        System.out.println("pieceRow: " + pieceRow);
    }

    // EFFECTS: if this method returns true then the piece is adjusted
    //          else the method returns false to note that nothing was changed
    public boolean adjustBoundsRotate(TetrisPiece tp, int widthBefore) {
        int newWidth = (int) tp.getDimensions().getWidth();
        if (widthBefore <= newWidth && (pieceCol + newWidth > numCols)) {
            pieceCol = numCols - newWidth;
            System.out.println("First Case");
            System.out.println("New PieceCol:       " + pieceCol);
            System.out.println("Width:              " + newWidth);
            return true;
        }
        if (widthBefore <= newWidth && (pieceCol - newWidth <= 0) && pieceCol < newWidth - widthBefore) {
            System.out.println("Second Case");
            System.out.println("New PieceCol:       " + pieceCol);
            System.out.println("Width:              " + newWidth);
            System.out.println("PieceCol - newWidth: " + (pieceCol - newWidth));
            pieceCol = 0;
            return true;
        }

        System.out.println("False case");
        return false;
    }

    public void findOptimalCells(TetrisPiece tp) {
        ArrayList<Cell> cellsBefore = new ArrayList<>();
        ArrayList<Cell> optimalCells = new ArrayList<>();
        int widthBefore = (int) tp.getDimensions().getWidth();
        int heightBefore = (int) tp.getDimensions().getHeight();
        boolean found = false;

        for (Cell c: tp.getActualMatrix()) {
            cellsBefore.add(boardList.get(c.getIndex(numCols)));
        }
        tp.rotateRight();

        ArrayList<Cell> originalMatrix = tp.getOriginalMatrix();
        int matrixNumRows = tp.getMatrixNumRows();
        int matrixNumCols = tp.getMatrixNumCols();
        int numTries = 0;
        while (numTries < 4) {
            boolean pieceGood = true;
            for (int row = 0; row < matrixNumRows; row++) {
                for (int col = 0; col < matrixNumCols; col++) {
                    Cell currentCell = originalMatrix.get(row * matrixNumCols + col);
                    if (currentCell.isFilled()) {
                        int index = (row + pieceRow) * numCols + col + pieceCol;
                        if (index < 0 || index >= (numRows * numCols)) {
                            pieceGood = false;
                            break;
                        }
                        Cell actualCell = boardList.get(index);
                        if (actualCell.isFilled() && !tp.getActualMatrix().contains(actualCell)) {
                            pieceGood = false;
                            break;
                        }
                    }
                }
            }

            if (pieceGood) {
                found = true;
                break;
            }
            if (numTries == 2) {
                pieceCol--;
            } else if (numTries == 3) {
                pieceCol++;
            } else {
                pieceRow--;
            }

            numTries++;
        }

        if (!found) {
            //tp.rotateLeft();
            System.out.println("OptimalWasNotFound");
        }
    }

    public boolean cellCheckValidity(TetrisPiece tp) {
        for (Cell c: tp.getOriginalMatrix()){
            System.out.println(c.getIndex(numCols));
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
//            System.out.format("%3d  ", (boardList.get(i).getRowPos() * numCols + boardList.get(i).getColPos()));
            int col = 0;
            if (boardList.get(i).getIsGhost()) {
                col = 1;
            }
            System.out.format("%3d  ", col);

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
        Cell cell = null;
        try {
            cell = boardList.get(rowPos * numCols + colPos);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Out of bounds!");
        }

        return cell;
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
            int index = c.getIndex(numCols) + (numCols);
            if (index >= numRows * numCols) {
                return false;
            }
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
