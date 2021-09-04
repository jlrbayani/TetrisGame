package main.ui;

import main.model.Score;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

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
