package bgu.spl.mics.application.objects;

import java.sql.Time;

/**
 * Represents the robot's pose (position and orientation) in the environment.
 * Includes x, y coordinates and the yaw angle relative to a global coordinate system.
 */
public class Pose {
    private float x;
    private float y;
    private float yaw; // the angle of the robot from the charging station
    private int time; // the time when the robot is in that pose

    /**
     * Constructor for Pose.
     *
     * @param x The x coordinate of the robot's position.
     * @param y The y coordinate of the robot's position.
     * @param yaw The yaw angle of the robot's orientation.
     */
    public Pose(float x, float y, float yaw) {
        this.x = x;
        this.y = y;
        this.yaw = yaw;
    }

    /**
     * Returns the x coordinate of the robot's position.
     *
     * @return The x coordinate of the robot's position.
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the robot's position.
     *
     * @return The y coordinate of the robot's position.
     */
    public float getY() {
        return y;
    }

    /**
     * Returns the yaw angle of the robot's orientation.
     *
     * @return The yaw angle of the robot's orientation.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Returns the time when the robot is in that pose.
     *
     * @return The time when the robot is in that pose.
     */
    public int getTime() {
        return time;

    
}
}
