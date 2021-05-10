package edu.moravian.csci299.DungeonDomination;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.ArrayList;

import android.graphics.PointF;

/**
 * The game is made up of multiple Rooms. 
 * A Room contains a list of enemies, enemy bullets, and player bullets
 * A Room can also be boss room consisting of one large enemy
 */
public class Room {

    /** Lists of enemies and enemy bullets and player bullets */
    private final List<Enemy> enemies;
    private final List<Bullet> enemyBullets;
    private final List<Bullet> playerBullets;
    private final Random random;
    private final boolean isBoss;

    /**
     * A room is either a boss room or not, has a width and a height,
     * and all enemies have their health multiplied by the multiplier.
     * @param isBoss Is this a boss room
     * @param width The width of the room
     * @param height The height of the room
     * @param multiplier The base health multiplier for harder enemies
     */
    public Room(boolean isBoss, int width, int height, int multiplier) {
        this.isBoss = isBoss;
        random = new Random();
        enemies = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        playerBullets = new ArrayList<>();
        if (isBoss) { // if this room is a boss room
            enemies.add(new Enemy(new PointF(random.nextInt(width), random.nextInt(height)), 
                new Weapon(20), 500*multiplier, 80f, 1f, 25*multiplier));
        } else { // this is a regular room
            int numEnemies = random.nextInt(3) + 1;
            for (int i = 0; i < numEnemies; i++) {
                enemies.add(new Enemy(new PointF(random.nextInt(width),
                        random.nextInt(height)), new Weapon(5), 100*multiplier, 30f, 2.5f, multiplier));
            }
        }
    }

    /**
     * Get list of enemies in the room
     * @return list of Enemies
     */
    public List<Enemy> getEnemies() { return this.enemies; }

    /**
     * Get number of enemies in the room
     * @return integer representing enemy count in room
     */
    public int getNumEnemies() { return this.enemies.size(); }

    /**
     * Get list of the player's bullets
     * @return list of Bullets
     */
    public List<Bullet> getPlayerBullets() { return this.playerBullets; }

    /**
     * Get list of the enemy's bullets
     * @return list of Bullets
     */
    public List<Bullet> getEnemyBullets() { return this.enemyBullets; }

    /**
     * Remove all enemies that have 0 or less health and collect their coins
     * @return Amount of coins the player has earned
     */
    public int removeEnemies() {
        int coinsEarned = 0;
        ListIterator<Enemy> iter = enemies.listIterator();
        while(iter.hasNext()) {
            Enemy enemy = iter.next();
            if (enemy.getHealth() <= 0) {
                coinsEarned +=  enemy.getCoinValue();
                iter.remove();
            }
        }
        return coinsEarned;
    }

    /**
     * Move all enemies towards the player and give them a small chance of firing a bullet
     * @param player The player to move towards
     */
    public void moveEnemies(Player player) {
        PointF currentLocation = player.getLocation();
        for (Enemy enemy : getEnemies()) {
            PointF location = enemy.getLocation();
            double direction = Math.atan2(currentLocation.y - location.y, currentLocation.x - location.x);
            enemy.setDirection(direction);
            enemy.move();
            if (Math.random() <= 0.01) {
            enemyBullets.add(spawnBullet(currentLocation, enemy));
            }
        }
    }

    /**
     * Move the enemy bullets. Remove them if they hit the player and decrease the player's health
     * @param player The player being shot at
     */
    public void moveEnemyBullets(Player player) {
        for (Bullet bullet : getEnemyBullets()) {
            bullet.move();
            if (bullet.hitPlayer(player)) {
            bullet.setHit(true);
                player.decreaseHealth(bullet.getDamage());
            }
        }
        removeEnemyBullets();
    }

    /**
     * Move the player's bullets. Remove them if they hit an enemy and decrease enemies health
     */
    public void movePlayerBullets() {
        for (Bullet bullet : getPlayerBullets()) {
                bullet.move();
            Enemy enemyHit = bullet.intersectsEnemy(getEnemies());
            if (enemyHit != null) {
                bullet.setHit(true);
                enemyHit.decreaseHealth(bullet.getDamage());
            }
        }
        removePlayerBullets();
    }

    /**
     * Spawn a bullet with correct direction, damage, and speed based on the character
     * @param dest The destination point being fired at
     * @param character The character doing the firing. Either the player or the enemy
     * @return A bullet set in the right direction and at the right place with correct damage
     */
    public Bullet spawnBullet(PointF dest, Character character) {
        PointF location = character.getLocation();
        Bullet bullet = new Bullet(new PointF(location.x, location.y), character.getWeapon(), character.getBulletSpeed());
        bullet.setDirection(Math.atan2(dest.y - location.y, dest.x - location.x));
        return bullet;
    }

    /** Remove player bullets if they hit the enemy */
    public void removePlayerBullets() { playerBullets.removeIf(Bullet::getHit); }

    /** Remove enemy bullets if they hit the player*/
    public void removeEnemyBullets() { enemyBullets.removeIf(Bullet::getHit); }

}
