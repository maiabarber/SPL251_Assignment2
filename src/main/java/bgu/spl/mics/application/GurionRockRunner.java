package bgu.spl.mics.application;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.services.TimeService;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        int totalServicesCount = 0;
        Path absolutePath = Paths.get(args[0]).toAbsolutePath();
        Path folder = absolutePath.getParent();

        try (FileReader reader = new FileReader(absolutePath.toString())) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> config = gson.fromJson(reader, type);
    
            // קריאת המצלמות
            Map<String, Object> camerasConfig = (Map<String, Object>) config.get("Cameras");
            List<Map<String, Object>> camerasConfigurations = (List<Map<String, Object>>) camerasConfig.get("CamerasConfigurations");
            String cameraDataPath = folder.toString() + (String) camerasConfig.get("camera_datas_path");//קישור לcamera data
            Map<String, List<Map<String, Object>>> allcamerasData = loadCameraData(cameraDataPath); //מילון שהמפתחות זה שם המצלמה והערכים זה מילון (ויש 2 מפתחות זמן ודטקטד אובגקטס)

            // יצירת מצלמות
            for (Map<String, Object> cameraConfig : camerasConfigurations) {
                int id = ((Double) cameraConfig.get("id")).intValue();
                int frequency = ((Double) cameraConfig.get("frequency")).intValue();
                String cameraKey = (String) cameraConfig.get("camera_key");
                List<Map<String, Object>> cameraData = allcamerasData.get(cameraKey);

                Camera camera = new Camera(id, frequency, cameraData);
                CameraService cameraService = new CameraService(camera);
                StatisticalFolder.getInstance().addActiveCamera(camera); //בשביל ההדפסה שנדע איזה מצלמות עובדות
                new Thread(cameraService).start();
                totalServicesCount++;
            }
            // קריאת LiDAR
            Map<String, Object> lidarConfig = (Map<String, Object>) config.get("LiDarWorkers");
            List<Map<String, Object>> lidarConfigurations = (List<Map<String, Object>>) lidarConfig.get("LidarConfigurations");
            String lidarDataPath = folder.toString() + (String) lidarConfig.get("lidars_data_path");
            LiDarDataBase liDarDataBase = LiDarDataBase.getInstance(lidarDataPath);

// קריאת poseJsonFile
            String poseJsonFile = folder.toString() + "/" + (String) config.get("poseJsonFile");
            List<Pose> poseList = loadPoseData(poseJsonFile);

            // יצירת GPSIMU עם ה-poses
            GPSIMU gpsimu = new GPSIMU(1, poseList);

            // יצירת PoseService
            PoseService poseService = new PoseService(gpsimu);
            new Thread(poseService).start();
            totalServicesCount++;

            // יצירת LiDAR Workers
            for (Map<String, Object> lidarConfigMap : lidarConfigurations) {
                int id = ((Double) lidarConfigMap.get("id")).intValue();
                int frequency = ((Double) lidarConfigMap.get("frequency")).intValue();
                // יצירת LiDarWorkerTracker
                LiDarWorkerTracker liDarWorkerTracker = new LiDarWorkerTracker(id, frequency);
                LiDarService liDarService = new LiDarService(liDarWorkerTracker, liDarDataBase, poseService);
                StatisticalFolder.getInstance().addActiveLiDar(liDarWorkerTracker); //בשביל ההדפסה שנדע איזה מצלמות עובדות
                new Thread(liDarService).start();
                totalServicesCount++;
            }
            
            // קריאת TickTime ו-Duration
            int tickTime = ((Double) config.get("TickTime")).intValue();
            int duration = ((Double) config.get("Duration")).intValue();

            // יצירת מיקרוסרוויסים
            TimeService timeService = new TimeService(tickTime, duration);
            // ניצור Thread עבור כל סרוויס
            new Thread(timeService).start();
            totalServicesCount++;
    
            FusionSlam fusionSlam = FusionSlam.getInstance();
            // יצירת סרוויס FusionSLAM
            FusionSlamService fusionSlamService = new FusionSlamService(fusionSlam, folder);
            new Thread(fusionSlamService).start();
            fusionSlamService.setTotalServicesCount(totalServicesCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

public static Map<String, List<Map<String, Object>>> loadCameraData(String cameraDataPath) {
    try (FileReader reader = new FileReader(cameraDataPath)) {
        // שימוש ב-Gson להמרת JSON לאובייקט
        Gson gson = new Gson();
        // הגדרת סוג המידע שנקבל
        Type type = new TypeToken<Map<String, List<Map<String, Object>>>>() {}.getType();
        // המרת ה-JSON למבנה Java
        return gson.fromJson(reader, type);
    } catch (Exception e) {
        throw new RuntimeException("Failed to load camera data: " + e.getMessage(), e);
    }
}
/**
 * Loads Pose data from a JSON file.
 *
 * @param poseJsonFilePath The path to the pose JSON file.
 * @return A list of Pose objects.
 */
public static List<Pose> loadPoseData(String poseJsonFilePath) {
    try (FileReader reader = new FileReader(poseJsonFilePath)) {
        Gson gson = new Gson();
        // הגדרת סוג הנתונים לטעינה
        Type type = new TypeToken<List<Pose>>() {}.getType();
        // קריאת הנתונים והמרתם לרשימת Pose
        return gson.fromJson(reader, type);
    } catch (Exception e) {
        throw new RuntimeException("Failed to load pose data: " + e.getMessage(), e);
    }
}
}
