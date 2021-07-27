package main.ui;

import main.model.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PausedPanel extends JPanel implements ActionListener {
    final static String PAUSEDPANEL = "pausedPanel";
    private static final Color backgroundCol = new Color(43, 42, 42);

    private TetrisFrame frame;
    private JButton resumeGame, options, quitGame;
    private GamePanel parent;

    public PausedPanel(TetrisFrame frame, GamePanel parent) {
        super();
        this.frame = frame;
        this.parent = parent;

        initButtons();
        initPanel();
    }

    private void initButtons() {
        ImageIcon resumeIcon = new ImageIcon("resources/icons/resumeButton.png");
        resumeGame = new JButton(resumeIcon);
        resumeGame.setMaximumSize(new Dimension(resumeIcon.getIconWidth(), resumeIcon.getIconHeight()));
        resumeGame.addActionListener(this);

        ImageIcon optionsIcon = new ImageIcon("resources/icons/optionsButton.png");
        options = new JButton(optionsIcon);
        options.setMaximumSize(new Dimension(optionsIcon.getIconWidth(), optionsIcon.getIconHeight()));
        options.addActionListener(this);

        ImageIcon quitIcon = new ImageIcon("resources/icons/quitButton.png");
        quitGame = new JButton(quitIcon);
        quitGame.setMaximumSize(new Dimension(quitIcon.getIconWidth(), quitIcon.getIconHeight()));
        quitGame.addActionListener(this);
    }

    private void initPanel() {
        setPreferredSize(new Dimension(TetrisFrame.WIDTH, TetrisFrame.HEIGHT));
        setBackground(backgroundCol);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(Box.createRigidArea(new Dimension(250, 0)));
        add(resumeGame);
        add(Box.createRigidArea(new Dimension(100, 0)));
        add(options);
        add(Box.createRigidArea(new Dimension(100, 0)));
        add(quitGame);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        Game game = parent.getGame();
        if (src == quitGame) {
            game.endGame();
            frame.showMenu();
            frame.setTitle("Tetris");
        } else if (src == resumeGame) {
            game.startCountDown();
            frame.showGamePanel();
        } else if (src == options) {
            frame.showOptions();
        }
    }
}
