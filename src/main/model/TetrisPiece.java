package main.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class TetrisPiece implements Entity{

    private int rotation;
    private double fallSpeed, extrapolate;

    private Type type;
    private ArrayList<Block> blocks;

    public enum Type {
        I, J, L, O, S, T, Z
    }

    public TetrisPiece() {
        this.fallSpeed = 1;
        this.rotation = 1;

        this.type = chooseRandomType();
    }

    public TetrisPiece(int numType) {
        this.fallSpeed = 1;
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



    @Override
    public void setExtrapolation(double extrapolate) {
        this.extrapolate = extrapolate;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g2) {

    }
}
