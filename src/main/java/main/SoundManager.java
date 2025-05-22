package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class SoundManager {
    Clip clip;
    URL[] soundURL = new URL[3];

    public SoundManager() {
        // Main menu music
        soundURL[0] = getClass().getResource("/sounds/MenuMusic.wav");
        // Eating sound
        soundURL[1] = getClass().getResource("/sounds/nom-nom.wav");
        // Click sound for menus
        soundURL[2] = getClass().getResource("/sounds/clicked.wav");
    }

    void setFile(int i) {
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(inputStream);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    void play() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    void loop() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}
