package main.ui;

import java.awt.*;

// Tetris contains the main method that starts the game
public class Tetris {
    public static void main(String[] args) {
        System.out.println("Welcome to Tetris!");

        EventQueue.invokeLater(() -> {
            TetrisFrame frame = new TetrisFrame();
        });

    }
}
