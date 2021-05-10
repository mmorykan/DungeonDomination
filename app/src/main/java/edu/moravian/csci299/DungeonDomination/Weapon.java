package edu.moravian.csci299.DungeonDomination;

/**
 * A weapon representing just a damage value. In the future, this would be an interface 
 * with multiple different weapons implementing it
 */
public class Weapon {

    private final int damage; // weapon's damage

    /**
     * Constructor for new Weapon
     * @param damage damage the weapon does
     */
    public Weapon(int damage) { this.damage = damage; }
    
    /** Get the damage of the weapon */
    public int getDamage() { return this.damage; }
}
