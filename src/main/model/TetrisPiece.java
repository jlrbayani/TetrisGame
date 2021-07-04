package main.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class TetrisPiece extends Entity{

    private int rotation, rowPos, colPos;
    private Type type;
    private ArrayList<Block> blocks;

    public enum Type {
        I, J, L, O, S, T, Z
    }

    public TetrisPiece() {
        this.rotation = 1;

        this.type = chooseRandomType();
    }

    public TetrisPiece(int numType) {
        this.rotation = 1;
        this.type = setType(numType);
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
        blocks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {

        }
    }

    public void lockPiece(Cell c) {

    }

    @Override
    public void setExtrapolation(double extrapolate) {
        this.extrapolate = extrapolate;
        for (Block b: blocks) {
            b.setExtrapolation(extrapolate);
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g2) {

    }
}
