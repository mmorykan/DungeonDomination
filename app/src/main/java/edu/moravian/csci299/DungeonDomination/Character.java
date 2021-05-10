package edu.moravian.csci299.DungeonDomination;

import android.graphics.PointF;

/**
 * The parent class for Player, Bullet, and Enemy. Contains most attributes for each class 
 * and provides the move method
 */
public class Character {

    protected PointF location; // location in the room
    protected final Weapon weapon; // their weapon
    protected int health; // current health
    protected double direction; // direction they're moving
    protected double speed; // speed they're moving
    protected double bulletSpeed; // speed their bullet travels
    protected float radius; // their radius
    protected float step; // step to move character by
    protected double distXToTravel = 0.0, distYToTravel = 0.0; // distance to be travelled
    
    /**
     * Create the player with the given initial position.
     * @param location the initial position
     * @param weapon player's current weapon
     * @param health player's starting health
     * @param radius The radius of the character, defining the hit box
     * @param step The amount of space to move the character by
     * @param speed How quickly the character moves on the screen
     * @param bulletSpeed The speed of the bullet fired by this character
     */
     public Character(PointF location, Weapon weapon, int health, float radius, float step, double speed, double bulletSpeed) {
        this.location = location;
        this.weapon = weapon;
        this.health = health;
        this.radius = radius;
        this.step = step;
        this.speed = speed;
        this.bulletSpeed = bulletSpeed;
    }
    
    /** Set character's location */
    public void setLocation(PointF location) { this.location = location; }
    
    /** Get character's current location */
    public PointF getLocation() { return this.location; }

    /** Set their direction */
    public void setDirection(double direction) { this.direction = direction; }

    /** Get the radius of the character */
    public float getRadius() { return this.radius; }

    /** Get the character's remaining health */
    public int getHealth() { return this.health; }

    /** Set the character's health */
    public void setHealth(int health) { this.health = health; }

    /** Decrease the health of a character */
    public void decreaseHealth(int nerf) { this.health -= nerf; }
    
    /** Get the character's equipped weapon */
    public Weapon getWeapon() { return this.weapon; }
    
    /** Get the character's bullet speed */
    public double getBulletSpeed() { return this.bulletSpeed; }

    /**
     * Checks if the player is "out of bounds" of a rectangle that goes
     * from 0,0 to the given width and height.
     * @param width the width of the bounds in px
     * @param height the height of the bounds in px
     * @return true if the player is out of bounds
     */
    public boolean isOutOfBounds(int width, int height) {
        PointF player = this.location;
        return player.x < radius || player.y < radius
                    || player.x >= width - radius || player.y >= height - radius;
    }

    /**
     * Get the amount to move in the x and y direction and modify the characters location
     */
    public void move() {
        // Update the distance to be travelled
        double distance = this.speed * Game.DP_TO_PX_FACTOR;
        distXToTravel += Math.cos(direction) * distance;
        distYToTravel += Math.sin(direction) * distance;

        // Move the player as much of the distance as possible
        final double stepDist = this.step * Game.DP_TO_PX_FACTOR; // distance of each step
        double distTotal = Math.hypot(distYToTravel, distXToTravel); // total distance to travel
        if (distTotal >= stepDist) {
            double angle = Math.atan2(distYToTravel, distXToTravel); // angle to travel at
            double stepXDist = stepDist * Math.cos(angle); // step distance in X direction
            double stepYDist = stepDist * Math.sin(angle); // step distance in Y direction
            while (distTotal >= stepDist) { // while the distance to travel is at least one step
                // Remove this distance from the remaining distance to travel
                distTotal -= stepDist;

                // Move the player
                this.location.offset((float) stepXDist, (float) stepYDist);
            }

            // Update the remaining distance
            distXToTravel = distTotal * Math.cos(angle);
            distYToTravel = distTotal * Math.cos(angle);
        }
    }
}
