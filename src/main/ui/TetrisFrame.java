package main.ui;

import main.model.Score;
import main.persistence.JsonReader;
import main.persistence.JsonWriter;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class TetrisFrame extends JFrame {
    private JPanel cards;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private OptionsPanel optionsPanel;
    private HighScoresPanel highScoresPanel;
    private PausedPanel pausedPanel;
    private GameOverPanel gameOverPanel;

    private CardLayout cardLayout;
    private JPanel currentPanel, previousPanel;
    private SoundSystem ss;
    private ArrayList<Score> highScores;
    private JsonReader jsonReader;
    private JsonWriter jsonWriter;

    public static final int HEIGHT = 720;
    public static final int WIDTH = 920;

    public static final String HIGH_SCORES = "highScores.json";


    public TetrisFrame() {
        initFrame();
    }

    public void initFrame() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Tetris");

        ss = new SoundSystem();

        initHighScores();
        initCards();
        showMenu();

        currentPanel = menuPanel;

        pack();
        setVisible(true);
        centreOnScreen();

        // ensures that a listener keeps track of when the frame is closed to make sure the thread pool in sound system will eventually be safely closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                saveHighScores();
                closeSoundSystem();
                System.exit(0);
            }
        });
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

    public void showPausedPanel() {
        previousPanel = currentPanel;
        currentPanel = pausedPanel;
        cardLayout.show(cards, PausedPanel.PAUSEDPANEL);
    }

    public void showGamePanel() {
        previousPanel = currentPanel;
        currentPanel = gamePanel;
        cardLayout.show(cards, GamePanel.GAMEPANEL);
    }

    public void showGameOverPanel(Score score) {
        previousPanel = currentPanel;
        currentPanel = gameOverPanel;
        boolean added = addToHighScores(score);
        if (added) {
            setGameOverPanelState(true);
            System.out.println("setNewScore");
            gameOverPanel.setScore(score);
        }
        gameOverPanel.initPanel();
        cardLayout.show(cards, GameOverPanel.GAMEOVERPANEL);
    }

    private void setGameOverPanelState(boolean addScore) {
        gameOverPanel.setHasNewScore(addScore);
    }

    public ArrayList<Score> getHighScores() {
        return highScores;
    }

    private boolean addToHighScores(Score score) {
        boolean added = false;
        if (highScores.size() < 10) {
            highScores.add(score);
            added = true;
        } else {
            for (Score s: highScores) {
                if (s.getScoreNow() < score.getScoreNow()) {
                    highScores.remove(highScores.size() - 1);
                    highScores.add(score);
                    added = true;
                    break;
                }
            }
        }

        if (added) {
            Collections.sort(highScores);
        }

        return added;
    }

    private void initHighScores() {
        highScores = new ArrayList<>();
        jsonReader = new JsonReader(HIGH_SCORES);
        try {
            highScores = jsonReader.read();
            System.out.println("Successfully read HighScores from: " + HIGH_SCORES);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + HIGH_SCORES);
        } catch (JSONException e) {
            System.out.println("No saved High Scores yet!");
        }
    }

    private void saveHighScores() {
        jsonWriter = new JsonWriter(HIGH_SCORES);
        try {
            jsonWriter.open();
            jsonWriter.write(highScores);
            jsonWriter.close();
            System.out.println("\nSuccessfully saved HighScores to: " + HIGH_SCORES);
        } catch (FileNotFoundException e) {
            System.out.println("\nUnable to write to file: " + HIGH_SCORES);
        }
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

        pausedPanel = new PausedPanel(this, gamePanel);
        cards.add(pausedPanel, PausedPanel.PAUSEDPANEL);

        gameOverPanel = new GameOverPanel(this);
        cards.add(gameOverPanel, GameOverPanel.GAMEOVERPANEL);
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
        } else if (pausedPanel.equals(currentPanel)) {
            showPausedPanel();
        }
    }

    public JPanel getPreviousPanel() { return previousPanel; }

    public JPanel getCurrentPanel() {
        return currentPanel;
    }

    public SoundSystem getSoundSystem() {
        return this.ss;
    }

    private void closeSoundSystem() {
        ss.closeSoundSystem();
        System.out.println("Sound System Closed!");
    }

}
