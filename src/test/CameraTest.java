public class CameraTest {
    @Test
    void testCameraConstructor() {
        List<Map<String, Object>> cameraData = new ArrayList<>();
        Camera camera = new Camera(1, 10, cameraData);
        assertEquals(1, camera.getId());
        assertEquals(10, camera.getFrequency());
        assertEquals(STATUS.UP, camera.getStatus());
        assertEquals(cameraData, camera.getCameraData());
        assertEquals(new ArrayList<>(), camera.getDetectedObjectsList());
    }
    
    @Test
    void testCameraStatus() {
        List<Map<String, Object>> cameraData = new ArrayList<>();
        Camera camera = new Camera(1, 10, cameraData);
        assertEquals(STATUS.UP, camera.getStatus());
        camera.setStatus(STATUS.DOWN);
        assertEquals(STATUS.DOWN, camera.getStatus());
    }
    
    @Test
    void testCameraDetectedObjects() {
        List<Map<String, Object>> cameraData = new ArrayList<>();
        Camera camera = new Camera(1, 10, cameraData);
        StampedDetectedObjects detectedObjects = new StampedDetectedObjects(1, new ArrayList<>());
        camera.addDetectedObjects(detectedObjects);
        assertEquals(detectedObjects, camera.getLastDetectedObjects());
        assertEquals(Collections.singletonList(detectedObjects), camera.getDetectedObjectsList());
    }
    
    @Test
    void testPrepareCameraData() {
        Camera camera = new Camera(1, 2, mockCameraData());

        List<DetectedObject> objects = camera.getDetectedObjects(4);
        assertNotNull(objects);
        assertFalse(objects.isEmpty());
        assertEquals("Tree", objects.get(0).getDescription());
    }

    private List<Map<String, Object>> mockCameraData() {
        Map<String, Object> dataEntry = new HashMap<>();
        dataEntry.put("time", 4);
        dataEntry.put("detectedObjects", Arrays.asList(new DetectedObject("1", "Tree")));
        return Arrays.asList(dataEntry);
    }

    
}