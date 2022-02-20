/**
 * 
 */
package SharedResources;

/**
 * A shared class for storing and retrieving information from message buffers
 * 
 * @author Jason Gao
 */
public class ByteBufferCommunicator {
	private ByteBuffer requestBuffer;
	private ByteBuffer responseBuffer;
	
	public ByteBufferCommunicator() {
		this.requestBuffer = new ByteBuffer();
		this.responseBuffer = new ByteBuffer();
	}

	/**
	 * @return the requestBuffer
	 */
	public byte[] getRequestBuffer() {
		return this.requestBuffer.get();
	}

	/**
	 * @param requestBuffer the requestBuffer to set
	 */
	public void putRequestBuffer(byte[] request) {
		this.requestBuffer.put(request);
	}

	/**
	 * @return the responseBuffer
	 */
	public byte[] getResponseBuffer() {
		return this.responseBuffer.get();
	}

	/**
	 * @param responseBuffer the responseBuffer to set
	 */
	public void putResponseBuffer(byte[] response) {
		this.responseBuffer.put(response);;
	}
	
	/**
	 * Check if request buffer is empty
	 * @return True if empty, false otherwise
	 */
	public boolean requestBufferEmpty() {
		return this.requestBuffer.checkEmpty();
	}
	
	/**
	 * Check if response buffer is empty
	 * @return True if empty, false otherwise
	 */
	public boolean responseBufferEmpty() {
		return this.responseBuffer.checkEmpty();
	}
}
