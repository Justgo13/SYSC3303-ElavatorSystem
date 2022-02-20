/**
 * 
 */
package Messages;

import java.util.ArrayList;

/**
 * The message created when an elevator arrives at the destination floor
 * 
 * @author Harjap Gill
 *
 */
public class ArrivalElevatorMessage extends Message {
	private static final long serialVersionUID = 1L;
	private int elevatorId;
	private int currentFloor;
	private ArrayList<Integer> floorBuffer;

	/**
	 * @param elevatorId A unique elevator ID
	 * @param currentFloor The current floor the elevator is on
	 * @param floorBuffer The buffer holding floors the elevator will visit
	 */
	public ArrivalElevatorMessage(int elevatorId, int currentFloor, ArrayList<Integer> floorBuffer) {
		super();
		this.elevatorId = elevatorId;
		this.currentFloor = currentFloor;
		this.floorBuffer = floorBuffer;
	}

	@Override
	public MessageTypes getMessageType() {
		// TODO Auto-generated method stub
		return MessageTypes.ARRIVAL_ELEVATOR_MESSAGE;
	}
	
	/**
	 * @return the elevatorId
	 */
	public int getElevatorId() {
		return elevatorId;
	}

	/**
	 * @return the currentFloor
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}

	/**
	 * @return the floorBuffer
	 */
	public ArrayList<Integer> getFloorBuffer() {
		return floorBuffer;
	}
	
	public String toString() {
		return new String("ArrivalElevatorMessage for elevator: " + this.elevatorId + " for floor: " + 
				this.currentFloor + " with floor buffer: " + this.floorBuffer);
	}

}
