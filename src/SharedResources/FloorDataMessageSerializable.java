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
	private byte direction;
	private int destinationNumber;

	public FloorDataMessageSerializable(float timeStamp, int floorNumber, byte direction, int destinationNumber) {
		this.timeStamp = timeStamp;
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.destinationNumber = destinationNumber;
	}

	/**
	 * toString method to print contents of floor data message
	 */
	public String toString() {
		return String.format("Time: %.2f\n	Floor: %d\n	Direction: %d\n	Car Button: %d\n", timeStamp, floorNumber, direction, destinationNumber);
	}
	
	public float getTimeStamp() {
		return timeStamp;
	}
	
	public int getFloorNumber() {
		return floorNumber;
	}
	
	public byte getDirection() {
		return direction;
	}
	
	public int getDestinationNumber() {
		return destinationNumber;
	}
}
