package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FusionSlamTest {

    @Test
    void testTransformToGlobalCoordinates() {
        Pose currentPose = new Pose(1, 2, 45);
        CloudPoint localPoint = new CloudPoint(1, 0, 0);

        CloudPoint globalPoint = FusionSlam.transformToGlobalCoordinates(localPoint, currentPose);

        assertEquals(1.0, globalPoint.getX(), 0.01);
        assertEquals(3.0, globalPoint.getY(), 0.01);
    }

    @Test
    void testTransformMultiplePointsToGlobalCoordinates() {
        Pose currentPose = new Pose(2, 3, 90);
        List<CloudPoint> localPoints = Arrays.asList(
            new CloudPoint(1, 0, 0),
            new CloudPoint(0, 1, 0)
        );

        List<CloudPoint> globalPoints = FusionSlam.transformMultiplePointsToGlobalCoordinates(localPoints, currentPose);

        assertEquals(2.0, globalPoints.get(0).getX(), 0.01);
        assertEquals(4.0, globalPoints.get(0).getY(), 0.01);

        assertEquals(3.0, globalPoints.get(1).getX(), 0.01);
        assertEquals(3.0, globalPoints.get(1).getY(), 0.01);
    }
}
