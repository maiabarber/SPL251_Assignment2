package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.internal.LinkedTreeMap;
/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private int id;
    private int frequency; // the frequency of the camera sensor CHECK
    private STATUS currentStatus; // the current status of the camera sensor
    private List<Map<String, Object>> cameraData; // the objects detected by the camera sensor by tick
    private List<StampedDetectedObjects> detectedObjectsList; // the objects detected by the camera sensor
    private StampedDetectedObjects lastDetectedObjects; // the last objects detected by the camera sensor

    /**
     * Constructor for Camera.
     *
     * @param id The ID of the camera sensor.
     * @param frequency The frequency of the camera sensor.
     */
    public Camera(int id, int frequency, List<Map<String, Object>> cameraData) {
        this.id = id;
        this.frequency = frequency;
        this.currentStatus = STATUS.UP;
        this.cameraData = cameraData;
        this.detectedObjectsList = new ArrayList<>();
    }

    /**
     * Returns the ID of the camera sensor.
     *
     * @return The ID of the camera sensor.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the frequency of the camera sensor.
     *
     * @return The frequency of the camera sensor.
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Returns the status of the camera sensor.
     *
     * @return The status of the camera sensor.
        return currentStatus;
    */
    public STATUS getStatus() {
        return currentStatus;
    }

    /**
     * Returns the objects detected by the camera sensor.
     *
     * @return The objects detected by the camera sensor.
     */
    public StampedDetectedObjects getDetectedObjects(int tick) {
        List<DetectedObject> detectedObject1 = new ArrayList<>();
        for (Map<String, Object> detectedObject : cameraData) {
            if ((int) (double) detectedObject.get("time") == tick) {
                List<LinkedTreeMap<String,String>> to_convert=(List<LinkedTreeMap<String,String>>)detectedObject.get("detectedObjects");
                for (LinkedTreeMap<String,String> object : to_convert) {
                    detectedObject1.add(new DetectedObject(object.get("id"),object.get("description")));
                }
            }
        } 
        return new StampedDetectedObjects(tick, detectedObject1);
    }

    public void setStatus(STATUS status) {
        this.currentStatus = status;
    }

    public void setLastDetectedObjects(StampedDetectedObjects stampedDetectedObjects) {
        this.lastDetectedObjects = stampedDetectedObjects;
    }

    public List<DetectedObject> getLastDetectedObjects() {
        return lastDetectedObjects.getDetectedObjects();
    }
}
