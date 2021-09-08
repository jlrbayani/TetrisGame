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

// the main frame for the application which handles all of the panel/screen swapping within Tetris
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

    // the constructor for TetrisFrame
    public TetrisFrame() {
        initFrame();
    }

    // EFFECTS: initializes the frame, ensures the starting panel is the Menu and handles saving of high scores at exit
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

    // MODIFIES: this
    // EFFECTS: swaps the current panel with the gamePanel, starts a game
    public void startGame() {
        previousPanel = currentPanel;
        currentPanel = gamePanel;
        gamePanel.startGame();
        cardLayout.show(cards, GamePanel.GAMEPANEL);
    }

    // MODIFIES: this
    // EFFECTS: swaps the current panel with the menuPanel
    public void showMenu() {
        previousPanel = currentPanel;
        currentPanel = menuPanel;
        cardLayout.show(cards, MenuPanel.MENUPANEL);
    }

    // MODIFIES: this
    // EFFECTS: swaps the current panel with the highScoresPanel
    public void showHighScores() {
        previousPanel = currentPanel;
        currentPanel = highScoresPanel;
        cardLayout.show(cards, HighScoresPanel.HIGHSCORESPANEL);
    }

    // MODIFIES: this
    // EFFECTS: swaps the current panel with the optionsPanel
    public void showOptions() {
        previousPanel = currentPanel;
        currentPanel = optionsPanel;
        cardLayout.show(cards, OptionsPanel.OPTIONSPANEL);
    }

    // MODIFIES: this
    // EFFECTS: swaps the current panel with the pausedPanel
    public void showPausedPanel() {
        previousPanel = currentPanel;
        currentPanel = pausedPanel;
        cardLayout.show(cards, PausedPanel.PAUSEDPANEL);
    }

    // MODIFIES: this
    // EFFECTS: swaps the current panel (most likely with the game at a paused state) with the gamePanel
    public void showGamePanel() {
        previousPanel = currentPanel;
        currentPanel = gamePanel;
        cardLayout.show(cards, GamePanel.GAMEPANEL);
    }

    // MODIFIES: this
    // EFFECTS: swaps the current panel (most likely gamePanel) with the gameOverOver panel
    public void showGameOverPanel(Score score) {
        previousPanel = currentPanel;
        currentPanel = gameOverPanel;
        boolean added = addToHighScores(score);
        if (added) {
            setGameOverPanelState(true);
            gameOverPanel.setScore(score);
        }
        gameOverPanel.initPanel();
        cardLayout.show(cards, GameOverPanel.GAMEOVERPANEL);
    }

    // MODIFIES: gameOverPanel
    // EFFECTS: changes the gameOverPanelState
    private void setGameOverPanelState(boolean addScore) {
        gameOverPanel.setHasNewScore(addScore);
    }

    public ArrayList<Score> getHighScores() {
        return highScores;
    }

    // MODIFIES: this
    // EFFECTS: this adds a score to the highScore list and sorts the list as well from greatest to least score value
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

    // MODIFIES: this
    // EFFECTS: initializes the highScore and reads from a file in the string HIGH_SCORES
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

    // MODIFIES: file found from HIGH_SCORES
    // EFFECTS: saves the scores from highScores into the file in HIGH_SCORES
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

    // MODIFIES: this
    // EFFECTS: initializes the cards of panels and adds them to CardLayout
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

    // MODIFIES: this
    // EFFECTS: sets the currentPanel with the previousPanel
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

    public JPanel getCurrentPanel() {
        return currentPanel;
    }

    public SoundSystem getSoundSystem() {
        return this.ss;
    }

    private void closeSoundSystem() {
        ss.closeSoundSystem();
    }

}
