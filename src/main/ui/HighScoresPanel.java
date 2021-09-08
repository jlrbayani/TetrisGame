package main.ui;

import main.model.Score;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

// this shows the user a list of the HighScores they've accumulated and is saved to a json file
public class HighScoresPanel extends JPanel implements ActionListener {
    final static String HIGHSCORESPANEL = "highScoresPanel";
    private TetrisFrame frame;
    private ArrayList<Score> highScores;
    private StandardButton backButton;
    private static final Color backgroundCol = new Color(43, 42, 42);
    private static final int OFFSET_Y = 50;
    private static final int OFFSET_X = 310;
    private int startingX, startingY;

    // the constructor for the highScores panel which refers back to TetrisFrame
    public HighScoresPanel(TetrisFrame frame) {
        super();
        this.frame = frame;
        highScores = frame.getHighScores();
        startingX = 250;
        startingY = 140;

        initButtons();
        initPanel();
        requestFocus();
    }

    // MODIFIES: this
    // EFFECTS: initializes all the buttons for this panel
    private void initButtons() {
        backButton = new StandardButton("BACK");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(this);
    }

    // REQUIRES: the buttons added to the panel must be initialized
    // MODIFIES: this
    // EFFECTS: initializes all required components and adds them to this panel
    private void initPanel() {
        setBackground(backgroundCol);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel highScores = new JLabel("HIGH SCORES");
        highScores.setForeground(Color.WHITE);
        highScores.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
        highScores.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createRigidArea(new Dimension(0, 20)));
        add(highScores);
        add(Box.createRigidArea(new Dimension(0, 580)));
        add(backButton);
    }

    private void returnToPreviousMainPanel() {
        frame.returnToPreviousPanel();
    }

    // EFFECTS: handles user input if the backButton is pressed
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == backButton) {
            returnToPreviousMainPanel();
        }
    }

    // EFFECTS: handles and unique painting required for the panel, in this case this is used to display the high scores with their respective name and score
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        for (int i = 0; i < highScores.size(); i++) {
            Score currentScore = highScores.get(i);
            g2.setColor(Color.BLACK);
            Rectangle rect = new Rectangle(startingX - 130, startingY + (i * OFFSET_Y) - 30, 670, 40);
            g2.fill(rect);
            g2.setColor(Color.PINK);
            g2.draw(rect);
            g2.setColor(Color.WHITE);
            g2.drawString(currentScore.getName(), startingX, startingY + (i * OFFSET_Y));
            g2.drawString(currentScore.getScoreNow() + "", startingX + OFFSET_X, startingY + (i * OFFSET_Y));
        }
    }
}
