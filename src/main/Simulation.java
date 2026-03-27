package main;

import entities.Aircraft;
import input.KeyHandler;
import input.MouseHandler;
import render.HUD;
import render.Render;

import javax.swing.*;
import java.awt.*;
import java.security.Key;

public class Simulation extends JPanel implements Runnable {

    public final int screenWidth = 1280;
    public final int screenHeight = 720;
    private final int FPS = 120;
    private final int UPS = 200;

    private Thread thread;
    KeyHandler keyH = new KeyHandler(this);
    MouseHandler mouseH = new MouseHandler(this);
    Render render = new Render(this);
    HUD hud = new HUD(this);
    public Aircraft aircraft = new Aircraft(this);

    public final double GRAVITY = 9.81;
    private final double dt = 1.0 / UPS;

    public boolean isStalled = false;

    public Simulation() {
        initializePanel();
        startGameLoop();
    }

    private void initializePanel() {
        this.setMinimumSize(new Dimension(screenWidth, screenHeight));
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setMaximumSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.requestFocus();
        this.addKeyListener(keyH);
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseH);
    }

    private void startGameLoop() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / FPS;
        double timePerUpdate = 1000000000.0 / UPS;

        long previousTime = System.nanoTime();

        int fpsCounter = 0;
        int upsCounter = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {
                update();
                upsCounter++;
                deltaU--;
            }

            if (deltaF >= 1) {
                repaint();
                fpsCounter++;
                deltaF--;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + fpsCounter + " | UPS: " + upsCounter);
                fpsCounter = 0;
                upsCounter = 0;

            }
        }
    }

    public void update() {
        if (!isStalled) {
            aircraft.update(dt);

            if (aircraft.alpha >= 15.0 || aircraft.alpha <= -15.0) {
                isStalled = true;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        render.draw(g2);
        hud.draw(g2);

        g2.dispose();

    }

    public void reset() {
        isStalled = false;
        aircraft = new Aircraft(this);
        render.resetBackground();
    }
}
