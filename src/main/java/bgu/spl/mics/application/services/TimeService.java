package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

public class TimeService extends MicroService {

    private final int tickTime;
    private final int duration;

    public TimeService(int tickTime, int duration) {
        super("TimeService");
        this.tickTime = tickTime;
        this.duration = duration;//לבדוק אם להוסיף שדה של currentTick
    }

    @Override
    protected void initialize() {
        // הפעלת Thread שמנהל את ה-Ticks
        //new Thread(() -> {//לוודא אם ליצור פה תרד
            try {
                for (int currentTick = 0; currentTick <= duration; currentTick++) {
                    sendBroadcast(new TickBroadcast(currentTick));
                    Thread.sleep(tickTime * 1000L);
                }
                sendBroadcast(new TerminatedBroadcast(this.getName()));
                terminate();
            } catch (InterruptedException e) {// תפיסת חריגה להשליםםם- פה משתנה הדגל לfalse
            }
        }//).start();
    }

//}


