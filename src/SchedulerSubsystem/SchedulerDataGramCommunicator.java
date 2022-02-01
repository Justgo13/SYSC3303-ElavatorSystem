package SchedulerSubsystem;
/**
 * @author Kevin Quach, Michael Quach
 * 
 *         A class for sending and receiving UDP Datagram packets to and from the scheduler.
 *         Behaves as a server.
 *		   Currently comprised of code from the SimpleEchoServer.java example.
 */

import java.io.*;
import java.net.*;
import java.util.Arrays;
import SharedResources.*;

public class SchedulerDataGramCommunicator {
	private Box floorToElevatorBox;
	private Box elevatorToFloorBox;
	
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
}

