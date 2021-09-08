package main.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

// This Panel represents the options menu where the user can change their volume settings, look at the Controls, or return to the previous panel they were in
public class OptionsPanel extends JPanel {
    final static String OPTIONSPANEL = "optionsPanel";
    final static String OPTIONSMENU = "optionsMenuCard";
    final static String SOUNDSETTINGS = "soundSettings";
    final static String CONTROLS = "controls";
    private static final Color backgroundCol = new Color(43, 42, 42);

    private TetrisFrame frame;
    private SoundSystem ss;
    private PanelWithActList soundSettings, controls, optionsMenu;
    private JPanel optionsCards;
    private CardLayout optionsCardLayout;
    private PanelWithMouseList volumeControl;

    private StandardButton soundSettingsButton, controlsButton, backButton;
    private StandardButton controlsBackButton;
    private StandardButton soundsBackButton;
    private ImageIcon mutedIcon, unmutedIcon;
    private JButton muteButton;
    private BufferedImage controlsImg;

    // the constructor for the optionPanel
    public OptionsPanel(TetrisFrame frame) {
        super();
        this.frame = frame;
        this.ss = frame.getSoundSystem();
        setBackground(backgroundCol);
        setPreferredSize(new Dimension(TetrisFrame.WIDTH, TetrisFrame.HEIGHT));

        initCards();
        initOptionsMenu();
        initSoundSettings();
        initControls();

        showOptionsMenu();
    }

    // MODIFIES: this
    // EFFECTS: adds the different panels made for the CardLayout unique to the optionsPanel
    private void initCards() {
        optionsCards = new JPanel(new CardLayout(0, 0));
        optionsCardLayout = (CardLayout) (optionsCards.getLayout());
        add(optionsCards);

        optionsMenu = new PanelWithActList() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object src = e.getSource();
                if (src == soundSettingsButton) {
                    showSoundSettings();
                } else if (src == controlsButton) {
                    showControls();
                } else if (src == backButton) {
                    returnToPreviousMainPanel();
                }
            }
        };
        optionsCards.add(optionsMenu, OPTIONSMENU);

        soundSettings = new PanelWithActList() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object src = e.getSource();
                if (src == soundsBackButton) {
                    showOptionsMenu();
                } else if (src == muteButton) {
                    if (ss.isMuted()) {
                        ss.unmute();
                        muteButton.setIcon(unmutedIcon);
                    } else if (!ss.isMuted()) {
                        ss.mute();
                        muteButton.setIcon(mutedIcon);
                    }
                }
            }
        };
        optionsCards.add(soundSettings, SOUNDSETTINGS);

        controlsImg = null;
        try {
            controlsImg = ImageIO.read(getClass().getResource("/icons/controls.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        controls = new PanelWithActList() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.drawImage(controlsImg, 170, 50, null);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                Object src = e.getSource();
                if (src == controlsBackButton) {
                    showOptionsMenu();
                }
            }
        };
        optionsCards.add(controls, CONTROLS);
    }

    // MODFIES: this
    // EFFECTS: initializes the starting screen for the options Menu and adds all the components and buttons to the panel
    private void initOptionsMenu() {
        optionsMenu.setPreferredSize(new Dimension(TetrisFrame.WIDTH, TetrisFrame.HEIGHT));
        optionsMenu.setLayout(new BoxLayout(optionsMenu, BoxLayout.Y_AXIS));
        optionsMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsMenu.setBackground(backgroundCol);

        soundSettingsButton = new StandardButton("SOUNDS");
        soundSettingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundSettingsButton.addActionListener(optionsMenu);
        controlsButton = new StandardButton("CONTROLS");
        controlsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlsButton.addActionListener(optionsMenu);
        backButton = new StandardButton("BACK");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(optionsMenu);

        optionsMenu.add(Box.createRigidArea(new Dimension(0, 200)));
        optionsMenu.add(soundSettingsButton);
        optionsMenu.add(Box.createRigidArea(new Dimension(0, 100)));
        optionsMenu.add(controlsButton);
        optionsMenu.add(Box.createRigidArea(new Dimension(0, 100)));
        optionsMenu.add(backButton);
    }

    // MODIFIES: this
    // EFFECTS: initializes the UI for the SoundSettings Panel
    private void initSoundSettings() {
        soundSettings.setPreferredSize(new Dimension(TetrisFrame.WIDTH, TetrisFrame.HEIGHT));
        soundSettings.setLayout(new BoxLayout(soundSettings, BoxLayout.Y_AXIS));
        soundSettings.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundSettings.setBackground(backgroundCol);

        volumeControl = initVolumeControl();

        soundSettings.add(Box.createRigidArea(new Dimension(0, 200)));
        soundSettings.add(volumeControl);
        soundSettings.add(Box.createRigidArea(new Dimension(0, 10)));

        unmutedIcon = new ImageIcon (getClass().getResource("/icons/unmutedButton.png"));
        mutedIcon = new ImageIcon(getClass().getResource("/icons/mutedButton.png"));
        muteButton = new JButton(unmutedIcon);
        muteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        muteButton.setMaximumSize(new Dimension(unmutedIcon.getIconWidth(), unmutedIcon.getIconHeight()));
        muteButton.addActionListener(soundSettings);

        soundSettings.add(muteButton);
        soundSettings.add(Box.createRigidArea(new Dimension(0, 100)));


        soundsBackButton = new StandardButton("BACK");
        soundsBackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundsBackButton.addActionListener(soundSettings);

        soundSettings.add(soundsBackButton);

    }

    // MODIFIES: this
    // EFFECTS: creates a "slider" interface for controlling the volume in the SoundSystem
    private PanelWithMouseList initVolumeControl() {
        return new PanelWithMouseList() {
            private int xPos;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.drawRect(0, 0, WIDTH, HEIGHT);

                int currentVolumeVisual = (int) (ss.getCurrentVolume() * WIDTH);
                int emptySpaceVisual = WIDTH - currentVolumeVisual;

                g2.setColor(Color.GREEN);
                g2.fillRect(0, 0, currentVolumeVisual, HEIGHT);
                g2.setColor(Color.WHITE);
                g2.fillRect(currentVolumeVisual, 0, emptySpaceVisual, HEIGHT);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                this.xPos = e.getX();
                float volumeRatio = (float) xPos / (float) WIDTH;
                ss.setVolume(volumeRatio);
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.getX() > WIDTH) {
                    ss.setVolume(1f);
                } else if (e.getX() < 0) {
                    ss.setVolume(0f);
                }
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                this.requestFocusInWindow();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                soundSettings.requestFocusInWindow();
            }
        };
    }

    // MODIFIES: this
    // EFFECTS: initializes the panel for the Controls screen
    private void initControls() {
        controls.setPreferredSize(new Dimension(TetrisFrame.WIDTH, TetrisFrame.HEIGHT));
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setAlignmentX(Component.CENTER_ALIGNMENT);
        controls.setBackground(backgroundCol);

        controlsBackButton = new StandardButton("BACK");
        controlsBackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlsBackButton.addActionListener(controls);

        controls.add(Box.createRigidArea(new Dimension(0, 600)));
        controls.add(controlsBackButton);
    }

    private void showOptionsMenu() {
        optionsMenu.requestFocusInWindow();
        optionsCardLayout.show(optionsCards, OPTIONSMENU);
    }

    private void showSoundSettings() {
        soundSettings.requestFocusInWindow();
        optionsCardLayout.show(optionsCards, SOUNDSETTINGS);
    }

    private void showControls() {
        controls.requestFocusInWindow();
        optionsCardLayout.show(optionsCards, CONTROLS);
    }

    private void returnToPreviousMainPanel() {
        frame.returnToPreviousPanel();
    }

    // creates a panel with an ActionListener
    private abstract static class PanelWithActList extends JPanel implements ActionListener {
        public PanelWithActList() {
            super();
        }

        @Override
        public abstract void actionPerformed(ActionEvent e);
    }

    // creates a panel with a mouseListener and a MouseMotionListener
    private abstract static class PanelWithMouseList extends JPanel implements MouseListener, MouseMotionListener {
        final static int WIDTH = 200;
        final static int HEIGHT = 30;
        public PanelWithMouseList() {
            super();
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setMaximumSize(new Dimension(WIDTH, HEIGHT));
            setBackground(backgroundCol);
            setAlignmentX(Component.CENTER_ALIGNMENT);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseMoved(MouseEvent e) {}
    }
}

