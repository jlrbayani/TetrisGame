package main.ui;

import main.model.Score;

import javax.swing.*;
import java.util.ArrayList;

public class HighScoresPanel extends JPanel {
    final static String HIGHSCORESPANEL = "highScoresPanel";
    private TetrisFrame frame;
    private ArrayList<Score> highScores;

    public HighScoresPanel(TetrisFrame frame) {
        super();
        this.frame = frame;
        highScores = frame.getHighScores();
    }


}
