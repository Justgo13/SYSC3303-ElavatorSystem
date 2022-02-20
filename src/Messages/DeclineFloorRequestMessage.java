/**
 * 
 */
package Messages;

import java.util.ArrayList;

/**
 * The message that the elevator sends when it declines to service a floor request
 * 
 * @author Harjap Gill
 *
 */
public class DeclineFloorRequestMessage extends Message {
	private static final long serialVersionUID = 1L;
	private int requestID;
	private int elevatorId;
	private int elevatorCurrentFloor;
	private ArrayList<Integer> elevatorFloorBuffer;
	
	/**
	 * @param elevatorId A unique elevator ID
	 * @param elevatorCurrentFloor The current floor the elevator is on
	 * @param elevatorFloorBuffer The buffer holding floors the elevator will visit
	 */
	public DeclineFloorRequestMessage(int requestID, int elevatorId, int elevatorCurrentFloor, ArrayList<Integer> elevatorFloorBuffer) {
		super();
		this.requestID = requestID;
		this.elevatorId = elevatorId;
		this.elevatorCurrentFloor = elevatorCurrentFloor;
		this.elevatorFloorBuffer = elevatorFloorBuffer;
	}

	@Override
	public MessageTypes getMessageType() {
		// TODO Auto-generated method stub
		return MessageTypes.DECLINE_FLOOR_REQUEST_MESSAGE;
	}
	
	/**
	 * @return the ID of the request declined
	 */
	public int getRequestID() {
		return requestID;
	}

	/**
	 * @return the elevatorId
	 */
	public int getElevatorId() {
		return elevatorId;
	}

	/**
	 * @param elevatorId the elevatorId to set
	 */
	public void setElevatorId(int elevatorId) {
		this.elevatorId = elevatorId;
	}

	/**
	 * @return the elevatorCurrentFloor
	 */
	public int getElevatorCurrentFloor() {
		return elevatorCurrentFloor;
	}

	/**
	 * @param elevatorCurrentFloor the elevatorCurrentFloor to set
	 */
	public void setElevatorCurrentFloor(int elevatorCurrentFloor) {
		this.elevatorCurrentFloor = elevatorCurrentFloor;
	}

	/**
	 * @return the elevatorFloorBuffer
	 */
	public ArrayList<Integer> getElevatorFloorBuffer() {
		return elevatorFloorBuffer;
	}

	/**
	 * @param elevatorFloorBuffer the elevatorFloorBuffer to set
	 */
	public void setElevatorFloorBuffer(ArrayList<Integer> elevatorFloorBuffer) {
		this.elevatorFloorBuffer = elevatorFloorBuffer;
	}

}
