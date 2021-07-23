package main.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

            }
        };
        optionsCards.add(optionsMenu, OPTIONSMENU);

        soundSettings = new PanelWithActList() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
        optionsCards.add(soundSettings, SOUNDSETTINGS);

        controls = new PanelWithActList() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
        optionsCards.add(controls, CONTROLS);
    }

    private void initOptionsMenu() {
        optionsMenu.setPreferredSize(new Dimension(TetrisFrame.WIDTH, TetrisFrame.HEIGHT));
        optionsMenu.setLayout(new BoxLayout(optionsMenu, BoxLayout.Y_AXIS));
        optionsMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsMenu.setBackground(backgroundCol);


    }

    private void initSoundSettings() {

    }

    private void initControls() {

    }

    private void showOptionsMenu() {
        optionsCardLayout.show(optionsCards, OPTIONSMENU);
        optionsMenu.requestFocus();
    }

    private void showSoundSettings() {
        optionsCardLayout.show(optionsCards, SOUNDSETTINGS);
        soundSettings.requestFocus();
    }

    private void showControls() {
        optionsCardLayout.show(optionsCards, CONTROLS);
        controls.requestFocus();
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
}

