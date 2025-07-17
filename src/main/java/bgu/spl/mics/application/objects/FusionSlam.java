package bgu.spl.mics.application.objects;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update
 * a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam
 * exists.
 */
public class FusionSlam {
    private ArrayList<LandMark> landmarks; // map of the environment לכל לנדמארק id , description, cloudpoints
    private List<Pose> Poses;// מיקומים של הרובוט
    // Singleton instance holder

    private static class FusionSlamHolder {
        private static FusionSlam instance = new FusionSlam();
    }

    private FusionSlam() {
        landmarks = new ArrayList<>();
        Poses = new ArrayList<Pose>();
    }

    /**
     * Adds a landmark to the global map.
     *
     * @param landmark The landmark to add.
     */
    public void addLandmark(LandMark landmark) {
        landmarks.add(landmark);
    }

    /**
     * Adds a pose to the global map.
     *
     * @param pose The pose to add.
     */

    public void addPose(Pose pose) {
        Poses.add(pose);
    }

    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    public ArrayList<LandMark> getLandmarks() {
        return landmarks;
    }

    public List<Pose> getPoses() {
        return Poses;
    }

    public void setLandmarks(ArrayList<LandMark> landmarks) {
        this.landmarks = landmarks;
    }

    public void setPoses(List<Pose> poses) {
        Poses = poses;
    }

    public void addTrackedObjects(List<TrackedObject> trackedObjects) {
        ArrayList<LandMark> tmpLandMarks = new ArrayList<>();
        for (TrackedObject trackedObject : trackedObjects) {
            boolean exists = false; // דגל לבדיקה אם Landmark קיים
            for (LandMark landmark : landmarks) {
                if (landmark.getId().equals(trackedObject.getId())) {
                    exists = true;
    
                    // עדכון ה-Landmark הקיים עם ערכים חדשים
                    List<CloudPoint> avgCoordinate = getAvg(landmark.getCoordinates(), trackedObject.getCoordinates());
                    landmark.updateCoordinates(avgCoordinate); // נדרש לעדכן את המתודה updateCoordinates במחלקה LandMark
                    break;
                }
            }
    
            // אם ה-Landmark לא קיים, צור והוסף חדש
            if (!exists) {
                List<CloudPoint> coordinates = trackedObject.getCoordinates();
                LandMark newLandmark = new LandMark(trackedObject.getId(), trackedObject.getDescription(), coordinates);
                tmpLandMarks.add(newLandmark);
                StatisticalFolder.getInstance().incrementLandmarks();
            }
        }
    
        // הוספת LandMarks חדשים
        landmarks.addAll(tmpLandMarks);
    }

    public static List<CloudPoint> getAvg(List<CloudPoint> L, List<CloudPoint> C) {
        List<CloudPoint> res = new ArrayList<>();
        int k = Math.min(L.size(), C.size());
        for (int i = 0; i < k; i++) {
            CloudPoint Li = L.get(i);
            CloudPoint Ci = C.get(i);
            double x = (Li.getX() + Ci.getX()) / 2;
            double y = (Li.getY() + Ci.getY()) / 2;
            CloudPoint avg = new CloudPoint(x, y);
            res.add(avg);
        }
        return res;
    }
}
