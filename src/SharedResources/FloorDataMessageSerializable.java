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

	public FloorDataMessageSerializable(float timeStamp, int floorNumber, String direction, int destinationNumber) {
		this.timeStamp = timeStamp;
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.destinationNumber = destinationNumber;
	}

	/**
	 * toString method to print contents of floor data message
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
