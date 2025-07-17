package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * PoseService is responsible for maintaining the robot's current pose (position
 * and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private final GPSIMU gpsimu;// מחזיק currentTick,status,PoseList

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;
    }


    public Pose getPose() {
        return gpsimu.getPose(gpsimu.getCurrentTick());
    }

    public int getCurrentTick() {
        return gpsimu.getCurrentTick();
    }
    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the
     * current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            int currentTick = tick.getTickNumber();
            Pose pose = gpsimu.getPose(currentTick);
            if (pose != null) {
                sendEvent(new PoseEvent(pose));
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            System.out.println("PoseService received termination notification from: " + terminated.getSenderName());
            gpsimu.setStatus(STATUS.DOWN);
            terminate();
            sendBroadcast(new TerminatedBroadcast(getName()));
        });

        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            System.out.println("PoseService received crash notification from: " + crashed.getSenderName() + " "
                    + crashed.getDescription());
            gpsimu.setStatus(STATUS.ERROR);
            terminate();
        });
    }
}
