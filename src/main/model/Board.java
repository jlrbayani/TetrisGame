package main.model;

import java.awt.*;
import java.util.ArrayList;

public class Board extends Entity {

    private int numCols, numRows;
    private ArrayList<Cell> boardList;
    private int actualX, actualY;
    private double extrapolate;

    public Board(int numCols, int numRows, int actualX, int actualY) {
        this.numCols = numCols;
        this.numRows = numRows;

        this.actualX = actualX;
        this.actualY = actualY;

        initBoard();
        // for testing purposes
        // printBoard();
    }

    @Override
    public void setExtrapolation(double extrapolate) {
        for (Cell c: boardList) {
            c.setExtrapolation(extrapolate);
        }
    }

    @Override
    public void update() {
        for (Cell c: boardList) {
            c.update();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        for (Cell c: boardList) {
            c.draw(g2);
        }
    }

    public void initBoard() {
        boardList = new ArrayList<>();
        int currentX = actualX;
        int currentY = actualY;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                boardList.add(new Cell(i, j, currentX, currentY));
                currentX += Cell.SIZE;
            }
            currentY += Cell.SIZE;
            currentX = actualX;
        }
    }

    public void printBoard() {
        System.out.println("Board: ");
        for (int i = 0; i < boardList.size(); i++) {
            if (i % (numCols) == 0 && i > 0) {
                System.out.println();
            }
            System.out.format("%3d  ", (boardList.get(i).getRowPos() + (boardList.get(i).getColPos() * (numCols))));
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

}
