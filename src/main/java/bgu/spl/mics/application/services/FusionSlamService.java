package bgu.spl.mics.application.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedCloudPoints;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents
 * from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam fusionSlam;// רשימת poses ורשימת landmarks
    private final Set<String> terminatedServices = new HashSet<>();
    private static int totalServicesCount;
    private Path filePath;


    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global
     *                   map.
     */
    public FusionSlamService(FusionSlam fusionSlam, Path filePath) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
        this.filePath = filePath;

    }

    private boolean allServicesTerminated() {
        return terminatedServices.size() == totalServicesCount;
    }

    public static void setTotalServicesCount(int count) {
        totalServicesCount = count;
    }

    public Path getFilePath() {
        return filePath;
    }
    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and
     * TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeEvent(TrackedObjectsEvent.class, trackedObjectsEvent -> {
            fusionSlam.addTrackedObjects(trackedObjectsEvent.getTrackedObjects());
            complete(trackedObjectsEvent, true);
        });

        subscribeEvent(PoseEvent.class, poseEvent -> {
            fusionSlam.addPose(poseEvent.getCurrentPose());
            // complete(poseEvent, true);
        });

        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            System.out.println("System error detected: " + crashed.getSenderName());
            int crashTick = crashed.getTime();
            List<Pose> posesUntilCrash = getAllPosesUntilCrash(crashTick);
            createCrashOutput(crashed.getDescription(), crashed.getSenderName(), crashTick);
            terminate();
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            System.out.println("TerminatedBroadcast received from: " + terminated.getSenderName());
            terminatedServices.add(terminated.getSenderName());
            if (allServicesTerminated()) {
                createTerminationOutput();   
            }
        });
    }

    private void createCrashOutput(String error, String faultySensor, int crashTick) {
        Map<String, Object> outputData = new HashMap<>();
        outputData.put("Error", error);
        outputData.put("faultySensor", faultySensor);
        outputData.put("lastFrames", getLastFrames());
        outputData.put("poses", getAllPosesUntilCrash(crashTick));
        outputData.put("statistics", StatisticalFolder.getInstance());
        writeOutputToFile(outputData, filePath.toString() + "/output_file.json");
    }

    /**
     * Creates the termination output JSON file with system data.
     */
    private void createTerminationOutput() {
        Map<String, Object> outputData = new HashMap<>();
        outputData.put("poses", fusionSlam.getPoses());
        outputData.put("statistics", StatisticalFolder.getInstance());
        outputData.put("landmarks", fusionSlam.getLandmarks());
        writeOutputToFile(outputData, filePath.toString() + "/output_file.json");
    }

    /**
     * Writes the output data to a JSON file.
     *
     * @param outputData The data to write.
     * @param fileName   The name of the file to write to.
     */
    private void writeOutputToFile(Map<String, Object> outputData, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            Gson json = new GsonBuilder().setPrettyPrinting().create();
            json.toJson(outputData, writer);
            System.out.println("Output written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Retrieves the last frames of detected objects from cameras and LiDAR.
     *
     * @return A map containing the last frames.
     */
    private Map<String, Object> getLastFrames() {
        Map<String, Object> lastFrames = new HashMap<>();
        Map<Integer, List<DetectedObject>> cameraLastFrames = new HashMap<>();
        for (Camera camera : StatisticalFolder.getInstance().getActiveCameras()) {
            cameraLastFrames.put(camera.getId(), camera.getLastDetectedObjects());
        }
        lastFrames.put("lastCameraFrames", cameraLastFrames);

        Map<Integer, List<TrackedObject>> lidarLastFrames = new HashMap<>();
        for (LiDarWorkerTracker lidar : StatisticalFolder.getInstance().getActiveLidars()) {
            lidarLastFrames.put(lidar.getId(), lidar.getLastTrackedObjects());
        }
        lastFrames.put("lastLidarFrames", lidarLastFrames);
        return lastFrames;
    }

    private List<Pose> getAllPosesUntilCrash(int crashTick) {
        List<Pose> filteredPoses = new ArrayList<>();
        for (Pose pose : fusionSlam.getInstance().getPoses()) {
            if (pose.getTime() <= crashTick) {
                filteredPoses.add(pose);
            }
        }
        return filteredPoses;
    }
}
