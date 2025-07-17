package bgu.spl.mics;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    @Test
    void testRegisterAndSendEvent() throws InterruptedException {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        MicroService service = new MicroService("TestService") {
            @Override
            protected void initialize() {}
        };

        messageBus.register(service);

        assertDoesNotThrow(() -> messageBus.awaitMessage(service));

        Event<Boolean> event = new DetectObjectsEvent(new StampedDetectedObjects(1, new ArrayList<>()));
        Future<Boolean> future = messageBus.sendEvent(event);

        assertNotNull(future);

        Message receivedMessage = messageBus.awaitMessage(service);
        assertEquals(event, receivedMessage);
    }

    @Test
    void testSendBroadcast() throws InterruptedException {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        MicroService service1 = new MicroService("Service1") {
            @Override
            protected void initialize() {}
        };
        MicroService service2 = new MicroService("Service2") {
            @Override
            protected void initialize() {}
        };

        messageBus.register(service1);
        messageBus.register(service2);

        messageBus.subscribeBroadcast(TickBroadcast.class, service1);
        messageBus.subscribeBroadcast(TickBroadcast.class, service2);

        TickBroadcast broadcast = new TickBroadcast(1);
        messageBus.sendBroadcast(broadcast);

        assertEquals(broadcast, messageBus.awaitMessage(service1));
        assertEquals(broadcast, messageBus.awaitMessage(service2));
    }

    @Test
    void testUnregister() throws InterruptedException {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        MicroService service = new MicroService("TestService") {
            @Override
            protected void initialize() {}
        };

        messageBus.register(service);
        messageBus.unregister(service);

        assertThrows(IllegalStateException.class, () -> messageBus.awaitMessage(service));
    }
}
