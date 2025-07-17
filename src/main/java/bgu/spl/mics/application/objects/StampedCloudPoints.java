package bgu.spl.mics.application.objects;

import java.util.Arrays;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private String id;
    private int time;
    private Double[][] cloudPoints;

    /**
     * Constructor for StampedCloudPoints.
     *
     * @param id The ID of the tracked object.
     * @param time The time when the cloud points were detected.
     * @param cloudPoints The cloud points detected by the LiDAR.
     */
    public StampedCloudPoints(String id, int time, Double[][] cloudPoints) {
        this.id = id;
        this.time = time;
        this.cloudPoints = cloudPoints;
    }

    /**
     * Returns the ID of the tracked object.
     *
     * @return The ID of the tracked object.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the time when the cloud points were detected.
     *
     * @return The time when the cloud points were detected.
     */
    public int getTime() {
        return time;
    }

    /**
     * Returns the cloud points detected by the LiDAR.
     *
     * @return The cloud points detected by the LiDAR.
     */
    public Double[][] getCloudPoints() {
        if (cloudPoints == null) {
            System.out.println("CloudPoints is null in StampedCloudPoints with id: " + id + " and time: " + time + "STAMPEDCLOUDPOINTS class 52");
        }
        return cloudPoints;
    }

    public String toString() {
        return "StampedCloudPoints{" +
                "id='" + id + '\'' +
                ", time=" + time +
                ", cloudPoints=" + java.util.Arrays.deepToString(cloudPoints)
                +'}';
    }
}
