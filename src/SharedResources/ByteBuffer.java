package SharedResources;

import java.util.ArrayList;

/**
 * Holds an area of shared memory to be accessed with mutual exclusion. The
 * buffer holds byte arrays
 * 
 * @author Harjap Gill
 * @author Jason Gao
 */
public class ByteBuffer {
	private ArrayList<byte[]> queue;
	private boolean empty;

	/**
	 * Constructor for Buffer class
	 */
	public ByteBuffer() {
		queue = new ArrayList<byte[]>();
		empty = true;
	}

	/**
	 * Puts an item in the buffer as long as it is empty
	 * 
	 * @param item the byte array to store
	 */
	public synchronized void put(byte[] item) {
		while (!empty) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		queue.add(item);
		empty = false;
		notifyAll();
	}

	/**
	 * Gets the item in the buffer and empties the buffer
	 * 
	 * @return the byte array
	 */
	public synchronized byte[] get() {
		while (empty) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		byte[] item = queue.get(0);
		if (queue.isEmpty()) {
			empty = true;
		}
		notifyAll();
		return item;
	}

	/**
	 * Checks if the buffer has an object in it
	 * 
	 * @return True if the buffer is empty, false otherwise
	 */
	public synchronized boolean checkEmpty() {
		return empty;
	}
}
