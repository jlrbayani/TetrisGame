package main.ui;

import javax.swing.*;
import java.awt.*;

public class StandardButton extends JButton {

    public StandardButton(String text) {
        super(text);
        setFont(new Font("MONOSPACED", Font.BOLD, 20));
        setBackground(new Color(255, 255 , 255));
        setOpaque(true);
        //start.setBorderPainted(false);
        setFocusPainted(false);
    }
}
