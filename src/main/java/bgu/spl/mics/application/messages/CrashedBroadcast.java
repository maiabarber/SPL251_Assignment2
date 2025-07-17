package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;
/**
 * CrashedBroadcast is a broadcast message sent by a sensor to notify all other services
 * that the sender service has crashed.
 */
public class CrashedBroadcast implements Broadcast {
    private final String senderName;
    private final String description;
    private final int time;

    /**
     * Constructor for CrashedBroadcast.
     *
     * @param senderName The name of the service that has crashed.
     */
    public CrashedBroadcast(String senderName, String description, int time) {
        this.senderName = senderName;
        this.description = description;
        this.time = time;
    }

    /**
     * Gets the name of the service that sent this broadcast.
     *
     * @return The sender's name.
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Gets the description of the crash.
     *
     * @return The crash description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the time of the crash.
     *
     * @return The crash time.
     */
    public int getTime() {
        return time;
    }
}

