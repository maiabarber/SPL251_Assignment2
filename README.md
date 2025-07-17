# SPL225 - Assignment 2  
## GurionRock Pro Max Ultra Over 9000: Perception and Mapping System  

### 👨‍💻 Authors  
- Maia Barber
- Yuval Pariente  

---

## 🧠 Overview  
This project implements a multi-threaded, event-driven robotic perception and mapping system using Java. The system simulates the GurionRock Pro Max Ultra Over 9000 – an advanced robotic vacuum cleaner equipped with various sensors including a Camera, LiDAR, IMU, and GPS.  

It integrates:
- Java concurrency concepts (threads, synchronization, futures)
- A MicroServices framework (with MessageBus)
- Sensor fusion (SLAM) logic
- Event-driven architecture

---

## 🎯 Project Structure  

### 1. MicroServices Framework  
Custom-built framework handling:
- Events and Broadcast messages
- Message queue per MicroService
- Round-robin dispatching
- Future object support  

### 2. Sensors & Services  
- `CameraService`: Detects objects and sends `DetectObjectsEvent`  
- `LiDarWorkerService`: Tracks objects and sends `TrackedObjectsEvent`  
- `FusionSlamService`: Integrates pose and object data to build map  
- `PoseService`: Sends pose information as `PoseEvent`  
- `TimeService`: Central tick broadcaster (`TickBroadcast`)  

---

## 📦 Packages  

- `bgu.spl.mics`: Core MicroService interfaces  
- `bgu.spl.mics.application.services`: All MicroService classes  
- `bgu.spl.mics.application.messages`: Custom event/broadcast message classes  
- `bgu.spl.mics.application.objects`: Core domain model (Camera, LiDAR, Pose, Landmark, etc.)  

---

## ⚙️ How It Works  

1. `TimeService` emits `TickBroadcast` every tick  
2. `CameraService` detects objects and sends `DetectObjectsEvent`  
3. `LiDarWorkerService` processes it and sends `TrackedObjectsEvent`  
4. `PoseService` sends current robot pose to `FusionSlamService`  
5. `FusionSlamService` updates the world map using pose + tracked object data  

All components synchronize on ticks and terminate gracefully or on sensor error.

---

## 🧪 Testing  
We followed **Test-Driven Development (TDD)**. JUnit tests were written for:  
- `MessageBusImpl`  
- `FusionSlamService` logic  
- `CameraService` and `LiDarWorkerService` event preparation  

Tests are under `src/test/java`.

---

## 📄 Input  
System is initialized using a **Configuration JSON** (path passed as CLI argument).  
Additional files:  
- Camera Data  
- LiDAR Data  
- Pose Data  

---

## 📝 Output  
A single file: `output_file.json`  
Includes:
- Final statistics (runtime, number of detections, landmarks, etc.)
- Mapped landmarks with global coordinates
- Error info if a crash occurred

---

## 🧰 Technologies Used  
- Java 8  
- Maven (build tool)  
- GSON (for JSON parsing)  
- JUnit (unit testing)

---

## 🚀 Running the Project  

### Build  
```bash
mvn clean install
# SPL251_Assignment2
