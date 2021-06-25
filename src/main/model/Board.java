package main.model;

import java.util.ArrayList;

public class Board implements Runnable {

    private int numCols, numRows;
    private ArrayList<Cell> boardList;

    public Board(int numCols, int numRows) {
        this.numCols = numCols;
        this.numRows = numRows;

        initBoard();
        printBoard();
    }

    public void render(double extrapolate) {

    }

    public void update() {

    }

    public void initBoard() {
        boardList = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                boardList.add(new Cell(i, j));
            }
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

    @Override
    public void run() {

    }
}
