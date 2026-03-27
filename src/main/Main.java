package main;

import javax.swing.*;

public class Main {

    public static JFrame window;

    public static void main(String[] args) {

        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("AirSim2D");
        window.setResizable(false);

        Simulation sim = new Simulation();
        window.add(sim);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

}
