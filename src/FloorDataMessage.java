import java.util.Arrays;

/**
 * @author Jason Gao
 * 
 *         A class for creating a floor message that will be sent to the
 *         scheduler
 */
public class FloorDataMessage {
	private static final int MESSAGE_BYTE_LENGTH = 14;
	private static final int HEADER_OFFSET = 0;
	private static final int TIMESTAMP_OFFSET = 1;
	private static final int CURRENT_FLOOR_OFFSET = 5;
	private static final int DIRECTION_OFFSET = 9;
	private static final int DESTINATION_FLOOR_OFFSET = 10;

	private byte[] message; // holds the byte representation of a floor message sent to the scheduler

	public FloorDataMessage() {
		this.message = new byte[MESSAGE_BYTE_LENGTH];
	}

	/**
	 * Adds the header to the floor message, the header contains information that
	 * the scheduler uses to differentiate between floor messages and other messages
	 */
	public void addHeader() {
		message[HEADER_OFFSET] = 0; // TODO replace with some actual header later
	}

	/**
	 * Adds the timestamp to the floor message
	 * 
	 * @param timestamp The timestamp float of when a user request was made
	 */
	public void addTimestamp(float timestamp) {
		byte[] timestampBytes = ByteParser.floatToBytes(timestamp);
		message = ByteParser.overwriteBytes(message, timestampBytes, TIMESTAMP_OFFSET);
	}

	/**
	 * Adds the current floor to the floor message
	 * 
	 * @param currFloor The current floor the request was sent from
	 */
	public void addCurrentFloor(int currFloor) {
		byte[] currFloorBytes = ByteParser.intToBytes(currFloor);
		message = ByteParser.overwriteBytes(message, currFloorBytes, CURRENT_FLOOR_OFFSET);
	}

	/**
	 * Adds the direction to the floor message
	 * 
	 * @param direction The direction is a string depicting the direction the
	 *                  elevator is heading
	 */
	public void addDirection(String direction) {
		if (direction.equalsIgnoreCase("up")) {
			message[DIRECTION_OFFSET] = 0;
		} else if (direction.equalsIgnoreCase("down")) {
			message[DIRECTION_OFFSET] = 1;
		} else {
			System.out.printf("Unknown direction %s\n", direction);
		}
	}

	/**
	 * Adds the destination floor to the floor message
	 * 
	 * @param destFloor The floor a user wants to go to
	 */
	public void addDestinationFloor(int destFloor) {
		byte[] desinationFloorBytes = ByteParser.intToBytes(destFloor);
		message = ByteParser.overwriteBytes(message, desinationFloorBytes, DESTINATION_FLOOR_OFFSET);
	}

	/**
	 * Converts the message to its float and int representation from a byte array
	 * 
	 * @param message The byte array message
	 */
	public static void printMessage(byte[] message) {
		String strMessage = "";

		// message[0] is the message header
		byte header = message[0];
		strMessage += header + " ";

		// message[1] to message[4] is the timestamp in seconds
		byte[] timestampBytes = Arrays.copyOfRange(message, 1, 1 + 4);
		float timestamp = ByteParser.bytesToFloat(timestampBytes);
		strMessage += timestamp + " ";

		// message[5] to message[8] is the current floor
		byte[] currFloorBytes = Arrays.copyOfRange(message, 5, 5 + 4);
		int currFloor = ByteParser.bytesToInt(currFloorBytes);
		strMessage += currFloor + " ";

		// message[9] is the elevator direction
		byte direction = message[9];
		strMessage += direction + " ";

		// message[10] to message[13] is the destination floor
		byte[] destFloorBytes = Arrays.copyOfRange(message, 10, 10 + 4);
		int destFloor = ByteParser.bytesToInt(destFloorBytes);
		strMessage += destFloor + " ";

		System.out.println(strMessage);

	}

	/**
	 * @return The message byte array
	 */
	public byte[] getMessage() {
		return message;
	}
}
