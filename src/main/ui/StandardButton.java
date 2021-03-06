package main.ui;

import javax.swing.*;
import java.awt.*;

// a class for creating a standard button with set specific set of parameters that can be consistently used throughout the program
public class StandardButton extends JButton {
    final static int STANDARD_WIDTH = 170;
    final static int STANDARD_HEIGHT = 40;

    public StandardButton(String text) {
        super(text);
        setFont(new Font("MONOSPACED", Font.BOLD, 20));
        setBackground(new Color(255, 255 , 255));
        setOpaque(true);
        //start.setBorderPainted(false);
        setFocusPainted(false);
        setMaximumSize(new Dimension(STANDARD_WIDTH, STANDARD_HEIGHT));
    }
}
