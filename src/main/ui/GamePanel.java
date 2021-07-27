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
    private JPanel leftPanel, rightPanel;

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
    }

    private void initPanel() {
        setPreferredSize(new Dimension(TetrisFrame.WIDTH, TetrisFrame.HEIGHT));
        setBackground(backgroundCol);
        setLayout(new BorderLayout());

        leftPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

            }
        };
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        pauseGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(Box.createHorizontalGlue());
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(hold);
        leftPanel.add(add(Box.createRigidArea(new Dimension(0, 570))));
        leftPanel.add(pauseGame);

        next.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(next);
        rightPanel.add(Box.createRigidArea(new Dimension(40, 100)));


        leftPanel.setOpaque(false);
        rightPanel.setOpaque(false);
        add(leftPanel, BorderLayout.LINE_START);
        add(rightPanel, BorderLayout.LINE_END);

    }

    public void startGame() {
        game = new Game(frame);
        game.startGame();
    }

    public Game getGame() {
        return game;
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        leftPanel.repaint();
        rightPanel.repaint();

        for (Entity e: game.getEntityList()) {
            e.draw(g2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == pauseGame) {
            game.pauseGame();
            frame.showPausedPanel();
        }
    }
}
