package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * TickBroadcast is a broadcast message that contains the current tick number.
 * Sent by: TimeService
 * Handled by: All relevant services
 */

public class TickBroadcast implements Broadcast {
    private final int tickNumber;

    public TickBroadcast(int tickNumber) {
        this.tickNumber = tickNumber;
    }

    public int getTickNumber() {
        return tickNumber;
    }
}