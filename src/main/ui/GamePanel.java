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
    private JPanel leftPanel, centrePanel, rightPanel;

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
        pauseGame = new JButton(pauseIcon) {
            @Override
            public Dimension getPreferredSize() {
                int width = super.getPreferredSize().width;
                return new Dimension(width, width);
            }
        };
        pauseGame.setMaximumSize(new Dimension(pauseIcon.getIconWidth(), pauseIcon.getIconHeight()));
        pauseGame.addActionListener(this);
    }

    private void initPanel() {
        setPreferredSize(new Dimension(TetrisFrame.WIDTH, TetrisFrame.HEIGHT));
        setBackground(backgroundCol);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        initLeftPanel();
        initCentrePanel();
        initRightPanel();

        add(leftPanel);
        add(centrePanel);
        add(rightPanel);
    }

    private void initLeftPanel() {
        leftPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

            }
        };
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setMaximumSize(new Dimension(220, TetrisFrame.HEIGHT));

        pauseGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel holdContainer = new JPanel(new FlowLayout());
        holdContainer.setMaximumSize(new Dimension(150, 50));
        holdContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        holdContainer.setOpaque(false);
        holdContainer.add(hold);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(holdContainer);
        leftPanel.add(add(Box.createRigidArea(new Dimension(0, 550))));
        leftPanel.add(pauseGame);
        leftPanel.setOpaque(false);

    }

    private void initCentrePanel() {
        centrePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

            }
        };
        centrePanel.setMaximumSize(new Dimension(480, TetrisFrame.HEIGHT));
        centrePanel.setOpaque(false);

    }

    private void initRightPanel() {
        rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
            }
        };
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setMaximumSize(new Dimension(220, TetrisFrame.HEIGHT));

        next.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel nextContainer = new JPanel();
        nextContainer.setMaximumSize(new Dimension(150, 50));
        nextContainer.setOpaque(false);
        nextContainer.add(next, BorderLayout.CENTER);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(nextContainer);
        rightPanel.add(Box.createRigidArea(new Dimension(40, 100)));
        rightPanel.setOpaque(false);

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
        centrePanel.repaint();
        rightPanel.repaint();

        for (Entity e: game.getEntityList()) {
            e.draw(g2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == pauseGame && !game.isPaused()) {
            game.pauseGame();
            frame.showPausedPanel();
        }
    }


    private abstract static class PanelWithActList extends JPanel implements ActionListener {
        public PanelWithActList() {
            super();
        }

        @Override
        public abstract void actionPerformed(ActionEvent e);
    }
}
