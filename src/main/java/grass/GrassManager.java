package grass;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GrassManager {
    GamePanel gp;
    public Grass[] grass;
    public int[][] mapGrassNum;
    public int mapLevel;

    public GrassManager(GamePanel gp) {
        this.gp = gp;
        grass = new Grass[2];
        mapGrassNum = new int[gp.maxScreenRow][gp.maxScreenCol];
        getGrassImage();
        mapLevel = 1;
        loadMap("/maps/gameMap1.txt");
    }

    // Load grass images
    private void getGrassImage() {
        try {
            grass[0] = new Grass();
            grass[1] = new Grass();
            grass[0].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/grass.png")));
            grass[1].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/border.png")));
            grass[1].collision = true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Load map from stored text files
    public void loadMap(String filename) {
        try {
            InputStream inputStream = Objects.requireNonNull(getClass().getResourceAsStream(filename));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            int row = 0;

            while (row < gp.maxScreenRow) {
                String line = bufferedReader.readLine();
                mapGrassNum[row] = Arrays.stream(line.split(" "))
                        .mapToInt(Integer::parseInt)
                        .toArray();
                row++;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Draw
    public void draw(Graphics2D g2) {
        for (int j = 0; j < gp.maxScreenRow; j++) {
            for (int i = 0; i < gp.maxScreenCol; i++) {
                int num = mapGrassNum[j][i];
                g2.drawImage(grass[num].image, i * gp.tileSize, j * gp.tileSize, gp.tileSize, gp.tileSize, null);
            }
        }
    }

    // Get the tiles with grass on them
    public List<List<Integer>> getGrassCoordinates() {
        List<List<Integer>> listOfCoordinates = new ArrayList<>();
        for (int i = 0; i < mapGrassNum.length - 2; i++) {
            for (int j = 0; j < mapGrassNum[0].length - 2; j++) {
                List<Integer> theList = new ArrayList<>();
                if (mapGrassNum[i+1][j+1] == 0) {
                    theList.add(i+1);
                    theList.add(j+1);
                    listOfCoordinates.add(theList);
                }
            }
        }
        return listOfCoordinates;
    }
}
