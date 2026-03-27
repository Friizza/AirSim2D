package input;

import main.Simulation;
import render.HUD;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {

    Simulation sim;
    boolean isDraggingSlider = false;

    public MouseHandler(Simulation sim) {
        this.sim = sim;
    }

    private void updateThrottleFromMouse(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (mx >= HUD.THRUST_SLIDER_X - 10 && mx <= HUD.THRUST_SLIDER_X + HUD.SLIDER_WIDTH + 10) {
            if (my >= HUD.SLIDER_Y - 10 && my <= HUD.SLIDER_Y + HUD.SLIDER_HEIGHT + 10) {
                isDraggingSlider = true;
            }
        }

        if (isDraggingSlider) {
            double rawThrottle = 1.0 - ((double)(my - HUD.SLIDER_Y) / HUD.SLIDER_HEIGHT);

            if (rawThrottle > 1.0) rawThrottle = 1.0;
            if (rawThrottle < 0.0) rawThrottle = 0.0;

            sim.aircraft.throttle = rawThrottle;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (mx >= HUD.AUTO_TRIM_BTN_X && mx <= HUD.AUTO_TRIM_BTN_X + HUD.AUTO_TRIM_BTN_WIDTH &&
                my >= HUD.AUTO_TRIM_BTN_Y && my <= HUD.AUTO_TRIM_BTN_Y + HUD.AUTO_TRIM_BTN_HEIGHT) {

            sim.aircraft.autoTrim = !sim.aircraft.autoTrim;
        }

        updateThrottleFromMouse(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateThrottleFromMouse(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isDraggingSlider = false;
    }
}