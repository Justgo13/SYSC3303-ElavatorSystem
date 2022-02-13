/**
 * 
 */
package ElevatorSubsystem;

import java.util.ArrayList;

import Messages.ServiceFloorRequestMessage;

/**
 * @author Harjap Gill
 * 
 * Elevator class that holds the state of an elevator
 *
 */
public class Elevator {
	private enum states { IDLE, MOVING, STOPPED, DOORS_OPEN, DOORS_CLOSED };
	private boolean doorOpen;
	private int elevatorId;
	private int currentFloor;
	// Buffer holding the floors this elevator is planning on servicing
	private ArrayList<Integer> floorBuffer = new ArrayList<Integer>();

	
	/**
	 * Constructor to create elevator object
	 * 
	 * @param id of elevator to create
	 * @param doorOpen boolean of elevator door state
	 */
	public Elevator(int id, boolean doorOpen) {
		this.elevatorId = id;
		this.doorOpen = doorOpen;
	}
	
	
	
	public boolean processServiceRequest(ServiceFloorRequestMessage serviceFloorRequestMessage) {
		return false;
	}
	
	/** 
	 * Set the state of the elevator's door
	 * 
	 * @param open True if door is open, false if not
	 */
	public void setDoorOpen(boolean open) {
		this.doorOpen = open;
	}
	
	/**
	 * @return the floorBuffer
	 */
	public ArrayList<Integer> getFloorBuffer() {
		return floorBuffer;
	}



	/**
	 * @param floorBuffer the floorBuffer to set
	 */
	public void setFloorBuffer(ArrayList<Integer> floorBuffer) {
		this.floorBuffer = floorBuffer;
	}



	/**
	 * @param elevatorId the elevatorId to set
	 */
	public void setElevatorId(int elevatorId) {
		this.elevatorId = elevatorId;
	}



	/**
	 * 
	 * @return the state of the door, True if open, False if closed
	 */
	public boolean getDoorOpen() {
		return this.doorOpen;
	}
	
	/**
	 * 
	 * @return the id of the elevator
	 */
	public int getElevatorId() {
		return this.elevatorId;
	}



	/**
	 * @return the currentFloor
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}



	/**
	 * @param currentFloor the currentFloor to set
	 */
	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

}
