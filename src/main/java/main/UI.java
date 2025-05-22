package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Objects;

public class UI {
    // Main GamePanel
    GamePanel gp;

    // Font InputStreams
    private final InputStream inputStream1 = getClass().getResourceAsStream("/font/pressStart.ttf");
    private final InputStream instructionStream = getClass().getResourceAsStream("/font/pressStart.ttf");
    private final InputStream bigTitle = getClass().getResourceAsStream("/font/pressStart.ttf");
    private Font pressStart, instructions, titleFont;

    // Main Graphics object
    private Graphics2D g2;
    private final BufferedImage coinImage;

    // Times
    private double elapsedTime, startTime;
    boolean setStartTime = false;
    private boolean timeRecorded = false, reset = false;

    // Booleans and Rectangles for clicking regions
    boolean areEffectsOn = true;
    private boolean isMusicOn = true, isTimerOn = true, areCoinsOn = true;
    private final int quitHeight = 530;
    private final int lineSpace = 50;
    private final Rectangle playGame = new Rectangle(240,quitHeight - (lineSpace*4) - 32,288,32);
    private final Rectangle howToPlay = new Rectangle(208,quitHeight - (lineSpace*3) - 32,352,32);
    private final Rectangle leaderboards = new Rectangle(208,quitHeight - (lineSpace*2) - 32, 352, 32);
    private final Rectangle settings = new Rectangle(256, quitHeight - (lineSpace) - 32, 256, 32);
    private final Rectangle quitGame = new Rectangle(320,quitHeight - 32,128,32);
    private final Rectangle backToMainMenu = new Rectangle(240,494,288,32);
    private final Rectangle clearRecords = new Rectangle(48, 442, 234, 18);
    private final Rectangle playAgain = new Rectangle(224,530 - lineSpace - 32, 320,32);
    private final Rectangle yesClear = new Rectangle(192, 100 + (lineSpace*3) - 32, 96, 32);
    private final Rectangle noClear = new Rectangle(512, 100 + (lineSpace*3) - 32, 64,32);
    private final Rectangle musicOn = new Rectangle(464, 100 - 32, 64, 32);
    private final Rectangle musicOff = new Rectangle(464, 100 - 32, 96, 32);
    private final Rectangle effectsOn = new Rectangle(512,100 + lineSpace - 32,64,32);
    private final Rectangle effectsOff = new Rectangle(512,100 + lineSpace - 32,96,32);
    private final Rectangle timerOn = new Rectangle(544,100 + (lineSpace*2) - 32,64,32);
    private final Rectangle timerOff = new Rectangle(544,100 + (lineSpace*2) - 32,96,32);
    private final Rectangle coinsOn = new Rectangle(544,100 + (lineSpace*3) - 32,64,32);
    private final Rectangle coinsOff = new Rectangle(544,100 + (lineSpace*3) - 32,96,32);
    private final Rectangle endGame = new Rectangle(600, 530, 270,30);


    // Set fonts
    {
        try {
            titleFont = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(bigTitle)).deriveFont(40f);
            pressStart = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(inputStream1)).deriveFont(32f);
            instructions = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(instructionStream)).deriveFont(18f);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Global constructor
    public UI(GamePanel gp) {
        this.gp = gp;
        coinImage = gp.snacks[0].image;
    }

    // UI Draw method
    void draw(Graphics2D g2) {
        this.g2 = g2;
        if (gp.gameState == gp.endingScreen) {
            if (!reset) {
                // Reset attributes for the next game
                gp.resetAttributes();
                reset = true;
            }
            if (!timeRecorded) {
                // Store time in file
                gp.storedTimes.add(new Time(gp.storedTimes.size() + 1, formatTime(elapsedTime)));
                timeRecorded = true;
                gp.updateTimes();
            }
            drawEndingScreen();
        }
        else if (gp.gameState == gp.titleScreen) {
            drawTitleScreen();
        }
        else if (gp.gameState == gp.howToPlayScreen) {
            drawInstructionScreen();
        }
        else if (gp.gameState == gp.leaderboardScreen) {
            drawLeaderboardScreen();
        }
        else if (gp.gameState == gp.settingsScreen) {
            drawSettingsScreen();
        }
        else if (gp.gameState == gp.areYouSureScreen) {
            drawAreYouSureScreen();
        }
        else if (gp.gameState == gp.playingGame) {
            if (!setStartTime) {
                startTime = System.currentTimeMillis();
                setStartTime = true;
            }
            g2.setFont(new Font("Cambria", Font.PLAIN, 30));
            g2.setColor(Color.WHITE);
            if (areCoinsOn) {
                g2.drawImage(coinImage, 0, 0, gp.tileSize, gp.tileSize, null);
                g2.drawString("x " + gp.sprite.coinCount, 42,33);
            }
            elapsedTime = System.currentTimeMillis() - startTime;
            if (isTimerOn) {
                g2.drawString("Time: " + formatTime(elapsedTime), 562, 33);
            }

            g2.setFont(new Font("Cambria", Font.BOLD, 30));
            g2.drawString("Quit Game", 600,560);
            if (endGame.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
                g2.setColor(Color.RED);
                g2.drawString("Quit Game", 600,560);
                g2.setColor(Color.WHITE);
                if (gp.mouseH.mouseClicked) {
                    gp.gameState = gp.titleScreen;
                    gp.resetAttributes();
                    if (areEffectsOn) {
                        gp.playEffect(2);
                    }
                }
            }

            if (gp.mouseH.mouseClicked) {
                gp.mouseH.clearMouseClick();
            }
        }
    }

    // Formatting system time for display
    private String formatTime(double time) {
        int minsUnit = (int) ((time / (1000 * 60)) % 60) % 10;
        int minsTens = (int) ((time / (1000 * 60)) % 60) / 10;
        int secsUnit = (int) ((time / 1000) % 60) % 10;
        int secsTens = (int) ((time / 1000) % 60) / 10;
        int millisecs = (int) time % 100;
        return String.format("%d%d:%d%d:%02d", minsTens, minsUnit, secsTens, secsUnit, millisecs);
    }

    private int getXForCentredText(Graphics2D g, String text) {
        FontMetrics metrics = g.getFontMetrics();
        int w = metrics.stringWidth(text);
        return (gp.screenWidth - w)/2;
    }

    private void drawTitleScreen() {
        g2.setFont(titleFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Welcome to", getXForCentredText(g2, "Welcome to"), 80);
        g2.drawString("Feed the Sprite!", getXForCentredText(g2,"Feed the Sprite!"), 140);
        g2.setFont(pressStart);
        g2.drawString("Play Game", getXForCentredText(g2,"Play Game"), quitHeight - (lineSpace*4));
        g2.drawString("How to Play", getXForCentredText(g2,"How to Play"), quitHeight - (lineSpace*3));
        g2.drawString("Leaderboard", getXForCentredText(g2, "Leaderboard"), quitHeight - (lineSpace*2));
        g2.drawString("Settings", getXForCentredText(g2, "Settings"), quitHeight - lineSpace);
        g2.drawString("Quit", getXForCentredText(g2,"Quit"), quitHeight);


        // Changing color of text on hover
        g2.setColor(Color.RED);
        if (playGame.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
            g2.drawString("Play Game", getXForCentredText(g2,"Play Game"), quitHeight - (lineSpace*4));
            if (gp.mouseH.mouseClicked) {
                gp.gameState = gp.playingGame;
                if (areEffectsOn) {
                    gp.playEffect(2);
                }
                gp.mouseH.clearMouseClick();
            }
        }
        else if (howToPlay.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
            g2.drawString("How to Play", getXForCentredText(g2,"How to Play"), quitHeight - (lineSpace*3));
            if (gp.mouseH.mouseClicked) {
                gp.gameState = gp.howToPlayScreen;
                if (areEffectsOn) {
                    gp.playEffect(2);
                }
                gp.mouseH.clearMouseClick();
            }
        }
        else if (leaderboards.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
            g2.drawString("Leaderboard", getXForCentredText(g2, "Leaderboard"), quitHeight - (lineSpace*2));
            if (gp.mouseH.mouseClicked) {
                gp.gameState = gp.leaderboardScreen;
                if (areEffectsOn) {
                    gp.playEffect(2);
                }
                gp.mouseH.clearMouseClick();
            }
        }
        else if (settings.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
            g2.drawString("Settings", getXForCentredText(g2, "Settings"), quitHeight - (lineSpace));
            if (gp.mouseH.mouseClicked) {
                gp.gameState = gp.settingsScreen;
                if (areEffectsOn) {
                    gp.playEffect(2);
                }
                gp.mouseH.clearMouseClick();
            }
        }
        else if (quitGame.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
            g2.drawString("Quit", getXForCentredText(g2,"Quit"), quitHeight);
            if (gp.mouseH.mouseClicked) {
                gp.mouseH.clearMouseClick();
                if (areEffectsOn) {
                    gp.playEffect(2);
                }
                System.exit(0);
            }
        }
        else {
            if (gp.mouseH.mouseClicked) {
                gp.mouseH.clearMouseClick();
            }
        }
    }

    private void drawInstructionScreen() {
        g2.setFont(instructions);
        g2.setColor(Color.WHITE);
        g2.drawString("Feed the Sprite. I thought I said that.", 32,48);
        g2.drawString("Use WASD keys to move around. Try it!", 32, 48 + lineSpace);
        g2.drawString("Collect the snacks as fast as you can!", 32, 48 + (lineSpace*5));
        g2.drawString("A timer starts once you press play!", 32,48 + (lineSpace*6));
        g2.drawString("Avoid the walls; they'll slow you down!", 32, 48 + (lineSpace*7));
        g2.drawString("Hopefully that's all clear. Enjoy!", 32, 48 + (lineSpace*8));

        backToMainMenuButton();
    }

    private void drawEndingScreen() {
        g2.setFont(pressStart);
        g2.setColor(Color.WHITE);
        g2.drawString("Your time: " + formatTime(elapsedTime), getXForCentredText(g2, "Your time: " + formatTime(elapsedTime)), 100);
        if (formatTime(elapsedTime).equalsIgnoreCase(gp.storedTimes.getFirst().time())) {
            g2.setColor(Color.GREEN);
            g2.drawString("New record!", getXForCentredText(g2,"New Record!"), 160);
            g2.setColor(Color.WHITE);
        }
        else {
            g2.setColor(Color.YELLOW);
            g2.setFont(instructions);
            g2.drawString("Could've been faster", getXForCentredText(g2,"Could've been faster"), 160);
            g2.setColor(Color.WHITE);
            g2.setFont(pressStart);
        }
        g2.drawString("Leaderboard", getXForCentredText(g2, "Leaderboard"), quitHeight - (lineSpace*2));
        g2.drawString("Play Again", getXForCentredText(g2, "Play Again"), quitHeight - lineSpace);
        g2.drawString("Main Menu", getXForCentredText(g2, "Main Menu"), quitHeight);
        g2.setColor(Color.RED);
        if (leaderboards.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
            g2.drawString("Leaderboard", getXForCentredText(g2, "Leaderboard"), quitHeight - (lineSpace*2));
            if (gp.mouseH.mouseClicked) {
                gp.gameState = gp.leaderboardScreen;
                timeRecorded = false;
                reset = false;
                if (areEffectsOn) {
                    gp.playEffect(2);
                }
                gp.mouseH.clearMouseClick();
            }
        }
        else if (playAgain.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
            g2.drawString("Play Again", getXForCentredText(g2, "Play Again"), quitHeight - lineSpace);
            if (gp.mouseH.mouseClicked) {
                gp.mouseH.clearMouseClick();
                if (areEffectsOn) {
                    gp.playEffect(2);
                }
                gp.gameState = gp.playingGame;
                timeRecorded = false;
                reset = false;
            }
        }
        else if (backToMainMenu.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
            g2.drawString("Main Menu", getXForCentredText(g2, "Main Menu"), quitHeight);
            if (gp.mouseH.mouseClicked) {
                gp.mouseH.clearMouseClick();
                if (areEffectsOn) {
                    gp.playEffect(2);
                }
                gp.gameState = gp.titleScreen;
                timeRecorded = false;
                reset = false;
            }
        }
        else {
            if (gp.mouseH.mouseClicked) {
                gp.mouseH.clearMouseClick();
            }
        }
    }

    private void drawLeaderboardScreen() {
        g2.setFont(pressStart);
        g2.setColor(Color.WHITE);
        g2.drawString("Best Times:", getXForCentredText(g2,"Best Times:"),100);
        g2.setFont(instructions);
        if (gp.storedTimes.isEmpty()) {
            g2.setColor(Color.YELLOW);
            g2.drawString("No records available", getXForCentredText(g2,"No records available"), 160);
        }
        else {
            g2.drawString("Clear Records", 48, 460);
            if (clearRecords.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
                g2.setColor(Color.RED);
                g2.drawString("Clear Records", 48, 460);
                if (gp.mouseH.mouseClicked) {
                    gp.gameState = gp.areYouSureScreen;
                    if (areEffectsOn) {
                        gp.playEffect(2);
                    }
                    gp.mouseH.clearMouseClick();
                }
                g2.setColor(Color.WHITE);
            }
            if (gp.storedTimes.size() < 5) {
                for (Time x : gp.storedTimes) {
                    String time = x.time();
                    g2.drawString(time, getXForCentredText(g2,time), 100 + (lineSpace * x.id()));
                }
            }
            else {
                for (int i = 0; i < 5; i++) {
                    Time x = gp.storedTimes.get(i);
                    String time = x.time();
                    g2.drawString(time, getXForCentredText(g2,time), 100 + (lineSpace * x.id()));
                }
            }
        }
        backToMainMenuButton();
    }

    private void drawSettingsScreen() {
        g2.setFont(pressStart);
        g2.setColor(Color.WHITE);
        g2.drawString("Music: ", getXForCentredText(g2,"Music: "), 100);
        g2.drawString("Sound FX: ", getXForCentredText(g2,"Sound FX: "), 100 + lineSpace);
        g2.drawString("Show Timer: ", getXForCentredText(g2, "Show Timer: "), 100 + (lineSpace*2));
        g2.drawString("Show Coins: ", getXForCentredText(g2,"Show Coins: "), 100 + (lineSpace*3));

        if (isMusicOn) {
            g2.drawString("ON", 464, 100);
            if (musicOn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
                g2.setColor(Color.GREEN);
                g2.drawString("ON", 464, 100);
                g2.setColor(Color.WHITE);
                if (gp.mouseH.mouseClicked) {
                    gp.stopMusic();
                    isMusicOn = false;
                    if (areEffectsOn) {
                        gp.playEffect(2);
                    }
                    gp.mouseH.clearMouseClick();
                }
            }
        }
        else {
            g2.drawString("OFF", 464, 100);
            if (musicOff.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
                g2.setColor(Color.RED);
                g2.drawString("OFF", 464, 100);
                g2.setColor(Color.WHITE);
                if (gp.mouseH.mouseClicked) {
                    gp.playMusic();
                    isMusicOn = true;
                    if (areEffectsOn) {
                        gp.playEffect(2);
                    }
                    gp.mouseH.clearMouseClick();
                }
            }
        }

        if (areEffectsOn) {
            g2.drawString("ON", 512, 100 + lineSpace);
            if (effectsOn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
                g2.setColor(Color.GREEN);
                g2.drawString("ON", 512, 100 + lineSpace);
                g2.setColor(Color.WHITE);
                if (gp.mouseH.mouseClicked) {
                    areEffectsOn = false;
                    gp.mouseH.clearMouseClick();
                }
            }
        }
        else {
            g2.drawString("OFF", 512, 100 + lineSpace);
            if (effectsOff.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
                g2.setColor(Color.RED);
                g2.drawString("OFF", 512, 100 + lineSpace);
                g2.setColor(Color.WHITE);
                if (gp.mouseH.mouseClicked) {
                    areEffectsOn = true;
                    gp.playEffect(2);
                    gp.mouseH.clearMouseClick();
                }
            }
        }

        if (isTimerOn) {
            g2.drawString("ON", 544, 100 + (lineSpace*2));
            if (timerOn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
                g2.setColor(Color.GREEN);
                g2.drawString("ON", 544, 100 + (lineSpace*2));
                g2.setColor(Color.WHITE);
                if (gp.mouseH.mouseClicked) {
                    isTimerOn = false;
                    if (areEffectsOn) {
                        gp.playEffect(2);
                    }
                    gp.mouseH.clearMouseClick();
                }
            }
        }
        else {
            g2.drawString("OFF", 544, 100 + (lineSpace*2));
            if (timerOff.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
                g2.setColor(Color.RED);
                g2.drawString("OFF", 544, 100 + (lineSpace*2));
                g2.setColor(Color.WHITE);
                if (gp.mouseH.mouseClicked) {
                    isTimerOn = true;
                    if (areEffectsOn) {
                        gp.playEffect(2);
                    }
                    gp.mouseH.clearMouseClick();
                }
            }
        }

        if (areCoinsOn) {
            g2.drawString("ON", 544, 100 + (lineSpace*3));
            if (coinsOn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
                g2.setColor(Color.GREEN);
                g2.drawString("ON", 544, 100 + (lineSpace*3));
                g2.setColor(Color.WHITE);
                if (gp.mouseH.mouseClicked) {
                    areCoinsOn = false;
                    if (areEffectsOn) {
                        gp.playEffect(2);
                    }
                    gp.mouseH.clearMouseClick();
                }
            }
        }
        else {
            g2.drawString("OFF", 544, 100 + (lineSpace*3));
            if (coinsOff.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
                g2.setColor(Color.RED);
                g2.drawString("OFF", 544, 100 + (lineSpace*3));
                g2.setColor(Color.WHITE);
                if (gp.mouseH.mouseClicked) {
                    areCoinsOn = true;
                    if (areEffectsOn) {
                        gp.playEffect(2);
                    }
                    gp.mouseH.clearMouseClick();
                }
            }
        }

        backToMainMenuButton();
    }

    private void drawAreYouSureScreen() {
        g2.setFont(pressStart);
        g2.setColor(Color.WHITE);
        g2.drawString("Are you sure you want", getXForCentredText(g2,"Are you sure you want"),100);
        g2.drawString("to clear your records?", getXForCentredText(g2, "to clear your records?"), 100 + lineSpace);
        g2.drawString("YES", 192, 100 + (lineSpace*3));
        g2.drawString("NO", 512, 100 + (lineSpace*3));
        if (yesClear.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
            g2.setColor(Color.GREEN);
            g2.drawString("YES", 192, 100 + (lineSpace*3));
            if (gp.mouseH.mouseClicked) {
                gp.clearTimes();
                gp.gameState = gp.leaderboardScreen;
                if (areEffectsOn) {
                    gp.playEffect(2);
                }
                gp.mouseH.clearMouseClick();
            }
        }
        else if (noClear.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
            g2.setColor(Color.RED);
            g2.drawString("NO", 512, 100 + (lineSpace*3));
            if (gp.mouseH.mouseClicked) {
                gp.gameState = gp.leaderboardScreen;
                if (areEffectsOn) {
                    gp.playEffect(2);
                }
                gp.mouseH.clearMouseClick();
            }
        }
        else {
            if (gp.mouseH.mouseClicked) {
                gp.mouseH.clearMouseClick();
            }
        }

    }

    private void backToMainMenuButton() {
        g2.setFont(pressStart);
        g2.setColor(Color.WHITE);
        g2.drawString("Main Menu", getXForCentredText(g2, "Main Menu"), 530);
        if (backToMainMenu.contains(gp.mouseH.mouseX, gp.mouseH.mouseY)) {
            g2.setColor(Color.RED);
            g2.drawString("Main Menu", getXForCentredText(g2, "Main Menu"), 530);
            if (gp.mouseH.mouseClicked) {
                gp.gameState = gp.titleScreen;
                if (areEffectsOn) {
                    gp.playEffect(2);
                }
                gp.mouseH.clearMouseClick();
            }
        }
        else {
            if (gp.mouseH.mouseClicked) {
                gp.mouseH.clearMouseClick();
            }
        }
    }
}
