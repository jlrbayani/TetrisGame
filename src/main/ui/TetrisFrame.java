package main.ui;

import javax.swing.*;
import java.awt.*;

public class TetrisFrame extends JFrame {
    private JPanel cards;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private OptionsPanel optionsPanel;
    private HighScoresPanel highScoresPanel;

    private CardLayout cardLayout;
    private JPanel currentPanel, previousPanel;
    private SoundSystem ss;

    public static final int HEIGHT = 720;
    public static final int WIDTH = 920;

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
        previousPanel = currentPanel;
        currentPanel = gamePanel;
        gamePanel.startGame();
        cardLayout.show(cards, GamePanel.GAMEPANEL);
    }

    public void showMenu() {
        previousPanel = currentPanel;
        currentPanel = menuPanel;
        cardLayout.show(cards, MenuPanel.MENUPANEL);
    }

    public void showHighScores() {
        previousPanel = currentPanel;
        currentPanel = highScoresPanel;
        cardLayout.show(cards, HighScoresPanel.HIGHSCORESPANEL);
    }

    public void showOptions() {
        previousPanel = currentPanel;
        currentPanel = optionsPanel;
        cardLayout.show(cards, OptionsPanel.OPTIONSPANEL);
    }

    private void initCards() {
        cards = new JPanel(new CardLayout());
        cardLayout = (CardLayout) (cards.getLayout());
        add(cards);

        menuPanel = new MenuPanel(this);
        cards.add(menuPanel, MenuPanel.MENUPANEL);

        gamePanel = new GamePanel(this);
        cards.add(gamePanel, GamePanel.GAMEPANEL);

        optionsPanel = new OptionsPanel(this);
        cards.add(optionsPanel, OptionsPanel.OPTIONSPANEL);

        highScoresPanel = new HighScoresPanel(this);
        cards.add(highScoresPanel, HighScoresPanel.HIGHSCORESPANEL);
    }

    // MODIFIES: this
    // EFFECTS:  location of frame is set so frame is centred on desktop
    public void centreOnScreen() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);
    }

    public void returnToPreviousPanel() {
        if (previousPanel != null) {
            this.currentPanel = previousPanel;
        }

        if (menuPanel.equals(currentPanel)) {
            showMenu();
        } else if (gamePanel.equals(currentPanel)) {
            cardLayout.show(cards, GamePanel.GAMEPANEL);
        } else if (optionsPanel.equals(currentPanel)) {
            showOptions();
        } else if (highScoresPanel.equals(currentPanel)) {
            showHighScores();
        }
    }

    public JPanel getPreviousPanel() { return previousPanel; }

    public JPanel getCurrentPanel() {
        return currentPanel;
    }

    public SoundSystem getSoundSystem() {
        return this.ss;
    }
}
