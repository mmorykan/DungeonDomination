package edu.moravian.csci299.DungeonDomination;

import android.graphics.PointF;
import java.util.List;
import static edu.moravian.csci299.DungeonDomination.Util.withinRange;

/**
 * There is one player in the game. It shoots bullets anywhere the user touches.
 * A player collects coins as it kills enemies
 */
public class Player extends Character {

    public final static float BODY_PIECE_SIZE_DP = 35f; // radius of the player
    public final static float STEP_DISTANCE_DP = 2.5f; // distance, in dp, moved each movement

    /** Speed of the player, in dp/frame and bullet speed */
    private static final double SPEED = 6.0;
    private static final double BULLET_SPEED = 40.0;

    /**
     * Create the player with the given initial position.
     * @param location the initial position
     * @param weapon player's current weapon
     * @param health player's starting health
     */
    public Player(PointF location, Weapon weapon, int health) {
        super(location, weapon, health, BODY_PIECE_SIZE_DP, STEP_DISTANCE_DP, SPEED, BULLET_SPEED);
    }

    /**
     * Does the player intersect any enemy
     * @return true if a player intersects any enemy
     */
    public boolean intersectsEnemy(List<Enemy> enemies) {
        for (Enemy enemy: enemies) {
            if (withinRange(this.location, enemy.getLocation(), (BODY_PIECE_SIZE_DP + enemy.getRadius()) * Game.DP_TO_PX_FACTOR)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get player's equipped weapon.
     * @return the Weapon that the player has
     */
    public Weapon getWeapon() { return this.weapon; }

}
