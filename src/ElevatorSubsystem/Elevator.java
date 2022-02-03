/**
 * 
 */
package ElevatorSubsystem;

/**
 * @author Harjap Gill
 * 
 * Elevator class that holds the state of an elevator
 *
 */
public class Elevator {
	private boolean doorOpen;
	private int elevatorId;
	
	public Elevator(int id, boolean doorOpen) {
		this.elevatorId = id;
		this.doorOpen = doorOpen;
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

}
