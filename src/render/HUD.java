package render;

import main.Simulation;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class HUD {

    Simulation sim;

    Font roboto;

    public static final int THRUST_SLIDER_X = 30;
    public static final int PITCH_SLIDER_X = 90;
    public static final int AIRSPEED_SLIDER_X = 150;
    public static final int VSPEED_SLIDER_X = 210;
    public static final int ELEV_SLIDER_X = 270;

    public static final int SLIDER_Y = 480;
    public static final int SLIDER_WIDTH = 40;
    public static final int SLIDER_HEIGHT = 200;

    public static final int AUTO_TRIM_BTN_X = 330;
    public static final int AUTO_TRIM_BTN_Y = 480;
    public static final int AUTO_TRIM_BTN_WIDTH = 110;
    public static final int AUTO_TRIM_BTN_HEIGHT = 40;

    public static final double MAX_PITCH_DISPLAY = 45.0;
    public static final double MAX_AIRSPEED_DISPLAY = 400.0;
    public static final double MAX_VSPEED_DISPLAY = 50.0;
    public static final double MAX_ELEV_DISPLAY = 25.0;

    public static final int ALT_X = 1200;
    public static final int ALT_Y = 100;
    public static final int ALT_HEIGHT = 500;
    public static final double MAX_ALTITUDE = 15000.0;

    public HUD(Simulation sim) {
        this.sim = sim;

        loadFont();
    }

    private void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/font/roboto.ttf");
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);

            roboto = baseFont.deriveFont(16f);

        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        g2.setFont(roboto);
        g2.setColor(new Color(255, 255, 255, 200));

        int x = 20;
        int y = 40;
        int gap = 25;

        g2.drawString("=== FLIGHT DATA ===", x, y); y += gap;
        g2.drawString(String.format("ALTITUDE     : %.0f m", sim.aircraft.altitude), x, y); y += gap;
        g2.drawString(String.format("AIRSPEED     : %.3f m/s", sim.aircraft.velocity.x), x, y); y += gap;
        g2.drawString(String.format("VERT.SPD     : %.1f m/s", -sim.aircraft.velocity.y), x, y); y += gap;
        y += 10; // Spazio extra per separare i blocchi

        g2.drawString("=== ENGINE & FORCES ===", x, y); y += gap;
        g2.drawString(String.format("THROTTLE     : %.0f %%", sim.aircraft.throttle * 100), x, y); y += gap;
        g2.drawString(String.format("MAX THRUST   : %.0f N", sim.aircraft.currentAvailableThrust), x, y); y += gap;
        g2.drawString(String.format("THRUST       : %.0f N", sim.aircraft.thrust.x), x, y); y += gap;
        g2.drawString(String.format("DRAG         : %.0f N", sim.aircraft.currentDrag), x, y); y += gap;
        g2.drawString(String.format("LIFT         : %.0f N", sim.aircraft.currentLift), x, y); y += gap;
        g2.drawString(String.format("WEIGHT       : %.0f N", sim.aircraft.mass * sim.GRAVITY), x, y); y += gap;
        y += 10;

        g2.drawString("=== AERODYNAMICS ===", x, y); y += gap;
        g2.drawString(String.format("AoA (Alpha): %+.1f °", sim.aircraft.alpha), x, y); y += gap;
        g2.drawString(String.format("PITCH      : %+.1f °", sim.aircraft.pitch), x, y); y += gap;
        g2.drawString(String.format("C_L  : %.3f", sim.aircraft.liftCoefficient), x, y); y += gap;
        g2.drawString(String.format("C_D  : %.3f", sim.aircraft.dragCoefficient), x, y); y += gap;

        // DRAW SLIDER
        drawSlider(g2, THRUST_SLIDER_X, sim.aircraft.throttle, 0, 1.0, "THR", Color.GREEN, false);
        drawSlider(g2, PITCH_SLIDER_X, sim.aircraft.pitch, -MAX_PITCH_DISPLAY, MAX_PITCH_DISPLAY, "PIT", Color.CYAN, true);

        // AIRSPEED
        double airspeed = Math.sqrt(Math.pow(sim.aircraft.velocity.x, 2) + Math.pow(sim.aircraft.velocity.y, 2));
        Color spdColor = airspeed > 340.0 ? new Color(255, 0, 0, 200) : new Color(0, 150, 255, 200);
        drawSlider(g2, AIRSPEED_SLIDER_X, airspeed, 0, MAX_AIRSPEED_DISPLAY, "SPD", spdColor, false);

        drawSlider(g2, VSPEED_SLIDER_X, -sim.aircraft.velocity.y, -MAX_VSPEED_DISPLAY, MAX_VSPEED_DISPLAY, "V/S", Color.MAGENTA, true);
        drawSlider(g2, ELEV_SLIDER_X, sim.aircraft.elevatorAngle, -MAX_ELEV_DISPLAY, MAX_ELEV_DISPLAY, "ELV", Color.ORANGE, true);

        // AUTO TRIM BUTTON
        if (sim.aircraft.autoTrim) {
            g2.setColor(new Color(0, 255, 0, 120));
        } else {
            g2.setColor(new Color(255, 0, 0, 120));
        }
        g2.fillRect(AUTO_TRIM_BTN_X, AUTO_TRIM_BTN_Y, AUTO_TRIM_BTN_WIDTH, AUTO_TRIM_BTN_HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(AUTO_TRIM_BTN_X, AUTO_TRIM_BTN_Y, AUTO_TRIM_BTN_WIDTH, AUTO_TRIM_BTN_HEIGHT);

        g2.setFont(roboto.deriveFont(Font.BOLD, 14f));
        g2.drawString("AUTO TRIM", AUTO_TRIM_BTN_X + 15, AUTO_TRIM_BTN_Y + 25);

        // ALTITUDE METER
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(ALT_X, ALT_Y, ALT_X, ALT_Y + ALT_HEIGHT);
        g2.drawLine(ALT_X - 10, ALT_Y, ALT_X + 10, ALT_Y);
        g2.drawLine(ALT_X - 10, ALT_Y + ALT_HEIGHT, ALT_X + 10, ALT_Y + ALT_HEIGHT);

        g2.setFont(roboto.deriveFont(14f));
        g2.drawString("15km", ALT_X - 15, ALT_Y - 10);
        g2.drawString("GND", ALT_X - 15, ALT_Y + ALT_HEIGHT + 20);

        double altRatio = sim.aircraft.altitude / MAX_ALTITUDE;
        if (altRatio > 1.0) altRatio = 1.0;
        if (altRatio < 0.0) altRatio = 0.0;
        int dotY = ALT_Y + ALT_HEIGHT - (int)(altRatio * ALT_HEIGHT);

        g2.setColor(new Color(0, 255, 255));
        g2.fillOval(ALT_X - 6, dotY - 6, 12, 12);
        g2.drawString(String.format("%.0f m", sim.aircraft.altitude), ALT_X + 15, dotY + 5);

        drawVectors(g2);

        // STALL WARNING
        if (!sim.isStalled) {
            String warning = "";
            if (sim.aircraft.alpha >= 12) {
                warning = "STALL WARNING";
            } else if (sim.aircraft.alpha <= -12) {
                warning = "NEGATIVE STALL WARNING";
            }

            if (!warning.isEmpty()) {
                g2.setFont(roboto.deriveFont(Font.BOLD, 36f));
                g2.setColor(Color.RED);
                int textWidth = g2.getFontMetrics().stringWidth(warning);
                g2.drawString(warning, (sim.screenWidth / 2) - (textWidth / 2), sim.screenHeight - 50);
            }
        }

        if (sim.isStalled) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, sim.screenWidth, sim.screenHeight);

            g2.setFont(roboto.deriveFont(Font.BOLD, 80f));
            g2.setColor(Color.RED);

            String stalledText = "";
            if(sim.aircraft.alpha >= 15.0) {
                stalledText = "STALLED";
            } else if (sim.aircraft.alpha <= 15.0) {
                stalledText = "NEGATIVE STALL";
            }

            int stWidth = g2.getFontMetrics().stringWidth(stalledText);
            g2.drawString(stalledText, (sim.screenWidth / 2) - (stWidth / 2), sim.screenHeight / 2 - 20);

            g2.setFont(roboto.deriveFont(Font.BOLD, 24f));
            g2.setColor(Color.WHITE);
            String restartText = "Press ENTER to restart";
            int rWidth = g2.getFontMetrics().stringWidth(restartText);
            g2.drawString(restartText, (sim.screenWidth / 2) - (rWidth / 2), sim.screenHeight / 2 + 50);
        }
    }

    public void drawVectors(Graphics2D g2) {
        int cx = (int)sim.aircraft.position.x + 260;
        int cy = (int)sim.aircraft.position.y + 168;

        double scale = 0.0003;
        double vMag = Math.sqrt(Math.pow(sim.aircraft.velocity.x, 2) + Math.pow(sim.aircraft.velocity.y, 2));
        if (vMag == 0) vMag = 0.001;
        double vDirX = sim.aircraft.velocity.x / vMag;
        double vDirY = sim.aircraft.velocity.y / vMag;

        int wy = (int)(sim.aircraft.mass * sim.GRAVITY * scale);
        drawVectorLine(g2, cx, cy, cx, cy + wy, Color.YELLOW, "W");

        int tx = (int)(sim.aircraft.thrust.x * scale);
        int ty = (int)(sim.aircraft.thrust.y * scale);
        drawVectorLine(g2, cx, cy, cx + tx, cy + ty, Color.GREEN, "T");

        int dx = (int)(-vDirX * sim.aircraft.currentDrag * scale);
        int dy = (int)(-vDirY * sim.aircraft.currentDrag * scale);
        drawVectorLine(g2, cx, cy, cx + dx, cy + dy, Color.RED, "D");

        double angle = Math.toRadians(-sim.aircraft.pitch);
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);

        int oxW = 314 - 260;
        int oyW = 161 - 168;
        int rxW = cx + (int)(oxW * cosA - oyW * sinA);
        int ryW = cy + (int)(oxW * sinA + oyW * cosA);

        int lxW = (int)(vDirY * sim.aircraft.currentWingLift * scale);
        int lyW = (int)(-vDirX * sim.aircraft.currentWingLift * scale);
        drawVectorLine(g2, rxW, ryW, rxW + lxW, ryW + lyW, Color.CYAN, "L_w");

        int oxT = 61 - 260;
        int oyT = 141 - 168;
        int rxT = cx + (int)(oxT * cosA - oyT * sinA);
        int ryT = cy + (int)(oxT * sinA + oyT * cosA);

        int lxT = (int)(vDirY * sim.aircraft.currentTailLift * scale);
        int lyT = (int)(-vDirX * sim.aircraft.currentTailLift * scale);
        drawVectorLine(g2, rxT, ryT, rxT + lxT, ryT + lyT, Color.MAGENTA, "L_t");
    }

    private void drawVectorLine(Graphics2D g2, int x1, int y1, int x2, int y2, Color color, String label) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(x1, y1, x2, y2);

        g2.fillOval(x2 - 4, y2 - 4, 8, 8);

        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2.drawString(label, x2 + 8, y2 + 5);
    }

    private void drawSlider(Graphics2D g2, int x, double value, double min, double max, String label, Color color, boolean centered) {
        g2.setColor(new Color(50, 50, 50, 200));
        g2.fillRect(x, SLIDER_Y, SLIDER_WIDTH, SLIDER_HEIGHT);

        if (centered) {
            int centerY = SLIDER_Y + SLIDER_HEIGHT / 2;
            g2.setColor(new Color(255, 255, 255, 100));
            g2.drawLine(x, centerY, x + SLIDER_WIDTH, centerY);

            if (value > max) value = max;
            if (value < min) value = min;

            double ratio = value / max;
            int dotY = centerY - (int)(ratio * (SLIDER_HEIGHT / 2));

            g2.setColor(color);
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(x - 5, dotY, x + SLIDER_WIDTH + 5, dotY);
        } else {
            double ratio = value / max;
            if (ratio > 1.0) ratio = 1.0;
            if (ratio < 0.0) ratio = 0.0;

            int fillHeight = (int) (SLIDER_HEIGHT * ratio);
            int fillY = SLIDER_Y + SLIDER_HEIGHT - fillHeight;

            g2.setColor(color);
            g2.fillRect(x, fillY, SLIDER_WIDTH, fillHeight);
        }

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, SLIDER_Y, SLIDER_WIDTH, SLIDER_HEIGHT);
        g2.drawString(label, x + 5, SLIDER_Y - 10);
    }

}
