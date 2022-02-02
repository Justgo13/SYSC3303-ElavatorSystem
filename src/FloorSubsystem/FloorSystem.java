package FloorSubsystem;
import java.io.IOException;
import java.util.List;

import SchedulerSubsystem.SchedulerDataGramCommunicator;
import SharedResources.SerializeUtils;

/**
 * @author Jason Gao
 * 
 *         The floor subsystem is in charge of sending user requests that it
 *         gets from file input to the scheduler
 *
 */
public class FloorSystem implements Runnable{
	private FloorDataParser parser;
	private SchedulerDataGramCommunicator sharedCommunicator;

	/**
	 * Constructs a FloorSystem.
	 * @param floorDataFilename the filename of the data used to simulate an elevator system.
	 * @param sharedCommunicator a reference to the Scheduler's communicator
	 */
	public FloorSystem(String floorDataFilename, SchedulerDataGramCommunicator sharedCommunicator) {
		this.sharedCommunicator = sharedCommunicator;
		parser = new FloorDataParser(sharedCommunicator);
		parser.parseFile(floorDataFilename);
	}
	
	/**
	 * Sends each message to the scheduler and receives and prints a message back for each entry in (floorDataFilename).txt.
	 */
	@Override
	public void run() {
		//Assume that for iteration 1, each message sent by the floor will eventually be received again
		List<byte[]> floorDataEntry = sharedCommunicator.getFloorDataEntry();
		for(int i = 0; i < floorDataEntry.size(); i++) {
			System.out.println("Sending message from Floor System to Scheduler.");
			sharedCommunicator.sendToElevator(floorDataEntry.get(i));
			
			try {
				System.out.println("Floor System received message from Scheduler: \n" + SerializeUtils.deserialize(sharedCommunicator.receiveFromElevator()));
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}		
	}
}