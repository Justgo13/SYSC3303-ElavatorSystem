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
	private Box floorToElevatorBox; // a box where floor is producer and elevator is consumer
	private Box elevatorToFloorBox; // a box where box is producer and floor is consumer
	
	/**
	 * Create shared memory for each direction of communication
	 */
	public SchedulerDataGramCommunicator() {
		floorToElevatorBox = new Box();
		elevatorToFloorBox = new Box();
	}
	
	/**
	 * Add data to the elevator to floor box
	 * 
	 * @param data to be added to the box
	 */
	public void elevatorToFloorPut(byte[] data) {
		this.elevatorToFloorBox.put(data);
	}
	
	/**
	 * Retrieve data from the elevator to floor box
	 *  
	 * @return data that was stored in the box
	 */
	public byte[] elevatorToFloorGet() {
		return this.elevatorToFloorBox.get();
	}
	
	/**
	 * Add data to the floor to elevator box
	 * 
	 * @param data to be added to the box
	 */
	public void floorToElevatorPut(byte[] data) {
		this.floorToElevatorBox.put(data);
	}
	
	/**
	 * Retrieve data from the floor to elevator box
	 *  
	 * @return data that was stored in the box
	 */
	public byte[] floorToElevatorGet() {
		return this.floorToElevatorBox.get();
	}
}

