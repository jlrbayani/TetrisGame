package main.ui;

import main.model.Score;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// this represents the panel to show to the user when a game finishes
// it is determined if the score they reach should be added to the HighScores list
// this panel has two cases of showing if the user is successful or if they fail to make it to the HighScores list
public class GameOverPanel extends JPanel implements ActionListener {
    final static String GAMEOVERPANEL = "gameOverPanel";
    private static final Color backgroundCol = new Color(43, 42, 42);
    private TetrisFrame frame;
    private String name;
    private Score score;
    private boolean hasNewScore;
    private JTextField textField;
    private StandardButton menuButton, confirmButton;
    private JLabel header, additionalText;

    // the constructor for the GameOverPanel which refers back to TetrisFrame
    public GameOverPanel(TetrisFrame frame) {
        this.frame = frame;
        hasNewScore = false;
        score = null;

        initButtons();
        requestFocus();
    }

    public void setHasNewScore(boolean hasNewScore) {
        this.hasNewScore = hasNewScore;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    // MODIFIES: this
    // EFFECTS: initializes the possible buttons for the gameOverPanel
    public void initButtons() {
        menuButton = new StandardButton("MAIN MENU");
        menuButton.setAlignmentX(CENTER_ALIGNMENT);
        menuButton.addActionListener(this);

        confirmButton = new StandardButton("CONFIRM");
        confirmButton.setAlignmentX(CENTER_ALIGNMENT);
        confirmButton.addActionListener(this);

        textField = new JTextField(10);
        textField.setMaximumSize(new Dimension(200, 30));
        textField.addActionListener(this);
    }

    // REQUIRES: buttons to be initialized
    // MODIFIES: this
    // EFFECTS: adds the components required for the current state of the panel which depends on hasNewScore
    public void initPanel() {
        removeAll();
        revalidate();

        setPreferredSize(new Dimension(TetrisFrame.WIDTH, TetrisFrame.HEIGHT));
        setBackground(backgroundCol);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        if (hasNewScore) {
            header = new JLabel("NEW HIGH SCORE !");
            header.setAlignmentX(CENTER_ALIGNMENT);
            header.setFont(new Font(Font.MONOSPACED, Font.BOLD, 50));
            header.setForeground(Color.WHITE);
            additionalText = new JLabel("Your Name");
            additionalText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 30));
            additionalText.setAlignmentX(CENTER_ALIGNMENT);
            additionalText.setForeground(Color.WHITE);

            add(Box.createRigidArea(new Dimension(0, 50)));
            add(header);
            add(Box.createRigidArea(new Dimension(0, 100)));
            add(additionalText);
            add(Box.createRigidArea(new Dimension(0, 50)));
            add(textField);
            add(Box.createRigidArea(new Dimension(0, 50)));
            add(confirmButton);
        } else {
            header = new JLabel("TOO BAD !");
            header.setAlignmentX(CENTER_ALIGNMENT);
            header.setFont(new Font(Font.MONOSPACED, Font.BOLD, 50));
            header.setForeground(Color.WHITE);

            add(Box.createRigidArea(new Dimension(0, 50)));
            add(header);
            add(Box.createRigidArea(new Dimension(0, 200)));
            add(menuButton);
        }
    }

    // EFFECTS: handles all the button effects in the panel, also checks if the name length isn't longer than 15 characters
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == menuButton) {
            frame.showMenu();
        } else if (src == confirmButton) {
            if (textField.getText().length() > 15) {
                additionalText.setText("Length Invalid! Please Choose a shorter name!");
                textField.setText("");
            } else {
                score.setName(textField.getText());
                textField.setText("");
                frame.showMenu();
                hasNewScore = false;
            }
        }
    }
}
