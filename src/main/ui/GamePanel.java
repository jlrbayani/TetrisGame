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
        setLayout(new BorderLayout());

        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        //leftPanel.setBackground(backgroundCol);
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        //rightPanel.setBackground(backgroundCol);

        //leftPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        pauseGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(Box.createHorizontalGlue());
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(hold);
        leftPanel.add(add(Box.createRigidArea(new Dimension(0, 400))));
        leftPanel.add(pauseGame);
        leftPanel.add(options);
        leftPanel.add(quitGame);

        next.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(next);




        leftPanel.setOpaque(false);
        rightPanel.setOpaque(false);
        add(leftPanel, BorderLayout.LINE_START);
        add(rightPanel, BorderLayout.LINE_END);


          // GridBagLayout Attempt
//        addToGridBagConst(leftPanel, hold, 0, 0, 1, 1, GridBagConstraints.FIRST_LINE_START, 50, 20, -1, -1);
//        addToGridBagConst(leftPanel, next, 2, 0, 1, 1, GridBagConstraints.FIRST_LINE_END, 50, 20, -1, -1);
//        addToGridBagConst(leftPanel, pauseGame, 0, 3, 1, 1, GridBagConstraints.LAST_LINE_START, 0, 0, 5, 1);
//        addToGridBagConst(leftPanel, options, 2, 3, 1, 1, GridBagConstraints.LAST_LINE_END, 0, 0, 5, 1 );
//        addToGridBagConst(leftPanel, quitGame, 1, 3, 1, 1, GridBagConstraints.PAGE_END, 0, 0, 1, 1 );
    }

    private void addToGridBagConst(JComponent container, JComponent comp, int gridx, int gridy, int weightx, int weighty, int anchor, int ipadx, int ipady, int gridWidth, int gridHeight) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        c.weightx = weightx;
        c.weighty = weighty;
        c.anchor = anchor;
        c.ipadx = ipadx;
        c.ipady = ipady;
        c.gridwidth = gridWidth;
        c.gridheight = gridHeight;

        container.add(comp, c);
    }

    public void startGame() {
        game = new Game(frame);
        game.startGame();
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
        if (src == quitGame) {
            game.endGame();
            frame.showMenu();
            frame.setTitle("Tetris");
        } else if (src == pauseGame) {
            game.pauseGame();
        } else if (src == options) {
            game.startCountDown();
        }
    }
}
