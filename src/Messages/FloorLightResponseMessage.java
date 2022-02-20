/**
 * 
 */
package Messages;

import SharedResources.DirectionEnum;

/**
 * The message created to turn on floor lights
 * 
 * @author Michael Quach
 *
 */
public class FloorLightResponseMessage extends Message {
	private static final long serialVersionUID = 1L;
	
	private int elevatorID;
	private int currentFloor;
	private DirectionEnum direction;
	
	/**
	 * Constructs response message to light up corresponding floor lights
	 * 
	 * @param elevatorId - elevator that has arrived
	 * @param currentFloor - floor where elevator has arrived
	 * @param direction - next direction of elevator
	 */
	public FloorLightResponseMessage(int elevatorID, int currentFloor, DirectionEnum direction) {
		super();
		this.elevatorID = elevatorID;
		this.currentFloor = currentFloor;
		this.direction = direction;
	}

	/**
	 * @return the message type
	 */
	public MessageTypes getMessageType() {
		return MessageTypes.FLOOR_LIGHT_RESPONSE_MESSAGE;
	}
	
	/**
	 * @return the elevatorID
	 */
	public int getElevatorID() {
		return elevatorID;
	}

	/**
	 * @return the currentFloor
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}

	/**
	 * @return the next direction of elevator
	 */
	public DirectionEnum getDirection() {
		return direction;
	}
	
	/**
	 * Returns string representation of message
	 */
	public String toString() {
		switch(direction) {
		case DOWN_DIRECTION:
			return String.format("Elevator %d arrived at floor %d. Lighting down direction lamp.", elevatorID, currentFloor);
		case UP_DIRECTION:
			return String.format("Elevator %d arrived at floor %d. Lighting up direction lamp.", elevatorID, currentFloor);
		default:
			return String.format("Elevator %d arrived at floor %d. Lighting both direction lamps.", elevatorID, currentFloor);
		
		}
	}
}
