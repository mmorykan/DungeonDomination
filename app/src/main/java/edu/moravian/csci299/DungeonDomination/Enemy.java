package edu.moravian.csci299.DungeonDomination;

import android.graphics.PointF;

/**
 * Enemies are around the same size as the player, except for the boss.
 * Enemies move slightly slower than the player and also fire bullets at the player occasionally.
 * Enemies give coins to the player if they are defeated.
 */
public class Enemy extends Character {

    /** Speed that the enemy move when drawing it and the enemies bullet speed */
    private static final double SPEED = 2.0;
    private static final double BULLET_SPEED = 15.0;

    /** Amount of coins the player received from killing this enemy */
    private final int coinValue;
    
    /** The starting health for the enemy */
    private final int initialHealth;
    
    /**
     * Create an enemy with the given initial position and health, a weapon, and the dpToPxFactor.
     *
     * @param location the initial position
     * @param weapon enemy's weapon
     * @param health enemy's starting health
     * @param radius the hit box of an enemy
     * @param step How far to draw the enemy each iteration
     * @param coinValue The amount of coins this enemy gives to the player when it dies
     */
    public Enemy(PointF location, Weapon weapon, int health, float radius, float step, int coinValue) {
        super(location, weapon, health, radius, step, SPEED, BULLET_SPEED);
        this.initialHealth = health;
        this.coinValue = coinValue;
    }

    /** Get the initial health of the enemy */
    public int getInitialHealth() { return this.initialHealth; }
    
    /** Get the value of coins the enemy is worth when defeated */
    public int getCoinValue() { return this.coinValue; }
}
