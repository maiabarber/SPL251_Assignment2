package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.StampedDetectedObjects; // Ensure this package and class exist
/**
 * DetectObjectsEvent is an event that contains a list of detected objects.
 * Sent by: Camera
 * Handled by: LiDar
 */
public class DetectObjectsEvent implements Event<Boolean> {

    private final StampedDetectedObjects stampedDetectedObjects;
    private final Camera camera;

    public DetectObjectsEvent(StampedDetectedObjects stampedDetectedObjects , Camera camera) {
        this.stampedDetectedObjects = stampedDetectedObjects;
        this.camera = camera;
    }
    public StampedDetectedObjects getStampedDetectedObjects() {
        return stampedDetectedObjects;
    }

    public Camera getCamera() {
        return camera;
    }
}
