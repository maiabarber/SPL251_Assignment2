package bgu.spl.mics.application.objects;
import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private String Id;
    private String Description;
    private List<CloudPoint> Coordinates;//list of coordinates of the object according to the charging station’s coordinate system.

    /**
     * Constructor for LandMark.
     *
     * @param Id The ID of the landmark.
     * @param Description The description of the landmark.
     * @param Coordinates The coordinates of the landmark.
     */
    public LandMark(String Id, String Description, List<CloudPoint> Coordinates) {
        this.Id = Id;
        this.Description = Description;
        this.Coordinates = Coordinates;
    }

    /**
     * Returns the ID of the landmark.
     *
     * @return The ID of the landmark.
     */
    public String getId() {
        return Id;
    }   

    /**
     * Returns the description of the landmark.
     *
     * @return The description of the landmark.
     */
    public String getDescription() {
        return Description;
    }

    /**
     * Returns the coordinates of the landmark.
     *
     * @return The coordinates of the landmark.
     */
    public List<CloudPoint> getCoordinates() {
        return Coordinates;
    }

    public void updateCoordinates(List<CloudPoint> newCoordinates) {
        this.Coordinates = newCoordinates;
    }
}
