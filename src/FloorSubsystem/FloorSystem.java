package FloorSubsystem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Messages.FloorDataMessage;
import Messages.Message;
import SharedResources.ByteBufferCommunicator;
import SharedResources.FaultType;
import SharedResources.SerializeUtils;

/**
 * @author Jason Gao
 * 
 *         The floor subsystem is in charge of sending user requests that it
 *         gets from file input to the scheduler
 *
 */
public class FloorSystem implements Runnable{
	private FloorDataParser parser = new FloorDataParser(); // reference to the floor data parser
	private static List<Message> floorDataEntry = new ArrayList<Message>(); // list of floor entries where each entry is a byte array
	private ByteBufferCommunicator floorBufferCommunicator;
	private ByteBufferCommunicator faultBufferCommunicator;
	private Floor floor;

	/**
	 * Constructs a FloorSystem.
	 * @param floorDataFilename the filename of the data used to simulate an elevator system.
	 * @param floorBufferCommunicator a reference to the Scheduler's communicator
	 */
	public FloorSystem(String floorDataFilename, ByteBufferCommunicator floorBufferCommunicator, ByteBufferCommunicator faultBufferCommunicator) {
		this.floorBufferCommunicator = floorBufferCommunicator;
		this.faultBufferCommunicator = faultBufferCommunicator;
		floor = new Floor(); 
		parser.parseFile(floorDataFilename);
	}
	
	/**
	 * Method for adding to the floor data entry list
	 * @param fdms The floor message as bytes
	 */
	public static void addFloorEntry(Message fdms) {
		floorDataEntry.add(fdms);
	}

	/**
	 * Sends each message to the scheduler and receives and prints a message back for each entry in (floorDataFilename).txt.
	 */
	@Override
	public void run() {
		//Assume that for iteration 1, each message sent by the floor will eventually be received again
		float timeZero = 0;
		for(int i = 0; i < floorDataEntry.size(); i++) {
			System.out.println("Sending message from Floor System to Scheduler.");
			if (i == 0) {
				FloorDataMessage msg = (FloorDataMessage) floorDataEntry.get(i);
				timeZero = msg.getTimeStamp();
			}
			
			
			// current message time
			FloorDataMessage currentMsg = (FloorDataMessage) floorDataEntry.get(i);
			float currentTime = currentMsg.getTimeStamp();
			
			try {
				Thread.sleep((long) (currentTime - timeZero));
				
				if (currentMsg.getFaultType() != FaultType.NO_FAULT) {
					faultBufferCommunicator.sendUDPMessage(SerializeUtils.serialize(currentMsg));
				} else {
					floorBufferCommunicator.sendUDPMessage(SerializeUtils.serialize(currentMsg));
				}
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			
//			try {
//				System.out.println("Floor System received message from Scheduler: \n" + SerializeUtils.deserialize(floorBufferCommunicator.getResponseBuffer()));
//			} catch (ClassNotFoundException | IOException e) {
//				e.printStackTrace();
//			}
		}		
	}
	
	public static void main(String[] args) {
//		int sendPort = 23;
//		int receivePort = 24;
//		ByteBufferCommunicator floorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
//		FloorSystem floorSystem = new FloorSystem("floorData.txt", floorBufferCommunicator);
//		Thread floorSystemThread = new Thread(floorSystem);	//TODO maybe make this thread be spawned by floor system itself
//		Thread floorResponseHandler = new Thread(new FloorResponseHandler(floorSystem, floorBufferCommunicator));
//		new Thread(floorBufferCommunicator).start();
//		
//
//		floorSystemThread.start();
//		floorResponseHandler.start();
	}
}