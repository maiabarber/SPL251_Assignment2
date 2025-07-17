package bgu.spl.mics.application.objects;
import java.util.List;
/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private Integer id;
    private Integer frequency; // time interval at which the liDar send new events
    private STATUS currentStatus; // the current status of the LiDar sensor
    private List <TrackedObject> lastTrackedObjects; // the last objects tracked by the LiDar sensor

    /**
     * Constructor for LiDarWorkerTracker.
     *
     * @param id The ID of the LiDar worker.
     * @param frequency The frequency of the LiDar worker.
     */
    public LiDarWorkerTracker(Integer id, Integer frequency) {
        this.id = id;
        this.frequency = frequency;
        this.currentStatus = STATUS.UP;
    }

    /**
     * Returns the ID of the LiDar worker.
     *
     * @return The ID of the LiDar worker.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Returns the frequency of the LiDar worker.
     *
     * @return The frequency of the LiDar worker.
     */
    public Integer getFrequency() {
        return frequency;
    }

    /**
     * Returns the status of the LiDar worker.
     *
     * @return The status of the LiDar worker.
     */
    public STATUS getStatus() {
        return currentStatus;
    }
    
    /**
     * Returns the last objects tracked by the LiDar sensor.
     *
     * @return The last objects tracked by the LiDar sensor.
     */
    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }

    public void setStatus(STATUS status) {
        this.currentStatus = status;
    }
}

