package main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.Sprite;
import grass.GrassManager;
import object.Snack;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;

// Main GamePanel
public class GamePanel extends JPanel implements Runnable {
    // Screen Settings
    private final int originalTileSize = 16; // 16x16 tile
    private final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    // No. of rows and columns in the main frame
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    // Calculate the screen width
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    // List of Maps
    private final Integer[] mapOrder = new Integer[]{2,3,4,5,6,7,8,9,10};
    {Collections.shuffle(Arrays.asList(mapOrder));}

    // Game States
    final int playingGame = 0;
    final int titleScreen = 1;
    final int endingScreen = 2;
    final int howToPlayScreen = 3;
    final int leaderboardScreen = 4;
    final int settingsScreen = 5;
    final int areYouSureScreen = 6;
    int gameState = titleScreen;

    // Initialize all helper functions and classes
    GrassManager grassM = new GrassManager(this);
    KeyHandler keyH = new KeyHandler();
    Sprite sprite = new Sprite(this, keyH);
    Snack[] snacks = new Snack[10];
    SnackManager snackManager = new SnackManager(this);
    public CollisionChecker checker = new CollisionChecker(this);
    SoundManager music = new SoundManager();
    SoundManager effects = new SoundManager();
    UI gameUI = new UI(this);
    MouseHandler mouseH = new MouseHandler();
    List<Time> storedTimes = new ArrayList<>();
    {loadTimes();}

    // Constructor for Main
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        // For better rendering performance
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseH);
        this.setFocusable(true); // Focusable means it's like the active window
        this.requestFocusInWindow();
    }

    // Reset all timers and coins and whatever
    void resetAttributes() {
        // Called in constructors so can be reset here
        Collections.shuffle(Arrays.asList(mapOrder));
        gameUI.setStartTime = false;
        grassM.mapLevel = 1;
        grassM.loadMap("/maps/gameMap1.txt");
        snackManager.setObjects();
        snackManager.updateSnackPositions();
        sprite = new Sprite(this, keyH);
    }

    // Game thread
    Thread gameThread;
    void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        // SLEEP METHOD GAME LOOP
        // FPS:
        int FPS = 60;
        double drawInterval = (double) 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        while (gameThread != null) {
            // 1. UPDATE: update information such as character position
            update();

            // 2. DRAW: draw the screen with the updated information
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime/1000000;
                if (remainingTime < 0) {
                    remainingTime = 0;
                }
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }


    }

    // Update and Repaint
    private void update() {
        // To show changes in the sprite's position
        if (gameState == playingGame || gameState == titleScreen || gameState == howToPlayScreen) {
            sprite.update();
        }
        if (gameState == playingGame) {
            for (int i = 1; i <= 9; i++) {
                if (sprite.coinCount == (i * 10) && grassM.mapLevel != (i + 1)) {
                    loadNextMap();
                }
            }
        }

        // Show ending screen once game ends
        if (sprite.coinCount == 100 && gameState != endingScreen) {
            gameState = endingScreen;
        }
    }

    // For drawing sprites and other stuff
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (gameState == playingGame) {
            grassM.draw(g2);
            snackManager.draw(g2);
        }
        gameUI.draw(g2);
        if (gameState == playingGame || gameState == titleScreen || gameState == howToPlayScreen) {
            sprite.draw(g2);
        }
        g2.dispose();
    }

    // Music
    void playMusic() {
        music.setFile(0);
        music.play();
        music.loop();
    }

    void stopMusic() {
        music.stop();
    }

    void playEffect(int i) {
        effects.setFile(i);
        effects.play();
    }

    private void loadNextMap() {
        grassM.loadMap("/maps/gameMap" + mapOrder[grassM.mapLevel - 1] + ".txt");
        snackManager.setObjects();
        snackManager.updateSnackPositions();
        grassM.mapLevel++;
        sprite.sendToRandomCorner();
    }


    // Leaderboard Logic
    private void loadTimes() {
        try {
            File file = new File("./leaderboard/times.json");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            // Get the times stored in the JSON file
            Gson gson = new Gson();
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String jsonString = bufferedReader.readLine();
            if (jsonString != null) {
                Type listTime = new TypeToken<List<Time>>(){}.getType();
                storedTimes = gson.fromJson(jsonString, listTime);
            }
            inputStream.close();

            // Sort the times
            sortTimes();
            updateTimes();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void updateTimes() {
        try {
            sortTimes();
            File file = new File("./leaderboard/times.json");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            Gson gson = new Gson();
            FileWriter writer = new FileWriter(file, false);
            writer.write(gson.toJson(storedTimes));
            writer.close();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void clearTimes() {
        storedTimes = new ArrayList<>();
        try {
            File file = new File("./leaderboard/times.json");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, false);
            writer.write("");
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void sortTimes() {
        List<String> timesToBeSorted = new ArrayList<>();
        for (Time x : storedTimes) {
            timesToBeSorted.add(x.time());
        }
        Collections.sort(timesToBeSorted);
        clearTimes();
        for (int i = 0; i < timesToBeSorted.size(); i++) {
            storedTimes.add(new Time(i + 1, timesToBeSorted.get(i)));
        }
    }
}
