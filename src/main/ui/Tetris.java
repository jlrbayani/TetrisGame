package main.ui;

import java.awt.*;

public class Tetris {
    public static void main(String[] args) {
        System.out.println("Welcome to Tetris!");

        EventQueue.invokeLater(() -> {
            TetrisFrame frame = new TetrisFrame();
        });

    }
}
