package SharedResources;

import java.io.IOException;

/**
 * @author Michael Quach
 * 
 *         Holds an area of shared memory to be accessed with mutual exclusion.
 */
public class Box {
	private byte[] contents;
	private boolean empty;
	
	public Box() {
		contents = null;
		empty = true;
	}
	
	/**
	 * Puts an item in the box as long as it is empty
	 * @param item the Datagram packet to store
	 */
	public synchronized void put(byte[] item) {
		System.out.println("Empty" + empty);
		while (!empty) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		contents = item;
		empty = false;
		notifyAll();
	}
	
	/**
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
		byte[] item = contents;
		contents = null;
		empty = true;
		notifyAll();
		return item;
	}
	
	/**
	 * @return True if the box is empty, false otherwise
	 */
	public synchronized boolean checkEmpty() {
		return empty;
	}
}
