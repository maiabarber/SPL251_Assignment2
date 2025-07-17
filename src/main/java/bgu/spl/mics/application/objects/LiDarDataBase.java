package bgu.spl.mics.application.objects;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private List<StampedCloudPoints> cloudPoints;

    private static class LiDarDataBaseHolder {
        private static volatile LiDarDataBase instance = null;
    }

    private LiDarDataBase(List<StampedCloudPoints> cloudPoints) {
        this.cloudPoints = cloudPoints;
    }

    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        if (LiDarDataBaseHolder.instance == null) {
            synchronized (LiDarDataBaseHolder.class) { 
                if (LiDarDataBaseHolder.instance == null) {
                    List<StampedCloudPoints> parsedData = parseLiDarFile(filePath);
                    LiDarDataBaseHolder.instance = new LiDarDataBase(parsedData);
                }
            }
        }
        return LiDarDataBaseHolder.instance;
    }

    /**
     * Parses the LiDAR data file and returns a list of StampedCloudPoints.
     *
     * @param filePath The path to the LiDAR data file.
     * @return A list of StampedCloudPoints parsed from the LiDAR data file.
     */

    private static List<StampedCloudPoints> parseLiDarFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<StampedCloudPoints>>() {}.getType();
            List<StampedCloudPoints> parsedData = gson.fromJson(reader, listType);
            if (parsedData == null) {
                throw new IllegalStateException("Parsed LiDar data is null");
            }
            
            for (StampedCloudPoints point : parsedData) {
                if (point.getCloudPoints() == null) {
                    System.out.println("Null cloudPoints found for ID: " + point.getId() + ", Time: " + point.getTime());
                }
            }
            
            return parsedData;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or parse LiDAR file: " + filePath, e);
        }
    }

    public static LiDarDataBase getInstance(){
        return LiDarDataBaseHolder.instance;
    }
}
