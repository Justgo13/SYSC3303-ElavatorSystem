package SharedResources;

public class Box {
	private byte[] contents;
	private boolean empty;
	
	public Box() {
		contents = null;
		empty = true;
	}
	
	public synchronized void put(byte[] item) {
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
	
	public synchronized boolean checkEmpty() {
		return empty;
	}
}
