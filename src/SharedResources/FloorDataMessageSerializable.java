package SharedResources;
import java.io.Serializable;

/**
 * @author Michael Quach
 * 
 *         Might want to send this serialized to a byte array potentially
 *         Note that message type needs to be specified in byte array and prefixed before this message is serialized
 */
public class FloorDataMessageSerializable implements Serializable {
	private static final long serialVersionUID = 1L;
	private float timeStamp;
	private int floorNumber;
	private String direction;
	private int destinationNumber;

	/**
	 * Constructor to create FloorDataMessage
	 * 
	 * @param timeStamp of the message
	 * @param floorNumber indicating the floor of the message
	 * @param direction to set direction of the message
	 * @param destinationNumber destination floor of the message
	 */
	public FloorDataMessageSerializable(float timeStamp, int floorNumber, String direction, int destinationNumber) {
		this.timeStamp = timeStamp;
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.destinationNumber = destinationNumber;
	}

	/**
	 * toString method to print contents of floor data message
	 * 
	 * @return a string depicting the contents of the message
	 */
	public String toString() {
		return String.format("Floor message: \n	Time: %.2f\n	Floor: %d\n	Direction: %s\n	Car Button: %d", timeStamp, floorNumber, direction, destinationNumber);
	}
	
	/**
	 * 
	 * @return The timestamp as a float
	 */
	public float getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * 
	 * @return The current floor number in the message
	 */
	public int getFloorNumber() {
		return floorNumber;
	}
	
	/**
	 * 
	 * @return The direction (up/down) in the message
	 */
	public String getDirection() {
		return direction;
	}
	
	/**
	 * 
	 * @return The destination floor of the user request
	 */
	public int getDestinationNumber() {
		return destinationNumber;
	}
}
