package object;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Snack {
    public BufferedImage image;
    public int worldX, worldY;
    public Rectangle objectArea;
    public boolean collected;

    public Snack(String imgPath) {
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imgPath)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
