package edu.moravian.csci299.DungeonDomination;

import java.util.List;
import java.util.LinkedList;
import java.util.Random;

import android.graphics.PointF;

/**
 * The Game keeps track of the player and multiple Rooms (pre-populated areas of enemies)
 */
public class Game {

    /** The amount to move the player by when moving out of bounds */
    private static final float EDGE_OFFSET = 0.0001f;

    /** The one player in the game */
    private Player player;

    /** A game is represented of multiple pre-populated rooms */
    private final LinkedList<Room> rooms;

    /** The room that is currently being played */
    private Room currentRoom;

    /** The width and height of the game, in px */
    private int width, height;

    /** Converts dp to px */
    public static final float DP_TO_PX_FACTOR = 1f;
    
    /** Coins collected */
    private int collectedCoins = 0;
    
    /** Status of game */
    private boolean gameHasStarted = false, isGameOver = false, hasWon = false;
    
    /** Random */
    private final Random random;
    
    /** Constructor for a new game */
    public Game() {
        random = new Random();
        rooms = new LinkedList<>();
    }

    /**
     * Returns status of if game has started yet or not.
     * @return true if not started, otherwise false if started
     */
    public boolean hasNotStarted() { return !gameHasStarted; }

    /**
     * Returns if game is over (either player died or won)
     * @return true if game is over, otherwise false
     */
    public boolean isGameOver() { return this.isGameOver; }

    /**
     * Returns if the player has won by defeating the final boss or not
     * @return true if player won, otherwise false
     */
    public boolean hasPlayerWon() { return this.hasWon; }

    /**
     * Get the total number of collected coins this game
     * @return integer representing number of coins collected this game
     */
    public int getCurrentCollectedCoins() { return this.collectedCoins; }

    /**
     * Start the game.
     * @param width the width of the playing area in px
     * @param height the height of the playing area in px
     * @param weaponDamage The amount of damage the player deals
     * @param multiplier The amount to multiply every enemies base health by
     */
    public void startGame(int width, int height, int weaponDamage, int multiplier) {
        this.width = width;
        this.height = height;
        currentRoom = new Room(false, width, height, multiplier);
        int numRooms = random.nextInt(10) + 10; // random number of rooms within range
        for (int i = 0; i < numRooms - 2; i++) {
             rooms.add(new Room(false, width, height, multiplier));
        }
        rooms.add(new Room(true, width, height, multiplier));  // boss room
        this.gameHasStarted = true; 
        player = new Player(new PointF(width/2f, height/2f), new Weapon(weaponDamage), 100);
    }   

    /** Get the player in the game */
    public Player getPlayer() { return this.player; }

    /** Get the list of the players bullets from the current room */
    public List<Bullet> getPlayerBullets() { return this.currentRoom.getPlayerBullets(); }

    /** Get the list of all enemies bullets from the current room */
    public List<Bullet> getEnemyBullets() { return this.currentRoom.getEnemyBullets(); }
    
    /** Get the enemies from the current room */
    public List<Enemy> getEnemies() { return this.currentRoom.getEnemies(); }

    /**
     * Sets the direction that the player will move in the future.
     * @param angle the new direction of the player, in radians
     */
    public void setMovementDirection(double angle) { if (this.gameHasStarted) player.setDirection(angle); }

    /** Make the current room the next room in the list of rooms */
    private void unlockRoom() { this.currentRoom = rooms.pop(); }

    /**
     * Update the game. This moves the player, all enemies, and all bullets.
     * Updates collected coins from the dead enemies.
     * Keeps the playe within bounds until all enemies are killed.
     * Enemies constantly move toward the player and can shoot the player.
     * @return true if the game is still going, false if the game is over
     */
    public boolean update() {
        if (this.isGameOver) { return false; }
        PointF currentLocation = player.getLocation();
        boolean isOutOfBounds = player.isOutOfBounds(this.width, this.height);
        int numOfEnemies = currentRoom.getNumEnemies();
        if (isOutOfBounds && numOfEnemies == 0) {  // Move to next room if there is one
            if (rooms.size() == 0) { // if this was the last room
                this.isGameOver = true;
                this.hasWon = true;
                return false; 
            }
            unlockRoom();
            PointF newLocation = checkBounds(
                currentLocation,
                this.width - Player.BODY_PIECE_SIZE_DP - EDGE_OFFSET,
                Player.BODY_PIECE_SIZE_DP + EDGE_OFFSET,
                this.height - Player.BODY_PIECE_SIZE_DP - EDGE_OFFSET,
                Player.BODY_PIECE_SIZE_DP + EDGE_OFFSET
            ); // Move player to opposite side
            player.setLocation(newLocation);
        } else if (isOutOfBounds && numOfEnemies > 0) {  // Keep player in bounds
            PointF newLocation = checkBounds(
                currentLocation,
                Player.BODY_PIECE_SIZE_DP + EDGE_OFFSET,
                this.width - Player.BODY_PIECE_SIZE_DP - EDGE_OFFSET,
                Player.BODY_PIECE_SIZE_DP + EDGE_OFFSET,
                this.height - Player.BODY_PIECE_SIZE_DP - EDGE_OFFSET
            ); // Move player just inside bounds
            player.setLocation(newLocation);
        } else {
            player.move();
        }

        // move enemies and all bullets, and remove enemies if defeated
        currentRoom.moveEnemies(player);
        collectedCoins += currentRoom.removeEnemies(); // collect coins from defeated enemies
        currentRoom.moveEnemyBullets(player);
        currentRoom.movePlayerBullets();

        // If player intersected any enemy, game is over
        if (player.intersectsEnemy(currentRoom.getEnemies()) || player.getHealth() <= 0) {
            player.setHealth(0);
            this.isGameOver = true;
        }
        return true;
    }

    /**
     * Spawn a bullet from player to touched point
     * @param point The point to fire a bullet at
     * @return true all the time
     */
    public boolean touched(PointF point) {
        getPlayerBullets().add(currentRoom.spawnBullet(point, player));
        return true;
    }

    /**
     * Get the new location of the player and make sure it is inside bounds
     * @param currentLocation The location of the player
     * @param left The left coordinate to set the player's x value to
     * @param right The right coordinate to set the player's x value to
     * @param top The top coordinate to set the player's y value to
     * @param bottom The bottom coordinate to set the player's y value to
     * @return The new location for the player
     */
    private PointF checkBounds(PointF currentLocation, float left, float right, float top, float bottom) {
        PointF newLocation = new PointF(currentLocation.x, currentLocation.y);
        double radius = player.getRadius();

        // keep player within left and right bounds
        if (currentLocation.x < radius) {
            newLocation.x = left;
        } else if (currentLocation.x >= this.width - radius) {
            newLocation.x = right;
        }

        // keep player within top and bottom bounds
       if (currentLocation.y < radius) {
           newLocation.y = top;
       } else if (currentLocation.y >= this.height - radius) {
           newLocation.y = bottom;
       }
        return newLocation;
    }
}
