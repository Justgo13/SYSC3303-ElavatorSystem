package SchedulerSubsystem;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import SharedResources.*;

/**
 * @author Kevin Quach, Michael Quach
 * 
 *         A class for sending and receiving Datagram packets to and from the scheduler.
 */
public class SchedulerDataGramCommunicator {
	private Box floorToElevatorBox;
	private Box elevatorToFloorBox;
	
	/**
	 * Create shared memory for each direction of communication
	 */
	public SchedulerDataGramCommunicator() {
		floorToElevatorBox = new Box();
		elevatorToFloorBox = new Box();
	}
	
	public void elevatorToFloorPut(byte[] data) {
		this.elevatorToFloorBox.put(data);
	}
	
	public byte[] elevatorToFloorGet() {
		return this.elevatorToFloorBox.get();
	}
	
	public void floorToElevatorPut(byte[] data) {
		this.floorToElevatorBox.put(data);
	}
	
	public byte[] floorToElevatorGet() {
		return this.floorToElevatorBox.get();
	}
}

