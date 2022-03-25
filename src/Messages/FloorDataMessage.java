package Messages;

import SharedResources.DirectionEnum;
import SharedResources.FaultType;

/**
 * The message created when sending a floor request
 * 
 * @author Michael Quach
 * 
 */
public class FloorDataMessage extends Message{
	private static final long serialVersionUID = 1L;
	private float timeStamp;
	private int floorNumber;
	private DirectionEnum direction;
	private int destinationNumber;
	private FaultType fault;

	/**
	 * Constructor to create FloorDataMessage
	 * 
	 * @param timeStamp of the message
	 * @param floorNumber indicating the floor of the message
	 * @param direction to set direction of the message
	 * @param destinationNumber destination floor of the message
	 * @param fault 
	 */
	public FloorDataMessage(float timeStamp, int floorNumber, DirectionEnum direction, int destinationNumber, int fault) {
		super();
		this.timeStamp = timeStamp;
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.destinationNumber = destinationNumber;
		
		switch (fault) {
		case 0:
			this.fault = FaultType.NO_FAULT;
			break;
		case 1:
			this.fault = FaultType.TRANSIENT;
			break;
		case 2:
			this.fault = FaultType.HARD;
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public MessageTypes getMessageType() {
		return MessageTypes.FLOOR_DATA_MESSAGE;
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
	public DirectionEnum getDirection() {
		return direction;
	}
	
	/**
	 * 
	 * @return The destination floor of the user request
	 */
	public int getDestinationNumber() {
		return destinationNumber;
	}
	
	/**
	 * 
	 * @return The elevator fault enum
	 */
	public FaultType getFaultType() {
		return fault;
	}
}
