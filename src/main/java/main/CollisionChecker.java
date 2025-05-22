package main;

import entity.Sprite;

// Class to check for collision with other objects in the map
public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Sprite entity) {
        // Obtain the sprite's solidArea in the MAP's coordinates
        int leftX = entity.x + entity.solidArea.x;
        int rightX = leftX + entity.solidArea.width;
        int topY = entity.y + entity.solidArea.y;
        int bottomY = topY + entity.solidArea.height;

        /*
         So this is now to check the rows/columns, and determine if
         the player's solidArea is in the same row/column as the
         block/tile
        */
        int leftCol = leftX/gp.tileSize;
        int rightCol = rightX/gp.tileSize;
        int topRow = topY/gp.tileSize;
        int bottomRow = bottomY/gp.tileSize;

        // You can walk into 2 tiles at once, so we check each of the two tiles
        int tileNum1 = -1;
        int tileNum2 = -1;

        /*
         Now we apply conditions to know which coordinates to
         check depending on which direction we are facing
        */
        switch (entity.direction) {
            case "up":
                // Facing upwards, we check the tile in front to our left, and that of our right
                // Update the row to reflect where we would be after moving
                topRow = topRow - entity.speed/gp.tileSize;
                tileNum1 = gp.grassM.mapGrassNum[topRow][leftCol]; // For the left tile
                tileNum2 = gp.grassM.mapGrassNum[topRow][rightCol]; // For the right tile
                break;
            case "down":
                bottomRow = bottomRow + entity.speed/gp.tileSize;
                tileNum1 = gp.grassM.mapGrassNum[bottomRow][leftCol];
                tileNum2 = gp.grassM.mapGrassNum[bottomRow][rightCol];
                break;
            case "left":
                leftCol = leftCol - entity.speed/gp.tileSize;
                tileNum1 = gp.grassM.mapGrassNum[bottomRow][leftCol];
                tileNum2 = gp.grassM.mapGrassNum[topRow][leftCol];
                break;
            case "right":
                rightCol = rightCol + entity.speed/gp.tileSize;
                tileNum1 = gp.grassM.mapGrassNum[bottomRow][rightCol];
                tileNum2 = gp.grassM.mapGrassNum[topRow][rightCol];
                break;
        }

        // And so if the tile has collision set to true, we don't update the player's position.
        // Else, we update normally. We know the stone has tileNum = 1, with collision on.
        if (tileNum1 > 0 || tileNum2 > 0) {
            if (gp.grassM.grass[tileNum1].collision || gp.grassM.grass[tileNum2].collision) {
                entity.collisionOn = true;
            }
        }
    }
}
