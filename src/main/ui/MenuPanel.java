package main.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel implements ActionListener {
    final static String MENUPANEL = "menuPanel";
    private static final Color backgroundCol = new Color(224, 214, 255);

    private JLabel title;
    private JButton start;
    private TetrisFrame frame;

    public MenuPanel(TetrisFrame frame) {
        super();
        this.frame = frame;

        initButtons();
        initPanel();
        requestFocus();
    }

    // MODIFIES: this
    // EFFECTS: initializes all the main components of the game panel
    private void initButtons() {
        start = new JButton("New Game");
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.addActionListener(this);
    }

    // MODIFIES: this
    // EFFECTS: initializes all the main components of the menu panel
    private void initPanel() {
        setBackground(backgroundCol);

        title = new JLabel("TETRIS");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Courier", Font.PLAIN, 100));

        setPreferredSize(new Dimension(TetrisFrame.WIDTH,TetrisFrame.HEIGHT));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createRigidArea(new Dimension(0, 100)));
        add(title);
        add(Box.createRigidArea(new Dimension(0, 150)));
        add(start);
        add(Box.createRigidArea(new Dimension(0, 50)));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == start) {
            setBackground(new Color(0, 0, 0));
            frame.startGame();
        }
    }
}
