package FloorSubsystem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason Gao
 * 
 *         The floor subsystem is in charge of sending user requests that it
 *         gets from file input to the scheduler
 *
 */
public class FloorSystem {
	private final FloorDataParser parser = new FloorDataParser(); // reference to the floor data parser
	private final FloorDataGramCommunicator communicator = new FloorDataGramCommunicator(); // reference to the floor
																							// subsystem communicator

	public static List<byte[]> floorDataEntry; // list of floor entries where each entry is a byte array

	public FloorSystem(String floorDataFilename) {
		floorDataEntry = new ArrayList<byte[]>();
		parser.parseFile(floorDataFilename);
	}

	/**
	 * Sends the floor requests to the scheduler
	 */
	public void sendFloorData() {
		for (byte[] floorEntry : floorDataEntry) {
			communicator.send(floorEntry);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String floorDataFilename = "floorData.txt";
		FloorSystem fs = new FloorSystem(floorDataFilename);
		fs.sendFloorData();
	}

}