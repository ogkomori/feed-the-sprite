package main;

import object.Snack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SnackManager {
    GamePanel gp;
    int[] rowNums, colNums;

    public SnackManager(GamePanel gp) {
        this.gp = gp;
        setObjects();
        updateSnackPositions();
    }

    // Load snack images
    void setObjects() {
        gp.snacks[0] = new Snack("/img/snack/Burger.png");
        gp.snacks[1] = new Snack("/img/snack/Cake.png");
        gp.snacks[2] = new Snack("/img/snack/Candy.png");
        gp.snacks[3] = new Snack("/img/snack/Cookie.png");
        gp.snacks[4] = new Snack("/img/snack/Donut.png");
        gp.snacks[5] = new Snack("/img/snack/Drink.png");
        gp.snacks[6] = new Snack("/img/snack/Fries.png");
        gp.snacks[7] = new Snack("/img/snack/IceCream.png");
        gp.snacks[8] = new Snack("/img/snack/Lolly.png");
        gp.snacks[9] = new Snack("/img/snack/Pizza.png");
        rowNums = new int[10];
        colNums = new int[10];
    }

    void draw(Graphics2D g2) {
        // Change snack to grass in case it has been eaten
        for (int i = 0; i < rowNums.length; i++) {
            if (gp.sprite.worldSolidArea.intersects(gp.snacks[i].objectArea) && !gp.snacks[i].collected) {
                try {
                    if (gp.gameUI.areEffectsOn) {
                        gp.playEffect(1);
                    }
                    gp.snacks[i].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/grass.png")));
                    gp.snacks[i].collected = true;
                    gp.sprite.coinCount++;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        // Draw based on the above
        for (int i = 0; i < rowNums.length; i++) {
            g2.drawImage(gp.snacks[i].image, gp.snacks[i].worldX, gp.snacks[i].worldY, gp.tileSize, gp.tileSize, null);
        }
    }

    // Set the position of snacks randomly for each level
    void updateSnackPositions() {
        List<List<Integer>> spaces = gp.grassM.getGrassCoordinates();
        Set<Integer> uniqueSet = new HashSet<>();
        Random random = new Random();
        int count = 0;
        while (uniqueSet.size() < 10) {
            int index = random.nextInt(spaces.size());
            if (uniqueSet.add(index)) {
                rowNums[count] = spaces.get(index).getFirst();
                colNums[count] = spaces.get(index).getLast();
                count++;
            }
        }

        // Set the areas to check for collisions
        for (int i = 0; i < gp.snacks.length; i++) {
            gp.snacks[i].worldX = gp.tileSize * colNums[i];
            gp.snacks[i].worldY = gp.tileSize * rowNums[i];
            gp.snacks[i].objectArea = new Rectangle(gp.snacks[i].worldX, gp.snacks[i].worldY, gp.tileSize, gp.tileSize);
        }
    }
}
