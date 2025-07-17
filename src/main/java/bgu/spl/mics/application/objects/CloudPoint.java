package bgu.spl.mics.application.objects;

/**
 * CloudPoint represents a specific point in a 3D space as detected by the LiDAR.
 * These points are used to generate a point cloud representing objects in the environment.
 */
public class CloudPoint {
    private double x;
    private double y;

    /**
     * Constructor for CloudPoint.
     *
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     */
    public CloudPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate of the point.
     *
     * @return The x coordinate of the point.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the point.
     *
     * @return The y coordinate of the point.
     */
    public double getY() {
        return y;
    }
}
