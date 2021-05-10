package edu.moravian.csci299.DungeonDomination;

import android.graphics.PointF;
import static edu.moravian.csci299.DungeonDomination.Util.withinRange;
import java.util.List;

/**
 * A Bullet is a smaller sphere starting from the character that fired it to the designated position.
 * Bullets travel faster than characters
 */
public class Bullet extends Character {

    /** Size and step distance of a bullet*/
    public final static float STEP_DISTANCE_DP = 1f;
    public static final float RADIUS = 20f;
    
    /** If a bullet hit a character */
    private boolean hit;
    
    /**
     * Bullet is a character with 0 health and 0 bullet speed
     * @param location The location of the bullet
     * @param weapon How much damage the bullet does
     * @param speed How quick the bullet is moving
     */
    public Bullet(PointF location, Weapon weapon, double speed) {
        super(location, weapon, 0, RADIUS, STEP_DISTANCE_DP, speed, 0);
        hit = false;
    }

    /**
     * Get boolean for whether or not bullet hit a character
     * @return true if bullet hit a character, otherwise false
     */
    public boolean getHit() { return this.hit; }

    /**
     * Set boolean for whether or not bullet hit a character
     * @param hit true if bullet hit a character, otherwise false
     */
    public void setHit(boolean hit) { this.hit = hit; }

    /**
     * Get the enemy that the bullet hits
     * @param enemies List of all enemies in a room
     */
    public Enemy intersectsEnemy(List<Enemy> enemies) {
        for (Enemy enemy: enemies) {
            if (withinRange(this.location, enemy.getLocation(), (RADIUS + enemy.getRadius()) * Game.DP_TO_PX_FACTOR)) {
                return enemy;
            }
        }
        return null; // No enemy found
    }

    /**
     * Has the bullet hit the player
     * @param player The player to compare positions with
     */
    public boolean hitPlayer(Player player) {
        return withinRange(this.location, player.getLocation(), (RADIUS + player.getRadius()) * Game.DP_TO_PX_FACTOR);
    }

    /** Get the damage of the bullet */
    public int getDamage() { return this.getWeapon().getDamage(); }
}

