package main.ui;

import javax.swing.*;
import java.awt.*;

public class Tetris {
    public static void main(String[] args) {
        System.out.println("Welcome to Tetris!");

        EventQueue.invokeLater(() -> {
            JFrame frame = new TetrisFrame();
        });
    }
}
