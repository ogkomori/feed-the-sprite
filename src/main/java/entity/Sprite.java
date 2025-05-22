package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class Sprite {
    public int x,y;
    public final int speed = 4;

    // Pictures of sprite motion
    private BufferedImage up1, down1, up2, down2;
    public String direction = "";

    private int spriteNumber = 1;
    private int spriteCounter = 0;

    // Region of the sprite considered solid
    public final Rectangle solidArea = new Rectangle(8, 16, 32, 24);
    public boolean collisionOn = false;

    // Area relative to the map
    public Rectangle worldSolidArea;

    GamePanel gp;
    KeyHandler kh;
    public int coinCount;

    public Sprite(GamePanel gp, KeyHandler kh) {
        this.gp = gp;
        this.kh = kh;
        x = 362;
        y = 188;
        getPlayerImage();
        coinCount = 0;
        worldSolidArea = new Rectangle(x + solidArea.x, y + solidArea.y, solidArea.width, solidArea.height);
    }

    // Sprite gets sent to a random corner each level
    public void sendToRandomCorner() {
        int[] rows = new int[]{1,10};
        int[] cols = new int[]{1,14};
        Random random = new Random();
        int rowIn = random.nextInt(2);
        int colIn = random.nextInt(2);
        x = cols[colIn] * gp.tileSize;
        y = rows[rowIn] * gp.tileSize;
    }

    // Load sprite images
    private void getPlayerImage() {
        try {
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/firstSprite.png")));
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/secondSprite.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/firstSpriteA.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/secondSpriteA.png")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Update position of the sprite
    public void update() {
        if (kh.upPressed) {
            direction = "up";
        }
        else if (kh.downPressed) {
            direction = "down";
        }
        else if (kh.leftPressed) {
            direction = "left";
        }
        else if (kh.rightPressed) {
            direction = "right";
        }

        // For the collision checker
        collisionOn = false;
        gp.checker.checkTile(this);
        if (!collisionOn) {
            if (kh.upPressed) {
                y -= speed;
            }
            else if (kh.downPressed) {
                y += speed;
            }
            else if (kh.leftPressed) {
                x -= speed;
            }
            else if (kh.rightPressed) {
                x += speed;
            }
        }


        // For the walking animation
        spriteCounter++;
        if(spriteCounter > 10) {
            if (spriteNumber == 1) {
                spriteNumber = 2;
            }
            else if (spriteNumber == 2) {
                spriteNumber = 1;
            }
            spriteCounter = 0;
        }

        // Update world solid area
        worldSolidArea.x = x + solidArea.x;
        worldSolidArea.y = y + solidArea.y;
    }

    // Draw the sprite
    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        if (direction.equalsIgnoreCase("up")) {
            if(spriteNumber == 1) {
                image = up1;
            }
            if(spriteNumber == 2) {
                image = up2;
            }
        }
        else {
            if(spriteNumber == 1) {
                image = down1;
            }
            if(spriteNumber == 2) {
                image = down2;
            }
        }
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }
}
