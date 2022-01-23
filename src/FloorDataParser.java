import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author jgao2
 *
 */
public class FloorDataParser {

	// indexes used to get the floor data values when file parsing
	private final int TIMESTAMP_INDEX = 0;
	private final int CURRENT_FLOOR_INDEX = 1;
	private final int DIRECTION_INDEX = 2;
	private final int DESTINATION_FLOOR_INDEX = 3;

	// hashmap keys used to retrieve specific floor entry values
	private final String TIMESTAMP = "timestamp";
	private final String CURRENT_FLOOR = "currentFloor";
	private final String DIRECTION = "direction";
	private final String DESTINATION_FLOOR = "destinationFloor";
	
	public void parseFile(String filename) {
		try {
			File floorDataFile = new File(filename);
			Scanner fileReader = new Scanner(floorDataFile);

			// iterate through the floor data file line by line
			while (fileReader.hasNextLine()) {
				String floorData = fileReader.nextLine(); // This looks like 14:05:15.0 2 Up 4
				List<String> lineAsArrayList = this.str2ArrayList(floorData); // Looks like [14:05:15.0, 2, Up, 4]

				// create a map of floor entry key value pairs from the extracted file line
				Map<String, String> floorEntry = new HashMap<String, String>();
				floorEntry.put(TIMESTAMP, lineAsArrayList.get(TIMESTAMP_INDEX));
				floorEntry.put(CURRENT_FLOOR, lineAsArrayList.get(CURRENT_FLOOR_INDEX));
				floorEntry.put(DIRECTION, lineAsArrayList.get(DIRECTION_INDEX));
				floorEntry.put(DESTINATION_FLOOR, lineAsArrayList.get(DESTINATION_FLOOR_INDEX));
				
				
				FloorSystem.floorDataEntry.add(floorEntry);
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * Convert a String into an ArrayList of Strings
	 * 
	 * @param stringToConvert The String to convert
	 * @return An ArrayList where each word in stringToConvert is an ArrayList
	 *         element
	 */
	private List<String> str2ArrayList(String stringToConvert) {
		String str[] = stringToConvert.split(" ");
		List<String> convertedArrayList = new ArrayList<String>();
		convertedArrayList = Arrays.asList(str);
		return convertedArrayList;
	}
}
