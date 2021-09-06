package main.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class MenuPanel extends JPanel implements ActionListener {
    final static String MENUPANEL = "menuPanel";
    private static final Color backgroundCol = new Color(43, 42, 42);

    private JLabel title;
    private StandardButton start, highScores, options;
    private TetrisFrame frame;

    public MenuPanel(TetrisFrame frame) {
        super();
        this.frame = frame;

        initButtons();
        initPanel();
        requestFocus();
    }

    // MODIFIES: this
    // EFFECTS: initializes all the main buttons of the menu panel
    private void initButtons() {
        start = new StandardButton("START");
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.addActionListener(this);

        highScores = new StandardButton("HIGH SCORES");
        highScores.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScores.addActionListener(this);

        options = new StandardButton("OPTIONS");
        options.setAlignmentX(Component.CENTER_ALIGNMENT);
        options.addActionListener(this);
    }

    // MODIFIES: this
    // EFFECTS: initializes all the main components of the menu panel
    private void initPanel() {
        setBackground(backgroundCol);
        URL url = getClass().getResource("/icons/TetrisLogo.png");
        ImageIcon tetrisLogo = new ImageIcon(url);

        title = new JLabel(tetrisLogo);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        setPreferredSize(new Dimension(TetrisFrame.WIDTH,TetrisFrame.HEIGHT));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createRigidArea(new Dimension(0, 100)));
        add(title);
        add(Box.createRigidArea(new Dimension(0, 100)));
        add(start);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(highScores);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(options);
        add(Box.createRigidArea(new Dimension(0, 50)));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == start) {
            frame.startGame();
        } else if (src == highScores) {
            frame.showHighScores();
        } else if (src == options) {
            frame.showOptions();
        }
    }
}
