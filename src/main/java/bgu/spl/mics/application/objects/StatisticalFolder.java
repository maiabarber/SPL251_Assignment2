package bgu.spl.mics.application.objects;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private AtomicInteger systemRuntime; 
    private AtomicInteger numDetectedObjects;
    private AtomicInteger numTrackedObjects;
    private AtomicInteger numLandmarks;
    private List<Camera> activesCameras;
    private List<LiDarWorkerTracker> activeLiDars;

    private static class StatisticalFolderHolder {
        private static StatisticalFolder instance = new StatisticalFolder(new AtomicInteger(0), new AtomicInteger(0), new AtomicInteger(0), new AtomicInteger(0));
    }

    /**
     * Constructor for StatisticalFolder.
     *
     * @param systemRuntime The runtime of the system.
     * @param numDetectedObjects The number of objects detected by the camera.
     * @param numTrackedObjects The number of objects tracked by the LiDAR.
     * @param numLandmarks The number of landmarks identified by the system.
     */
    private StatisticalFolder(AtomicInteger systemRuntime, AtomicInteger numDetectedObjects, AtomicInteger numTrackedObjects, AtomicInteger numLandmarks) {
        this.systemRuntime = systemRuntime;
        this.numDetectedObjects = numDetectedObjects;
        this.numTrackedObjects = numTrackedObjects;
        this.numLandmarks = numLandmarks;
        this.activesCameras = new ArrayList<>();
        this.activeLiDars = new ArrayList<>();
    }

    public static StatisticalFolder getInstance(){
        return StatisticalFolderHolder.instance;
    }

    /**
     * Returns the runtime of the system.
     *
     * @return The runtime of the system.
     */
    public AtomicInteger getSystemRuntime() {
        return systemRuntime;
    }

    /**
     * Returns the number of objects detected by the camera.
     *
     * @return The number of objects detected by the camera.
     */
    public AtomicInteger getNumDetectedObjects() {
        return numDetectedObjects;
    }

    /**
     * Returns the number of objects tracked by the LiDAR.
     *
     * @return The number of objects tracked by the LiDAR.
     */
    public AtomicInteger getNumTrackedObjects() {
        return numTrackedObjects;
    }

    /**
     * Returns the number of landmarks identified by the system.
     *
     * @return The number of landmarks identified by the system.
     */
    public AtomicInteger getNumLandmarks() {
        return numLandmarks;
    }

    public synchronized void incrementRuntime(int tickTime) {
        this.systemRuntime.addAndGet(tickTime);
    }

    public synchronized void incrementDetectedObjects(int count) {
        this.numDetectedObjects.addAndGet(count);
    }

    public synchronized void incrementTrackedObjects(int count) {
        this.numTrackedObjects.addAndGet(count);
    }

    public synchronized void incrementLandmarks() {
        this.numLandmarks.incrementAndGet();
    }

    public void addActiveCamera(Camera camera){
        activesCameras.add(camera);
    }

    public List<Camera> getActiveCameras(){
        return activesCameras;
    }

    public void addActiveLiDar(LiDarWorkerTracker liDar){
        activeLiDars.add(liDar);
    }

    public List<LiDarWorkerTracker> getActiveLidars(){
        return activeLiDars;
    }

    @Override
    public String toString() {
        return "StatisticalFolder{" +
                "systemRuntime=" + systemRuntime +
                ", numDetectedObjects=" + numDetectedObjects +
                ", numTrackedObjects=" + numTrackedObjects +
                ", numLandmarks=" + numLandmarks +
                '}';
    }
}
