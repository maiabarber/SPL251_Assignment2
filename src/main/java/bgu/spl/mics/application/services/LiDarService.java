package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.StampedCloudPoints;
import bgu.spl.mics.application.GurionRockRunner;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and
 * process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private LiDarWorkerTracker liDarWorkerTracker;
    private final LiDarDataBase liDarDataBase1;
    private int nextAvailableTick = 0;
    private ArrayList<TrackedObject> trackedObjectsToSend;
    private PoseService poseService;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service
     *                           will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker, LiDarDataBase liDarDataBase, PoseService poseService) {
        super(" LiDarWorkerTracker");
        this.liDarWorkerTracker = LiDarWorkerTracker;
        this.liDarDataBase1 = liDarDataBase;
        this.trackedObjectsToSend = new ArrayList<>();
        this.poseService = poseService;        
    }

    /**
     * Retrieves the cloud points for a specific object at a specific time.
     *
     * @param id   The ID of the object to retrieve cloud points for.
     * @param time The time to retrieve cloud points for.
     * @return The cloud points for the object at the specified time.
     */
    // private ArrayList<CloudPoint> getCoordinates(Camera eventCamera, DetectedObject detected, int time) {
    //     List<StampedCloudPoints> cloudPoints = liDarDataBase1.getCloudPoints();
    //     if (cloudPoints == null || cloudPoints.isEmpty()) {
    //         throw new IllegalStateException("LiDarDataBase returned null or empty cloudPoints list     LIDARSERVICE 68 ");
    //     }
    //     Double[][] list_of_lists_of_doubles =  null;
    //     for (StampedCloudPoints stampedCloudPoints : cloudPoints) {
    //         if (stampedCloudPoints.getId() == "ERROR"){
    //             sendBroadcast(new CrashedBroadcast("Camera" + eventCamera.getId(), detected.getDescription(), time));
    //         }
    //         if (stampedCloudPoints.getTime() == time && stampedCloudPoints.getId().equals(detected.getId())) {
    //             list_of_lists_of_doubles = stampedCloudPoints.getCloudPoints();
    //             break;
    //         }
    //     }

    private ArrayList<CloudPoint> getCoordinates(Camera eventCamera, DetectedObject detected, int time, double robotX, double robotY, double yaw) {
        List<StampedCloudPoints> cloudPoints = liDarDataBase1.getCloudPoints();
        if (cloudPoints == null || cloudPoints.isEmpty()) {
            throw new IllegalStateException("LiDarDataBase returned null or empty cloudPoints list in getCoordinates.");
        }
        Double[][] listOfLocalPoints = null;
        for (StampedCloudPoints stampedCloudPoints : cloudPoints) {
            if (stampedCloudPoints.getId().equals("ERROR")) {
                sendBroadcast(new CrashedBroadcast("Camera" + eventCamera.getId(), detected.getDescription(), time));
            }
            if (stampedCloudPoints.getTime() == time && stampedCloudPoints.getId().equals(detected.getId())) {
                listOfLocalPoints = stampedCloudPoints.getCloudPoints();
                break;
            }
        }
        if (listOfLocalPoints == null) {
            throw new IllegalStateException("LiDarDataBase returned null for cloudPoints");
        }
        ArrayList<CloudPoint> globalPoints = new ArrayList<>();
        double yawRadians = Math.toRadians(yaw); // Convert yaw to radians
        for (Double[] localPoint : listOfLocalPoints) {
            if (localPoint == null) {
                throw new IllegalStateException("Null cloudPoint found in data for ID: " + eventCamera.getId() + ", Time: " + time);
            }
            double localX = localPoint[0];
            double localY = localPoint[1];
            double globalX = robotX + (localX * Math.cos(yawRadians)) - (localY * Math.sin(yawRadians));
            double globalY = robotY + (localX * Math.sin(yawRadians)) + (localY * Math.cos(yawRadians));
            globalPoints.add(new CloudPoint(globalX, globalY));
        } 
        return globalPoints;
    }

//     // בדיקה למחוק
//     public static void printDoubleArray(Double[][] array) {
//         if (array == null) {
//             System.out.println("Array is null");
//             return;
//         }
//         for (int i = 0; i < array.length; i++) {
//             if (array[i] == null) {
//                 System.out.println("Row " + i + " is null");
//                 continue;
//             }
//             for (int j = 0; j < array[i].length; j++) {
//                 try {
//                     System.out.print(array[i][j] + " ");
//                 } catch (Exception e) {
//                     System.out.println("Error accessing cell [" + i + "][" + j + "]");
//                     e.printStackTrace();
//                 }
//             }
//             System.out.println();
//     }
// }


    /**
     * Converts a list of DetectedObjects to a list of TrackedObjects.
     *
     * @param detectedObjects The list of DetectedObjects to convert.
     * @return A list of TrackedObjects converted from the DetectedObjects.
     */

    // private List<TrackedObject> convertToTrackedObjects(Camera eventCamera,StampedDetectedObjects stampedDetectedObjects) {
    //     List<DetectedObject> detectedObjects = stampedDetectedObjects.getDetectedObjects();
    //     int time = stampedDetectedObjects.getTime();
    //     List<TrackedObject> trackedObjects = new ArrayList<>();
    //     for (DetectedObject detected : detectedObjects) {
    //         if (detected.getId() == "ERROR") {
    //             sendBroadcast(new CrashedBroadcast(detected.getId(), detected.getDescription(), time));
    //         }
    //         TrackedObject tracked = new TrackedObject(detected.getId(), time, detected.getDescription(),
    //                 getCoordinates(eventCamera, detected, time));
    //         trackedObjects.add(tracked);
    //     }
    //     return trackedObjects;
    // }


    private List<TrackedObject> convertToTrackedObjects(Camera eventCamera, StampedDetectedObjects stampedDetectedObjects, double robotX, double robotY, double yaw) {
        List<DetectedObject> detectedObjects = stampedDetectedObjects.getDetectedObjects();
        int time = stampedDetectedObjects.getTime();
        List<TrackedObject> trackedObjects = new ArrayList<>();
    
        try {
            for (DetectedObject detected : detectedObjects) {
                if (detected.getId().equals("ERROR")) {
                    sendBroadcast(new CrashedBroadcast(detected.getId(), detected.getDescription(), time));
                }
                TrackedObject tracked = new TrackedObject(
                    detected.getId(),
                    time,
                    detected.getDescription(),
                    getCoordinates(eventCamera, detected, time, robotX, robotY, yaw)
                );
                trackedObjects.add(tracked);
            }
        } catch (Exception e) {
        }
    return trackedObjects;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeEvent(DetectObjectsEvent.class, event -> {
            Pose currentPose = poseService.getPose(); // Retrieve current pose from the PoseService
            while (currentPose == null) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
                currentPose = poseService.getPose();
            }
            List<TrackedObject> newTrackedObjects = convertToTrackedObjects(
                                                        event.getCamera(), 
                                                        event.getStampedDetectedObjects(),
                                                        currentPose.getX(),
                                                        currentPose.getY(),
                                                        currentPose.getYaw()
                                                        );

    if (newTrackedObjects != null && !newTrackedObjects.isEmpty()) {
        trackedObjectsToSend.clear();
        this.trackedObjectsToSend.addAll(newTrackedObjects);
    }
    complete(event, true);
});
        //     List<TrackedObject> newTrackedObjects = convertToTrackedObjects(event.getCamera(), event.getStampedDetectedObjects());
        //     if (newTrackedObjects != null && !newTrackedObjects.isEmpty()){
        //         this.trackedObjectsToSend.addAll(newTrackedObjects);
        //     }
        //         complete(event, true);
        // });

        subscribeBroadcast(TickBroadcast.class, tick -> {
            int currentTick = tick.getTickNumber();
            if (currentTick >= nextAvailableTick) {
                if (!this.trackedObjectsToSend.isEmpty()) {
                    synchronized (StatisticalFolder.getInstance()) {
                        StatisticalFolder.getInstance().incrementTrackedObjects(trackedObjectsToSend.size());
                    }
                    sendEvent(new TrackedObjectsEvent(new ArrayList<>(trackedObjectsToSend)));
                    trackedObjectsToSend.clear();
                    nextAvailableTick = currentTick + liDarWorkerTracker.getFrequency();
                }
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            System.out.println("LiDarService received termination notification from: " + terminated.getSenderName());
            liDarWorkerTracker.setStatus(STATUS.DOWN);
            terminate();
            sendBroadcast(new TerminatedBroadcast(getName()));
        });

        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            System.out.println("LiDarWorkerService received crash notification from: " + crashed.getSenderName() + " "
                    + crashed.getDescription());
            liDarWorkerTracker.setStatus(STATUS.ERROR);
            // הדפסת סטטיסטיקל
            terminate();
        });

    }
}
