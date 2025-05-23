package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // The main window
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Feed The Sprite");

        // Add the game panel
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        /* This causes the window to be sized to fit the preferred
        size and layout of its subcomponents (=main.GamePanel)
        */
        window.pack();

        // Place the window in the middle of the page
        // Make the window visible
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.playMusic();
        gamePanel.startGameThread();
    }
}
