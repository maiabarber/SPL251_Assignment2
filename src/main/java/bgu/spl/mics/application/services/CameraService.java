package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.Callback;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import java.util.List;

public class CameraService extends MicroService {
    private final Camera camera;
    private int nextAvailableTick = 0;

    public CameraService(Camera camera) {
        super("Camera");
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Subscribes to TickBroadcasts and sends DetectObjectsEvents when objects are
     * detected.
     */
    @Override
    protected void initialize() {
        // הרשמה ל-TickBroadcast לביצוע פעולות תקופתיות
        subscribeBroadcast(TickBroadcast.class, tick -> {
            int currentTick = tick.getTickNumber();
            synchronized (StatisticalFolder.getInstance()) {
                StatisticalFolder.getInstance().incrementRuntime(1);
            }
            if (currentTick >= nextAvailableTick) {
                // קבלת רשימת עצמים שזוהו
                StampedDetectedObjects stampedDetectedObjects = camera.getDetectedObjects(currentTick);
                camera.setLastDetectedObjects(stampedDetectedObjects);

                if (!stampedDetectedObjects.getDetectedObjects().isEmpty()) {
                    // יצירת אובייקט StampedDetectedObjects ושליחת DetectObjectsEvent
                    sendEvent(new DetectObjectsEvent(stampedDetectedObjects, camera));
                    synchronized (StatisticalFolder.getInstance()) {
                        StatisticalFolder.getInstance()
                                .incrementDetectedObjects(stampedDetectedObjects.getDetectedObjects().size());
                    }
                    nextAvailableTick = currentTick + camera.getFrequency();
                }
            }
        });

        // הרשמה ל-TerminatedBroadcast לסיום מסודר
        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            System.out.println("CameraService received termination notification from: " + terminated.getSenderName());
            camera.setStatus(STATUS.DOWN);
            terminate();
            sendBroadcast(new TerminatedBroadcast(getName()));
        });

        // הרשמה ל-CrashedBroadcast לטיפול בקריסות שירותים אחרים
        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            System.out.println("CameraService received crash notification from: " + crashed.getSenderName() + " "
                    + crashed.getDescription());
            camera.setStatus(STATUS.ERROR);
            terminate();
        });
    }
}
