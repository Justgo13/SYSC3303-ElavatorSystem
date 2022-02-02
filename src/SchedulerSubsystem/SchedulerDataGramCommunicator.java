package SchedulerSubsystem;

import java.util.ArrayList;
import java.util.List;

import SharedResources.*;

/**
 * @author Kevin Quach, Michael Quach
 * 
 *         A class for sending and receiving Datagram packets to and from the scheduler.
 */
public class SchedulerDataGramCommunicator {
	private Box floorToElevatorBox;
	private Box elevatorToFloorBox;
	private List<byte[]> floorDataEntry = new ArrayList<byte[]>(); // list of floor requests where each entry is a byte array
	
	/**
	 * Create shared memory for each direction of communication
	 */
	public SchedulerDataGramCommunicator() {
		floorToElevatorBox = new Box();
		elevatorToFloorBox = new Box();
	}
	
	public void sendToElevator(byte[] data) {
		this.elevatorToFloorBox.put(data);
	}
	
	public byte[] receiveFromFloor() {
		return this.elevatorToFloorBox.get();
	}
	
	public void sendToFloor(byte[] data) {
		this.floorToElevatorBox.put(data);
	}
	
	public byte[] receiveFromElevator() {
		return this.floorToElevatorBox.get();
	}
	
	/**
	 * Method for adding to the floor data entry list
	 * @param floorData The floor message as bytes
	 */
	public void addFloorEntry(byte[] floorData) {
		this.floorDataEntry.add(floorData);
	}
	
	/**
	 * 
	 * @return A list of byte array where each entry is a floor request
	 */
	public List<byte[]> getFloorDataEntry() {
		return this.floorDataEntry;
	}
}

