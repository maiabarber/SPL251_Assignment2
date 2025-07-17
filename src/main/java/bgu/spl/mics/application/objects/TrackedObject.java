package bgu.spl.mics.application.objects;

import java.util.ArrayList;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private String id; // the ID of the tracked object
    private int time; // the time when the object was tracked
    private String description; // the description of the tracked object
    private ArrayList<CloudPoint> coordinates; // the coordinates of the tracked object

    /**
     * Constructor for TrackedObject.
     *
     * @param id The ID of the tracked object.
     * @param time The time when the object was tracked.
     * @param description The description of the tracked object.
     * @param coordinates The coordinates of the tracked object.
     */
    public TrackedObject(String id, int time, String description, ArrayList<CloudPoint> coordinates) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
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
     * Returns the time when the object was tracked.
     *
     * @return The time when the object was tracked.
     */
    public int getTime() {
        return time;
    }
    
    /**
     * Returns the description of the tracked object.
     *
     * @return The description of the tracked object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the coordinates of the tracked object.
     *
     * @return The coordinates of the tracked object.
     */
    public ArrayList<CloudPoint> getCoordinates() {
        return coordinates;
    }
}
