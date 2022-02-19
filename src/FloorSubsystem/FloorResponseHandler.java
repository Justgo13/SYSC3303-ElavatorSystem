package FloorSubsystem;

import java.io.IOException;

import Messages.*;
import SharedResources.ByteBufferCommunicator;
import SharedResources.SerializeUtils;

/**
 * 
 * @author Michael Quach
 *
 */
public class FloorResponseHandler implements Runnable {
	private FloorSystem floorSystem;
	private ByteBufferCommunicator floorBufferCommunicator;
	
	/**
	 * Constructs floor response handler, thread to receive floor light response messages and light corresponding lamps
	 * Barebones for now, simply prints out which lamps will be lit
	 * 
	 * @param floorSystem - floor system thread associated
	 * @param floorBufferCommunicator - byte buffer communicator this receives response messages from
	 */
	public FloorResponseHandler(FloorSystem floorSystem, ByteBufferCommunicator floorBufferCommunicator) {
		this.floorSystem = floorSystem;
		this.floorBufferCommunicator = floorBufferCommunicator;
	}

	public void run() {
		while(true) {
			byte[] response = floorBufferCommunicator.getResponseBuffer();
			try {
				FloorLightResponseMessage message = (FloorLightResponseMessage) SerializeUtils.deserialize(response);
				System.out.println(message);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}		
	}
}
