import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Jason Gao
 *
 *         A class for parsing floor data file input
 */
public class FloorDataParser {

	/**
	 * Opens a floor data file, parses each line into a byte array, then adds the
	 * byte array to the floor subsytem list of byte array messages
	 * 
	 * @param filename The floor data file name
	 */
	public void parseFile(String filename) {
		try {
			File floorDataFile = new File(filename);
			Scanner fileReader = new Scanner(floorDataFile);

			// iterate through the floor data file line by line
			while (fileReader.hasNextLine()) {
				String floorData = fileReader.nextLine(); // This looks like 14:05:15.0 2 Up 4
				List<String> lineAsArrayList = this.str2ArrayList(floorData, " "); // Looks like [14:05:15.0, 2, Up, 4]

				FloorDataMessage fdm = new FloorDataMessage();

				// add message header
				fdm.addHeader();

				// add timestamp
				float timestamp = this.timestampToFloat(lineAsArrayList.get(0));
				fdm.addTimestamp(timestamp);

				// add current floor
				int currFloor = Integer.parseInt(lineAsArrayList.get(1));
				fdm.addCurrentFloor(currFloor);

				// add elevator direction
				String direction = lineAsArrayList.get(2);
				fdm.addDirection(direction);

				// add destination floor
				int desinationFloor = Integer.parseInt(lineAsArrayList.get(3));
				fdm.addDestinationFloor(desinationFloor);

				// add the floor message to the floor subsytem list of byte array messages
				FloorSystem.floorDataEntry.add(fdm.getMessage());
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * Converts a timestamp from 14:05:15.0 (hh:mm:ss.mmm) to its float value in
	 * seconds
	 * 
	 * @param timestamp The string timestamp in the format of hh:mm:ss.mmm
	 * @return The number of seconds represented by the timestamp as a float
	 */
	private float timestampToFloat(String timestamp) {
		List<String> timestampAsArrayList = this.str2ArrayList(timestamp, ":"); // looks like [14, 05, 15.0]
		float timestampFloat = 0;

		float hours = Float.parseFloat(timestampAsArrayList.get(0)) * 3600;
		float minutes = Float.parseFloat(timestampAsArrayList.get(1)) * 60;
		float seconds = Float.parseFloat(timestampAsArrayList.get(2));

		timestampFloat += hours + minutes + seconds;
		return timestampFloat;
	}

	/**
	 * Convert a String into an ArrayList of Strings
	 * 
	 * @param stringToConvert The String to convert
	 * @param delimeter       A delimeter to split the string on
	 * @return An ArrayList where each word in stringToConvert is an ArrayList
	 *         element
	 */
	private List<String> str2ArrayList(String stringToConvert, String delimeter) {
		String str[] = stringToConvert.split(delimeter);
		List<String> convertedArrayList = new ArrayList<String>();
		convertedArrayList = Arrays.asList(str);
		return convertedArrayList;
	}
}
