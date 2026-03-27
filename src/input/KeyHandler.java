package input;

import main.Simulation;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    Simulation sim;

    public KeyHandler(Simulation sim) {
        this.sim = sim;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (sim.isStalled) {
            if (code == KeyEvent.VK_ENTER) {
                sim.reset();
            }
            return;
        }

        if (code == KeyEvent.VK_ENTER) {
            sim.reset();
        }

        if(code == KeyEvent.VK_W) {
            sim.aircraft.pitch -= 0.5;
            sim.aircraft.pitchRate = 0;
        }
        if(code == KeyEvent.VK_S) {
            sim.aircraft.pitch += 0.5;
            sim.aircraft.pitchRate = 0;
        }

        // Se l'utente preme le freccette stacca l'auto-trim
        if(code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
            sim.aircraft.autoTrim = false;

            if(code == KeyEvent.VK_UP) {
                sim.aircraft.elevatorAngle += 0.5;
                if(sim.aircraft.elevatorAngle > 25.0) sim.aircraft.elevatorAngle = 25.0;
            }
            if(code == KeyEvent.VK_DOWN) {
                sim.aircraft.elevatorAngle -= 0.5;
                if(sim.aircraft.elevatorAngle < -25.0) sim.aircraft.elevatorAngle = -25.0;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}