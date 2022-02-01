package FloorSubsystem;
import java.io.IOException;
import java.util.ArrayList;
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
	private final FloorDataParser parser = new FloorDataParser(); // reference to the floor data parser
	private final FloorDataGramCommunicator communicator = new FloorDataGramCommunicator(); // reference to the floor
																							// subsystem communicator
	private static List<byte[]> floorDataEntry = new ArrayList<byte[]>();; // list of floor entries where each entry is a byte array
	
	private SchedulerDataGramCommunicator sharedCommunicator;

	public FloorSystem(String floorDataFilename, SchedulerDataGramCommunicator sharedCommunicator) {
		this.sharedCommunicator = sharedCommunicator;
		parser.parseFile(floorDataFilename);
	}

	/**
	 * Sends the floor requests to the scheduler
	 */
	public void sendFloorData() {
		for (byte[] floorEntry : floorDataEntry) {
			communicator.send(floorEntry);
			communicator.receive();
		}
	}

	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		String floorDataFilename = "floorData.txt";
		FloorSystem fs = new FloorSystem(floorDataFilename);
		fs.sendFloorData();
	}*/
	
	/**
	 * Method for adding to the floor data entry list
	 * @param floorData
	 */
	public static void addFloorEntry(byte[] floorData) {
		floorDataEntry.add(floorData);
	}

	@Override
	public void run() {
		//Assume that for iteration 1, each message sent by the floor will eventually be received again
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