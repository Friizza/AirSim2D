package render;

import main.Simulation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Render {

    Simulation sim;

    BufferedImage background, a320;

    private double backgroundX = 0;
    private double backgroundY = 0;
    private final int BG_WIDTH = 1699;
    private final int BG_HEIGHT = 720;
    private final double PIXELS_PER_METER = 8;

    Color overlayColor = new Color(0, 0, 0, 80);

    public Render(Simulation sim) {
        this.sim = sim;

        loadAssets();
    }

    private void loadAssets() {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/background.png"));
            a320 = ImageIO.read(getClass().getResourceAsStream("/a320.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        updateBackground();

        int bx = (int) backgroundX;
        int by = (int) backgroundY;

        g2.drawImage(background, bx, by, BG_WIDTH, BG_HEIGHT, null);                           // In alto a sinistra
        g2.drawImage(background, bx + BG_WIDTH, by, BG_WIDTH, BG_HEIGHT, null);                // In alto a destra
        g2.drawImage(background, bx, by + BG_HEIGHT, BG_WIDTH, BG_HEIGHT, null);               // In basso a sinistra
        g2.drawImage(background, bx + BG_WIDTH, by + BG_HEIGHT, BG_WIDTH, BG_HEIGHT, null);    // In basso a destra

        g2.setColor(overlayColor);
        g2.fillRect(0, 0, sim.screenWidth, sim.screenHeight);

        double pitchRad = Math.toRadians(-sim.aircraft.pitch);
        int pivotX = (int)sim.aircraft.position.x + 260;
        int pivotY = (int)sim.aircraft.position.y + 168;
        g2.rotate(pitchRad, pivotX, pivotY);

        g2.drawImage(a320, (int)sim.aircraft.position.x, (int)sim.aircraft.position.y, 526, 250, null);
        g2.rotate(-pitchRad, pivotX, pivotY);
    }

    private void updateBackground() {
        double shiftX = sim.aircraft.velocity.x * PIXELS_PER_METER * (1.0 / 120.0);
        double shiftY = sim.aircraft.velocity.y * PIXELS_PER_METER * (1.0 / 120.0);

        backgroundX -= shiftX;
        backgroundY -= shiftY;

        if (backgroundX <= -BG_WIDTH) {
            backgroundX += BG_WIDTH;
        } else if (backgroundX > 0) {
            backgroundX -= BG_WIDTH;
        }

        if (backgroundY <= -BG_HEIGHT) {
            backgroundY += BG_HEIGHT;
        } else if (backgroundY > 0) {
            backgroundY -= BG_HEIGHT;
        }
    }

    public void resetBackground() {
        this.backgroundX = 0;
        this.backgroundY = 0;
    }
}
