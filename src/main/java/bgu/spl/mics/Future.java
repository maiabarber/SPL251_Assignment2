package bgu.spl.mics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	
	private T result;
	private final AtomicBoolean isResolved;
	private final Object lock = new Object();

	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		this.result = null;
		this.isResolved = new AtomicBoolean(false);
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	public T get() {
		synchronized (lock) {
			while (!isResolved.get()) {//while isResolved is false
				try {
					lock.wait();
				} catch (InterruptedException e) {
					//Thread.currentThread().interrupt(); //to check
					//לבדוק אם צריך את השורה מעל
				}
			}
			return result;
		}
	}
	
	/**
     * Resolves the result of this Future object.
     */
	public void resolve (T result) {
		synchronized (this) {
			if (!isResolved.get()) {
				isResolved.set(true);
				this.result = result;
				notifyAll();
			}
		}
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isResolved() {
		return isResolved.get();
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {
		long timeoutMS = unit.toMillis(timeout);
		long endTime = System.currentTimeMillis() + timeoutMS;
        synchronized (lock) {
			while (!isResolved.get() && System.currentTimeMillis() < endTime) {
                try {
                    lock.wait(timeoutMS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
			return isResolved.get() ? result : null;
        }
	}

}
