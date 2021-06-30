package main.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Block extends Entity{

    private int colPos, rowPos;
    private int blockType;
    private BufferedImage img;
    private Cell cell;

    public enum Dir {
        DOWN, LEFT, RIGHT
    }

    public Block(TetrisPiece.Type blockType) {
        this.cell = null;
        this.velocityX = 2;

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("resources/blocks/blockI.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getImg() {
        return img;
    }

    @Override
    public void setExtrapolation(double extrapolate) {
        this.extrapolate = extrapolate;
    }

    @Override
    public void update() {
        changeX  += (velocityX * extrapolate);
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("resources/blocks/blockI.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        g2.drawImage(img, (int) (actualX + changeX), actualY, null);

    }
}
