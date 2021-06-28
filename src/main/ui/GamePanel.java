package main.ui;

import main.model.Entity;
import main.model.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel implements ActionListener {
    final static String GAMEPANEL = "gamePanel";
    private static final Color backgroundCol = new Color(43, 42, 42);

    private Game game;
    private TetrisFrame frame;
    private JLabel hold, next;
    private JButton pauseGame;
    private JButton quitGame;
    private JButton options;

    public GamePanel(TetrisFrame frame) {
        super();
        this.frame = frame;

        initLabels();
        initButtons();
        initPanel();

    }

    // MODIFIES: this
    // EFFECTS: initializes all the main labels of the game panel
    private void initLabels() {
        ImageIcon holdIcon = new ImageIcon("resources/icons/holdIcon.png");
        hold = new JLabel(holdIcon);

        ImageIcon nextIcon = new ImageIcon("resources/icons/nextIcon.png");
        next = new JLabel(nextIcon);

    }

    // MODIFIES: this
    // EFFECTS: initializes all the main buttons of the game panel
    private void initButtons() {
        ImageIcon pauseIcon = new ImageIcon("resources/icons/pauseButton.png");
        pauseGame = new JButton(pauseIcon);
        pauseGame.setMaximumSize(new Dimension(pauseIcon.getIconWidth(), pauseIcon.getIconHeight()));
        pauseGame.addActionListener(this);

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
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(hold);
        add(next);
        add(pauseGame);
        add(options);
        add(quitGame);
    }

    public void startGame() {
        game = new Game(frame);
        game.startGame();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (Entity e: game.getEntityList()) {
            e.draw(g2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == quitGame) {
            game.endGame();
            frame.showMenu();
            frame.setTitle("Tetris");
        }
    }
}
