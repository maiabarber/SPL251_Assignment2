package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private int Time; // the time when the objects were detected
    private List<DetectedObject> detectedObjects; // the objects detected by the camera sensor

    /**
     * Constructor for StampedDetectedObjects.
     *
     * @param Time The time when the objects were detected.
     * @param detectedObjects The objects detected by the camera sensor.
     */
    public StampedDetectedObjects(int Time, List<DetectedObject> detectedObjects) {
        this.Time = Time;
        this.detectedObjects = detectedObjects;
    }

    /**
     * Returns the time when the objects were detected.
     *
     * @return The time when the objects were detected.
     */
    public int getTime() {
        return Time;
    }

    /**
     * Returns the objects detected by the camera sensor.
     *
     * @return The objects detected by the camera sensor.
     */
    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }
}
