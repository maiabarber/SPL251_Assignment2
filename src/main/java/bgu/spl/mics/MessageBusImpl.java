package bgu.spl.mics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {

	private static class MessageBusImplHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private final Map<MicroService, BlockingQueue<Message>> queues;
    private final Map<Class<? extends Event>, Queue<MicroService>> eventSubscribers;
    private final Map<Class<? extends Broadcast>, List<MicroService>> broadcastSubscribers;
    private final Map<Event<?>, Future<?>> eventFutures;

	private MessageBusImpl() {
		this.queues = new ConcurrentHashMap<>();
        this.eventSubscribers = new ConcurrentHashMap<>();
        this.broadcastSubscribers = new ConcurrentHashMap<>();
        this.eventFutures = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return MessageBusImplHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubscribers.putIfAbsent(type, new LinkedList<>());
        synchronized (eventSubscribers.get(type)) {
            eventSubscribers.get(type).add(m);
        }
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastSubscribers.putIfAbsent(type, new ArrayList<>());
        synchronized (broadcastSubscribers.get(type)) {
            broadcastSubscribers.get(type).add(m);
        }
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
        synchronized (this){
            Future<T> future = (Future<T>) eventFutures.get(e);
            if (future != null) {
                future.resolve(result);
        } 
        }
		
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		List<MicroService> subscribers = broadcastSubscribers.get(b.getClass());
        if (subscribers != null) {
            for (MicroService m : subscribers) {
                BlockingQueue<Message> queue = queues.get(m);
                if (queue != null) {
                    queue.add(b);
                }
            }
        }
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Queue<MicroService> subscribers = eventSubscribers.get(e.getClass());
        if (subscribers == null || subscribers.isEmpty()) {
            return null;
        }
        synchronized (subscribers) {
            MicroService m = subscribers.poll();
            if (m != null) {
                BlockingQueue<Message> queue = queues.get(m);
                if (queue != null) {
                    Future<T> future = new Future<>();
                    eventFutures.put(e, future);
                    queue.add(e);
                    subscribers.add(m);
                    return future;
                }
            }
        }
        return null;
	}

	@Override
	public void register(MicroService m) {
        queues.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		queues.remove(m);
        eventSubscribers.values().forEach(queue -> queue.remove(m));
        broadcastSubscribers.values().forEach(list -> list.remove(m));
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> queue = queues.get(m);
        if (queue == null) {
            throw new IllegalStateException("MicroService not registered");
        }
        return queue.take();//ממתין ולא מחזיר עד שיש משהו בתור
    }
}
