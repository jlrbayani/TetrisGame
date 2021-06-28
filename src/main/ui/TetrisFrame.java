package main.ui;

import javax.swing.*;
import java.awt.*;

public class TetrisFrame extends JFrame {
    private JPanel cards;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private CardLayout cardLayout;
    private JPanel currentPanel;

    public static final int HEIGHT = 720;
    public static final int WIDTH = 920;
    //public static final int WIDTH = 1280;

    public TetrisFrame() {
        initFrame();
    }

    public void initFrame() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Tetris");

        initCards();
        showMenu();

        currentPanel = menuPanel;

        pack();
        setVisible(true);
        centreOnScreen();
    }

    public void startGame() {
        currentPanel = gamePanel;
        gamePanel.startGame();
        cardLayout.show(cards, GamePanel.GAMEPANEL);
    }

    public void showMenu() {
        currentPanel = menuPanel;
        cardLayout.show(cards, MenuPanel.MENUPANEL);
    }

    public void showHighScores() {
        // currentPanel = highScoresPanel;
    }

    public void showOptions() {
        // currentPanel = optionsPanel;
    }

    private void initCards() {
        cards = new JPanel(new CardLayout());
        cardLayout = (CardLayout) (cards.getLayout());
        add(cards);

        menuPanel = new MenuPanel(this);
        cards.add(menuPanel, MenuPanel.MENUPANEL);

        gamePanel = new GamePanel(this);
        cards.add(gamePanel, GamePanel.GAMEPANEL);
    }

    // MODIFIES: this
    // EFFECTS:  location of frame is set so frame is centred on desktop
    public void centreOnScreen() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);
    }

    public JPanel getCurrentPanel() {
        return currentPanel;
    }
}
