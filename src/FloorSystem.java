import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */

/**
 * @author jgao2
 *
 */
public class FloorSystem {
	private final FloorDataParser parser = new FloorDataParser(); // reference to the floor data parser
	private final FloorDataGramCommunicator communicator = new FloorDataGramCommunicator(); // reference to the floor subsystem communicator

	public static List<Map<String, String>> floorDataEntry; // list of floor entries where each entry is represented by a map

	public FloorSystem(String floorDataFilename) {
		floorDataEntry = new ArrayList<Map<String, String>>();
		parser.parseFile(floorDataFilename);
	}
	
	public void sendFloorData() {
		for (Map<String, String> floorEntry : floorDataEntry) {
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