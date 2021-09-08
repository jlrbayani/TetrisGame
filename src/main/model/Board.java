package main.model;

import java.awt.*;
import java.util.ArrayList;

// Board represents where TetrisPieces and Blocks could be added to using the Cells that build this up
public class Board extends Entity {

    private int numCols;
    private int numRows;
    private ArrayList<Cell> boardList;
    private ArrayList<Cell> ghostList;
    private int actualX, actualY;
    private int pieceRow, pieceCol;
    private boolean canMove;

    // referenced https://tetris.fandom.com/ for wall kicks data
    private static final int[][] wallKickRightDefault = {
                                                    {-1, 0}, {-1, -1}, {0, 2}, {-1, 2},   // 1 --> 2
                                                    {1, 0}, {1, 1}, {0, -2}, {1, -2},     // 2 --> 3
                                                    {1, 0}, {1, -1}, {0, 2}, {1, 2},      // 3 --> 4
                                                    {-1, 0}, {-1, 1}, {0, -2}, {-1, 2}    // 4 --> 1
                                                    };
    // referenced https://tetris.fandom.com/ for wall kicks data
    private static final int[][] wallKickRightI = {
                                                    {-2, 0}, {1, 0}, {-2, 1}, {1, -2},   // 1 --> 2
                                                    {-1, 0}, {2, 0}, {-1, -2}, {2, 1},   // 2 --> 3
                                                    {2, 0}, {-1, 0}, {2, -1}, {-1, 2},   // 3 --> 4
                                                    {1, 0}, {-2, 0}, {1, 2}, {-2, -1}    // 4 --> 1
                                                    };


    // constructor for a Board object that indicates how many columns and rows it should have
    // the pieceRow defining the starting row for TetrisPieces should be placed
    // the pieceCol defining the starting col for TetrisPieces should be placed
    // actualX for the starting x location on the screen to start drawing the board
    // actualY for the starting y location on the screen to start drawing the board
    public Board(int numCols, int numRows, int actualX, int actualY, int pieceRow, int pieceCol) {
        this.numCols = numCols;
        this.numRows = numRows;

        this.actualX = actualX;
        this.actualY = actualY;

        this.pieceRow = pieceRow;
        this.pieceCol = pieceCol;
        canMove = false;

        initBoard();
    }

    // EFFECTS: sets the extrapolation for all the cells in the boardList
    @Override
    public void setExtrapolation(double extrapolate) {
        if (!isPaused) {
            for (Cell c : boardList) {
                c.setExtrapolation(extrapolate);
            }
        }
    }

    // EFFECTS: updates all entities inside boardList
    @Override
    public void update() {
        if (!isPaused) {
            for (Entity e : boardList) {
               e.update();
            }
        }
    }

    // EFFECTS: draws the corresponding cells in boardList and ghostList
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

    // MODIFIES: this
    // EFFECTS: initializes the Board and sets up all rows and columns of Cells in boardList
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

    // MODIFIES: this
    // EFFECTS: removes the first block currently in the boardList
    public void removeFirstBlock() {
        for (Cell c: boardList) {
            if (c.isFilled() || c.isLit()) {
                c.removeBlock();
                c.setIsGhost(false);
                c.setIsLit(false);
                return;
            }
        }
    }

    // EFFECTS: returns true if the board contains a block
    // returns false if the board doesn't contain any blocks
    public boolean boardContainsBlock() {
        for (Cell c: boardList) {
            if (c.isFilled()) {
                return true;
            }
        }

        return false;
    }

    // MODIFIeS: tp, boardList
    // EFFECTS: applies a lightFade on columns that overlap with TetrisPiece tp that propagates and changes fade intensity depending on the height from the top
    public void applyLightFade(TetrisPiece tp) {
        if (tp == null || tp.getActualMatrix().size() == 0) {
            return;
        }
        Cell firstCell = tp.getActualMatrix().get(0);
        ArrayList<Integer> columnsLit = new ArrayList<>();
        for (Cell c: tp.getActualMatrix()) {
            if (!columnsLit.contains(c.getColPos())) {
                columnsLit.add(c.getColPos());
            }
        }
        int startingHeight = firstCell.getRowPos();
        int numRowsLit = startingHeight - 1;

        float intensity = 1 / (float) numRowsLit;

        for (Integer col: columnsLit) {
            for (int i = numRowsLit; i > 0; i--) {
                Cell currentCell = boardList.get(i * numCols + col);
                if (!currentCell.isFilled()) {
                    currentCell.setIsLit(true);
                    currentCell.setLightFade(i * intensity);
                }
            }
        }

    }

    // MODIFIES: this
    // EFFECTS: returns the number of line clears found in Board
    // - a line clear is when a row is completely filled with blocks
    public int getLineClear() {
        int rowsCleared = 0;
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
                rowsCleared++;
                shiftBlocks(row);
            }
        }

        return rowsCleared;
    }

    // MODIFIES: this
    // EFFECTS: moves all the blocks above endRow to the row below it
    public void shiftBlocks(int endRow) {
        int currentRow = endRow;
        while (currentRow > 0) {
            currentRow--;
            for (int col = 0; col < numCols; col++) {
                Cell currentCell = boardList.get(currentRow * numCols + col);
                Cell cellBelow = boardList.get((currentRow + 1) * numCols + col);
                if (currentCell.isFilled()) {
                    Block b = currentCell.getBlock();
                    currentCell.removeBlock();
                    cellBelow.addBlock(b);
                }
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: given cells, boardList is removed of blocks corresponding to the same indices
    public void clearCells(ArrayList<Cell> cells) {
        for (Cell c: cells) {
            boardList.get(c.getIndex(numCols)).removeBlock();
        }
    }

    // MODIFIES: this
    // EFFECTS: adds TetrisPiece tp to the boardList
    // this is meant to be called constantly such that tp is updated
    public void addTetrisPiece(TetrisPiece tp) {
        ArrayList<Cell> matrix = tp.getOriginalMatrix();
        tp.clearActualMatrix();
        int matrixNumRows = tp.getMatrixNumRows();
        int matrixNumCols = tp.getMatrixNumCols();
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

                    if (actualCell != null) {
                        actualCell.addBlock(currentCell.getBlock());
                        tp.addToActualMatrix(actualCell);
                    }
                }
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes the TetrisPiece tp to the board
    // this is meant to be only called once to set the tp to the board at the start of its existence in this Board
    // returns true if tp is successfully added to the board
    // returns false otherwise
    public synchronized boolean initPieceToBoard(TetrisPiece tp) {
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
                        moveTo.add(actualCell);

                    }
                }
            }
        }

        ArrayList<Cell> checkAbove = new ArrayList<>();
        for (Cell c: moveTo) {
            checkAbove.add(boardList.get(c.getIndex(numCols)));
        }
        while (!checkPieceMove(tp, moveTo)) {
            checkAbove = getMoveTo(checkAbove, 0, -1);
            moveTo = getMoveTo(moveTo, 1, 0);
            if (moveTo.size() == 0 && checkAbove.size() != 0) {
                moveTo = checkAbove;
            } else if (moveTo.size() == 0 && checkAbove.size() == 0){
                return false;
            }
        }

        if (checkPieceMove(tp, moveTo)) {
            addPieceToBoard(tp, moveTo);
            return true;
        }

        return false;
    }

    // MODIFIES: this, tp
    // EFFECTS: adds the cells in tp to the ghostList and is reflected on the Board state
    // returns the row distance between the initial position of tp and ghostList
    public synchronized int fastDropPiece(TetrisPiece tp) {
        ArrayList<Cell> actualMatrix = tp.getActualMatrix();
        if (actualMatrix.size() == 0) {
            return 0;
        }
        int bottomRowTp = actualMatrix.get(0).getRowPos();
        int topRowGhost = ghostList.get(3).getRowPos();
        addPieceToBoard(tp, ghostList);

        return topRowGhost - bottomRowTp + 1;
    }

    // EFFECTS: updates the ghostList depending on tp
    // this searches for the bottomRow from tp until the first block is reached that might collide with tp
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
        while (checkPieceMove(tp, ghostCellsCurrent)) {
            ghostCellsBefore = ghostCellsCurrent;
            ghostCellsCurrent = getMoveTo(ghostCellsCurrent, 0, 1);
        }

        for (Cell c: ghostCellsBefore) {
            c.setIsGhost(true);
        }
        ghostList = ghostCellsBefore;
    }

    // MODIFIES: tp, this
    // EFFECTS: handles rotating tp
    // returns true if the rotation succeeds
    // returns false if the rotation fails
    public synchronized boolean rotatePieceInPlay(TetrisPiece tp, boolean rotateRight) {
        if (tp.getType() == TetrisPiece.Type.O) {
            return true;
        }
        TetrisPiece tester = tp.copyPiece();
        if (rotateRight) {
            tester.rotateRight();
        } else {
            tester.rotateLeft();
        }
        tester.setActualMatrix(this, tp.getOriginalMatrix());
        ArrayList<Cell> moveTo = tester.getActualMatrix();
        // if rotation occurs at the edge of the Board for Type I
        if (tp.getType() == TetrisPiece.Type.I && checkRowOverflow(tp, moveTo)) {
            moveTo = applyEdgeKickI(tp, moveTo);
        }

        // checks if rotation is possible in the if case
        // in else, all the wall kicks are applied to see if the rotation is possible if shifted slightly to a different row and column
        // referenced https://tetris.fandom.com/ for wall kicks data
        if (checkPieceMove(tp, moveTo) && !checkRowOverflow(tp, moveTo)) {
            tp.setRotation(tester.getRotation());
            addPieceToBoard(tp, moveTo);
            return true;
        } else {
            for (int numTries = 0; numTries < 4; numTries++) {
                moveTo = applyWallKick(tp, tester, numTries, rotateRight);
                if (checkPieceMove(tp, moveTo) && !checkRowOverflow(tp, moveTo)) {
                    tp.setRotation(tester.getRotation());
                    addPieceToBoard(tp, moveTo);
                    return true;
                }
            }
        }

        return false;
    }

    // MODIFIES: tp, this
    // EFFECTS: returns an ArrayList<Cell> that contains the cells of tp where edgeKicks are applied
    // this is a unique case for the TetrisPiece I
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
            if (diff > 4) {
                moveTo = getMoveToOverflow(moveTo, diff, 0);
            } else if (diff < -4){
                moveTo = getMoveToOverflow(moveTo, -(numCols - diff), 0);
            }
            index++;
        }

        return moveTo;
    }

    // MODIFIES: tp, this
    // EFFECTS: checks if the tp and the moveTo it intends to moveTo does not overflow to the next Row
    // - this occurs due to the nature of boardList being an ArrayList
    // returns true if there is an overflow to the next row
    // returns false otherwise
    public boolean checkRowOverflow(TetrisPiece tp, ArrayList<Cell> moveTo) {
        if (moveTo.size() != 4) {
            return true;
        }
        int index = 0;
        ArrayList<Cell> actualMatrix = tp.getActualMatrix();
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
            if (diff > 4 || diff < -4) {
                return true;
            }

            index++;
        }

        return false;
    }

    // EFFECTS: applies a wall kick to the copy piece and returns the moveTo that contains the cells its going to be possibly 'kicked' to
    // this wall kick information is based off rotation of the original piece tp, numTries, and the type of rotation (left or right)
    public ArrayList<Cell> applyWallKick(TetrisPiece tp, TetrisPiece copy, int numTries, boolean rotateRight) {
        int currentRotation = tp.getRotation();
        int kickIndex = ((currentRotation - 1) * 4) + numTries;
        ArrayList<Cell> moveTo;
        int rowChange, colChange;

        // wall kick specific for Piece I
        if (tp.getType() == TetrisPiece.Type.I) {
            if (rotateRight) {
                colChange = wallKickRightI[kickIndex][0];
                rowChange = wallKickRightI[kickIndex][1];
            } else {
                colChange = -wallKickRightI[kickIndex][0];
                rowChange = -wallKickRightI[kickIndex][1];
            }
        } else { // default wall kick for other pieces
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

    // MODIFIES: this
    // EFFECTS: this moves the piece tp to the left or to the right depending on move (-1 and 1 respectively)
    // it's not a guarantee move because there is a call to checkPieceMove which ensures that there's space for the tp to move in
    // if it succeeds, then the piece is added to the board and alters this
    public synchronized boolean movePieceSide(TetrisPiece tp, int move) {
        ArrayList<Cell> moveTo = getMoveTo(tp, move, 0);
        if (checkPieceMove(tp, moveTo)) {
            addPieceToBoard(tp, moveTo);
            return true;
        }

        return false;
    }

    // MODIFIES: this
    // EFFECTS: this moves the piece tp down 1 cell
    // it's not a guarantee move because there is a call to checkPieceMove which ensures that there's space for the tp to move in
    // if it succeeds, then the piece is added to the board and alters this
    public synchronized boolean movePieceDown(TetrisPiece tp) {
        if (tp == null) {
            return false;
        }
        ArrayList<Cell> moveTo = getMoveTo(tp, 0, 1);
        if (checkPieceMove(tp, moveTo)) {
            addPieceToBoard(tp, moveTo);
            return true;
        }

        return false;
    }


    // REQUIRES: moveTo must be verified using checkPieceMove before calling this method
    // MODIFIES: this
    // EFFECTS: this adds tp to boardList and gets rid of its older state and applies moveTo onto it regardless if moveTo is a valid cell or not
    // this is why checkPieceMove must be called to ensure that moveTo is fully valid for tp to move into
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

    // EFFECTS: a helper method for the other getMoveTo method, ensures tp is not null
    public synchronized ArrayList<Cell> getMoveTo(TetrisPiece tp, int colChange, int rowChange) {
        if (tp == null) {
            return null;
        }
        return getMoveTo(tp.getActualMatrix(), colChange, rowChange);
    }

    // EFFECTS: returns an ArrayList<Cell> containing the cellsll that actualMatrix could possibly move to based off colChange and rowChange
    public synchronized ArrayList<Cell> getMoveTo(ArrayList<Cell> actualMatrix, int colChange, int rowChange) {
        ArrayList<Cell> moveTo = new ArrayList<>();

        for (Cell c: actualMatrix) {
            if (c == null) {
                return new ArrayList<>();
            }

            int moveSide = c.getColPos() + colChange;

            if (moveSide >= numCols || moveSide < 0) {
                return new ArrayList<>();
            }

            int index = (c.getRowPos() + rowChange) * numCols + (moveSide);

            if (index >= numCols * numRows || index < 0) {
                return new ArrayList<>();
            }

            moveTo.add(boardList.get(index));
        }

        return moveTo;
    }

    // EFFECTS: specifically made for pieceI wallKicks and rotation against the edge of the walls, this method pushes it so that no row overflow happens due to the nature of boardList
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

    // EFFECTS: returns true if moveTo is valid for tp to move to
    // returns false otherwise
    public boolean checkPieceMove(TetrisPiece tp, ArrayList<Cell> moveTo) {
        if (moveTo == null || moveTo.size() == 0) {
            return false;
        }

        for (Cell moveToCurrentCell : moveTo) {
            if (moveToCurrentCell == null || (moveToCurrentCell.isFilled() && !tp.getActualMatrix().contains(moveToCurrentCell))) {
                return false;
            }
        }

        return true;
    }

    public void setPieceRow(int pieceRow) {
        this.pieceRow = pieceRow;
    }

    public void setPieceCol(int pieceCol) {
        this.pieceCol = pieceCol;
    }

    // MODIFIES: this
    // EFFECTS: shifts this board's pieceRow based off the offset and ensures that the pieceRow never exceeds numRows
    public void shiftPieceRow(int offset) {
        if (pieceRow == numRows) {
            return;
        }
        this.pieceRow += offset;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    // MODIFIES: this
    // EFFECTS: removes all the blocks in the cells of boardList
    public void clearBoard() {
        for (Cell c: boardList) {
            c.removeBlock();
        }
    }

    public void setCanMoveVertically(boolean canMove) {
        this.canMove = canMove;
        setCellsCanMoveVertically();
    }

    public void setCanMoveRight(boolean canMove) {
        this.canMove = canMove;
        setCellsCanMoveRight();
    }

    public void setCanMoveLeft(boolean canMove) {
        this.canMove = canMove;
        setCellsCanMoveLeft();
    }

    public void setCellsCanMoveVertically() {
        for (Cell c: boardList) {
            c.setCanMoveVertically(canMove);
        }
    }

    public void setCellsCanMoveRight() {
        for (Cell c: boardList) {
            c.setCanMoveRight(canMove);
        }
    }

    public void setCellsCanMoveLeft() {
        for (Cell c: boardList) {
            c.setCanMoveLeft(canMove);
        }
    }

    public Cell getCell(int rowPos, int colPos) {
        Cell cell = null;
        try {
            cell = boardList.get(rowPos * numCols + colPos);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println("Out of bounds!");
        }

        return cell;
    }

    @Override
    public String toString() {
        return "Board{" +
                "numCols=" + numCols +
                ", numRows=" + numRows +
                '}';
    }

}
