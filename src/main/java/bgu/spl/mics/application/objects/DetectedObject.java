package bgu.spl.mics.application.objects;

/**
 * DetectedObject represents an object detected by the camera.
 * It contains information such as the object's ID and description.
 */
public class DetectedObject {//עצמים שזוהו ע"י המצלמה ללא קואורדינטות
    private String id;
    private String description;

    /**
     * Constructor for DetectedObject.
     *
     * @param id The ID of the detected object.
     * @param description The description of the detected object.
     */
    public DetectedObject(String id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Returns the ID of the detected object.
     *
     * @return The ID of the detected object.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the description of the detected object.
     *
     * @return The description of the detected object.
     */
    public String getDescription() {
        return description;
    }

}
