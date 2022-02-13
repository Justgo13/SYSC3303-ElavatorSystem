package SharedResources;

import java.util.ArrayList;

/**
 * @author Harjap Gill
 * @author Jason Gao
 * 
 *         Holds an area of shared memory to be accessed with mutual exclusion.
 */
public class Buffer {
	private ArrayList<byte[]> queue;
	private boolean empty;
	
	/**
	 * Constructor for Box class
	 */
	public Buffer() {
		queue = new ArrayList<byte[]>();
		empty = true;
	}
	
	/**
	 * Puts an item in the buffer as long as it is empty
	 * 
	 * @param item the Datagram packet to store
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
	 * Gets the item in the box and empties the box
	 * 
	 * @return the available Datagram packet and remove it
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
	 * Checks if the box has an object in it
	 * 
	 * @return True if the box is empty, false otherwise
	 */
	public synchronized boolean checkEmpty() {
		return empty;
	}
}
