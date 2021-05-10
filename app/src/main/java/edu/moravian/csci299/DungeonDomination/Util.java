package edu.moravian.csci299.DungeonDomination;

import android.graphics.PointF;

/**
 * Utilities for use by other classes.
 */
public class Util {
    /**
     * Checks if two points are closer than a certain range of each other.
     * @param a the first point
     * @param b the first point
     * @param range the maximum distance allowed between the points
     * @return true if the distance from a to b is less than range
     */
    public static boolean withinRange(PointF a, PointF b, double range) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return dx*dx + dy*dy < range*range;
    }
}
