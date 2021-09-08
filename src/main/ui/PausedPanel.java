package main.ui;

import main.model.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// This panel shows when the Game is in a paused state and allows the user different choices to click on whether it be to resume, look at the options menu, or quit the current game
public class PausedPanel extends JPanel implements ActionListener {
    final static String PAUSEDPANEL = "pausedPanel";
    private static final Color backgroundCol = new Color(43, 42, 42);

    private TetrisFrame frame;
    private JButton resumeGame, options, quitGame;
    private GamePanel parent;

    // the constructor of the PausedPanel that needs the TetrisFrame and reference to the GamePanel so that quickly swapping back is possible
    public PausedPanel(TetrisFrame frame, GamePanel parent) {
        super();
        this.frame = frame;
        this.parent = parent;

        initButtons();
        initPanel();
    }

    // MODIFIES: this
    // EFFECTS: initializes all the buttons for this panel
    private void initButtons() {
        ImageIcon resumeIcon = new ImageIcon( getClass().getResource("/icons/resumeButton.png"));
        resumeGame = new JButton(resumeIcon);
        resumeGame.setMaximumSize(new Dimension(resumeIcon.getIconWidth(), resumeIcon.getIconHeight()));
        resumeGame.addActionListener(this);

        ImageIcon optionsIcon = new ImageIcon( getClass().getResource("/icons/optionsButton.png"));
        options = new JButton(optionsIcon);
        options.setMaximumSize(new Dimension(optionsIcon.getIconWidth(), optionsIcon.getIconHeight()));
        options.addActionListener(this);

        ImageIcon quitIcon = new ImageIcon( getClass().getResource("/icons/quitButton.png"));
        quitGame = new JButton(quitIcon);
        quitGame.setMaximumSize(new Dimension(quitIcon.getIconWidth(), quitIcon.getIconHeight()));
        quitGame.addActionListener(this);
    }

    // REQUIRES: all buttons must be initialized
    // MODIFIES: this
    // EFFECTS: initializes the panel with the TetrisFrame's preferred size and adds all components to be shown
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

    // EFFECTS: depending on which button is clicked, this panel allows a Game to resume, a view of the OptionsMenu, or a way to quit the game whenever the user likes
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        Game game = parent.getGame();
        if (src == quitGame) {
            game.quitGame();
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
