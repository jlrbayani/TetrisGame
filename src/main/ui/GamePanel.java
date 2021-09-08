package main.ui;

import main.model.Entity;
import main.model.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// The GamePanel contains all of the components fpr the user interface during gameplay of Tetris.
// The rendering method is also tied to the paintComponent of this JPanel due to how Swing handles what is shown and when something should be shown to the user.
public class GamePanel extends JPanel implements ActionListener {
    final static String GAMEPANEL = "gamePanel";
    private static final Color backgroundCol = new Color(43, 42, 42);

    private Game game;
    private TetrisFrame frame;
    private JLabel hold, next;
    private JButton pauseGame;
    private JPanel leftPanel, centrePanel, rightPanel;

    // constructor for the GamePanel which also initializes the keyboard tied to the controls for the game
    public GamePanel(TetrisFrame frame) {
        super();
        this.frame = frame;

        initLabels();
        initButtons();
        initPanel();

        this.addKeyListener(new Keyboard());
    }

    // MODIFIES: this
    // EFFECTS: initializes all the main labels of the game panel
    private void initLabels() {
        ImageIcon holdIcon = new ImageIcon(getClass().getResource("/icons/holdIcon.png"));
        hold = new JLabel(holdIcon);

        ImageIcon nextIcon = new ImageIcon(getClass().getResource("/icons/nextIcon.png"));
        next = new JLabel(nextIcon);

    }

    // MODIFIES: this
    // EFFECTS: initializes all the main buttons of the game panel
    private void initButtons() {
        ImageIcon pauseIcon = new ImageIcon( getClass().getResource("/icons/pauseButton.png"));
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

    // REQUIRES: all labels and buttons to be initialized
    // MODIFIES: this
    // EFFECTS: initializes all components and adds them to this panel
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
        requestFocusInWindow();
    }

    // EFFECTS: sets up the layout and look of the left hand side of the screen
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

    // EFFECTS: sets up the layout and feel of the centre panel
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

    // EFFECTS: sets up the layout and feel of the right hand side of the screen
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

    // EFFECTS: creates the Game objects and starts the game
    public void startGame() {
        game = new Game(frame);
        game.startGame();
    }

    public Game getGame() {
        return game;
    }

    // REQUIRES: game and all the other panels inside this one must be initialized
    // MODIFIES: this/the screen the user sees
    // EFFECTS: repaints the different panels in GamePanel and draws all the entities in game that needs to be rendered
    @Override
    protected synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        leftPanel.repaint();
        centrePanel.repaint();
        rightPanel.repaint();

        for (Entity e: game.getGameEntities()) {
            e.draw(g2);
        }

    }

    // EFFECTS: pauses the game if the src found is from the pauseGame button
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == pauseGame && !game.isPaused()) {
            game.pauseGame();
            frame.showPausedPanel();
        }
    }

    // a Keyboard class to handle controls for the Game
    private class Keyboard extends KeyAdapter {
        // MODIFIES: game
        // EFFECTS: checks for any keys that are held down and keeps track of keyPresses that lasts long
        @Override
        public void keyPressed(KeyEvent e) {
            boolean[] keysHeld = game.getKeysHeldDown();
            //System.out.println("KeyPressed: " + e.getKeyCode());
            int[] keysCall = game.getKeysNumCall();
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (!keysHeld[0]) {
                        keysCall[0]++;
                    }
                    keysHeld[0] = true;
                    return;
                case KeyEvent.VK_RIGHT:
                    if (!keysHeld[1]) {
                        keysCall[1]++;
                    }
                    keysHeld[1] = true;
                    return;
                case KeyEvent.VK_DOWN:
                    if (!keysHeld[2]) {
                        keysCall[2]++;
                    }
                    keysHeld[2] = true;
                    return;
            }

            game.keyPressed(e.getKeyCode());
        }

        // MODIFIES: game
        // EFFECTS: keeps track of any keys released and does the appropriate action for it
        @Override
        public void keyReleased(KeyEvent e) {
            boolean[] keysHeld = game.getKeysHeldDown();
            boolean[] keysSingle = game.getKeysSinglePress();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    keysHeld[0] = false;
                    return;
                case KeyEvent.VK_RIGHT:
                    keysHeld[1] = false;
                    return;
                case KeyEvent.VK_DOWN:
                    keysHeld[2] = false;
                    return;
            }

            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE:
                    keysSingle[0] = true;
                    break;
                case KeyEvent.VK_UP:
                    keysSingle[1] = true;
                    break;
                case KeyEvent.VK_Z:
                    keysSingle[2] = true;
            }


        }
    }

}
