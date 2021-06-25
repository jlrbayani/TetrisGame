package main.model;

public class Cell {

    private int colPos, rowPos;
    private double actualX, actualY;

    public Cell(int colPos, int rowPos){
        this.colPos = colPos;
        this.rowPos = rowPos;
    }

    public int getColPos() {
        return colPos;
    }

    public int getRowPos() {
        return rowPos;
    }

}
