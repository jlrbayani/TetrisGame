package main.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
                }
            }
        };
        optionsCards.add(soundSettings, SOUNDSETTINGS);

        controls = new PanelWithActList() {
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

    private void initSoundSettings() {
        soundSettings.setPreferredSize(new Dimension(TetrisFrame.WIDTH, TetrisFrame.HEIGHT));
        soundSettings.setLayout(new BoxLayout(soundSettings, BoxLayout.Y_AXIS));
        soundSettings.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundSettings.setBackground(backgroundCol);

        volumeControl = new PanelWithMouseList() {
            private int xPos;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.drawRect(0, 0, WIDTH, HEIGHT);

                g2.setColor(Color.RED);
                g2.fillRect(volumeControl.leftOffset, 0, (int) (ss.getCurrentVolume() * (WIDTH - volumeControl.rightOffset)), HEIGHT);
                g2.setColor(Color.WHITE);
                g2.fillRect((int) (ss.getCurrentVolume() * WIDTH), 0, WIDTH - volumeControl.rightOffset - (int) (ss.getCurrentVolume() * WIDTH), HEIGHT);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                this.xPos = e.getX();
                System.out.println("xPos: " + xPos);
                float volumeRatio;
                if (xPos < volumeControl.leftOffset) {
                    volumeRatio = 0;
                } else if (xPos > WIDTH - volumeControl.rightOffset) {
                    volumeRatio = 1;
                } else {
                    volumeRatio = (float) xPos / (float) WIDTH;
                }
                ss.setVolume(volumeRatio);
                this.repaint();
                System.out.println("Clicked!");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                this.requestFocusInWindow();
                System.out.println("Entered!");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                soundSettings.requestFocusInWindow();
                System.out.println("Exited!");
            }
        };

        soundSettings.add(Box.createRigidArea(new Dimension(0, 200)));
        soundSettings.add(volumeControl);
        soundSettings.add(Box.createRigidArea(new Dimension(0, 200)));

        soundsBackButton = new StandardButton("BACK");
        soundsBackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundsBackButton.addActionListener(soundSettings);

        soundSettings.add(soundsBackButton);

    }

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

    private abstract class PanelWithActList extends JPanel implements ActionListener {
        public PanelWithActList() {
            super();
        }

        @Override
        public abstract void actionPerformed(ActionEvent e);
    }

    private abstract class PanelWithMouseList extends JPanel implements MouseListener {
        final static int WIDTH = 220;
        final static int HEIGHT = 30;
        private int leftOffset, rightOffset;
        public PanelWithMouseList() {
            super();
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setMaximumSize(new Dimension(WIDTH, HEIGHT));
            setBackground(backgroundCol);
            setAlignmentX(Component.CENTER_ALIGNMENT);
            addMouseListener(this);
            leftOffset = 10;
            rightOffset = 10;
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}
    }
}

