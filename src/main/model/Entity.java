package main.model;

import java.awt.*;

public interface Entity {

    void setExtrapolation(double extrapolate);
    void update();
    void draw(Graphics2D g2);
}
