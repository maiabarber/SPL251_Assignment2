package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;//זמן נוכחי
    private STATUS currentStatus; // the current status of the GPS/IMU system
    private List <Pose> PoseList; // the robot's poses

    /**
     * Constructor for GPSIMU.
     *
     * @param currentTick The current tick of the system.
     */
    public GPSIMU(int currentTick,List<Pose> poses) {
        this.currentTick = currentTick;
        this.currentStatus = STATUS.UP;// to check
        this.PoseList = poses;
    }

    /**
     * Returns the current tick of the system.
     *
     * @return The current tick of the system.
     */
    public int getCurrentTick() {
        return currentTick;
    }

    public Pose getPose(int time){
        for (Pose pose : PoseList) {
            if (pose.getTime() == time) {
                return pose;
            }
        }
        return null;
    }

    /**
     * Returns the status of the GPS/IMU system.
     *
     * @return The status of the GPS/IMU system.
     */
    public STATUS getStatus() {
        return currentStatus;
    }

    /**
     * Returns the robot's poses.
     *
     * @return The robot's poses.
     */
    public List<Pose> getPoseList() {
        return PoseList;
    }

    /**
     * Sets the status of the GPS/IMU system.
     *
     * @param status The status of the GPS/IMU system.
     */
    public void setStatus(STATUS status) {
        this.currentStatus = status;
    }
}

