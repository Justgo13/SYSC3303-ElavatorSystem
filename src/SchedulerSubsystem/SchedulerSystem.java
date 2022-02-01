package SchedulerSubsystem;

import ElevatorSubsystem.ElevatorSystem;
import FloorSubsystem.FloorSystem;

/**
 * @author Kevin Quach, Michael Quach
 * 
 *         For iteration 1, the scheduler subsystem is in charge of acting as a pipeline of data between the floor subsystem and the elevator subsystem.
 *         In the future, it should be multi-threaded in order to handle multiple elevators simultaneously.
 *
 */
public class SchedulerSystem {
	
	/** 
	 * Start the floor and elevator subsystems.
	 * 
	 * Iteration 1 sequence
	 * 1. floor reads events from file
	 * 2. floor sends inputs to scheduler
	 * 4. scheduler sends data back to elevator
	 * 5. elevator sends data back to scheduler
	 * 6. scheduler sends data back to floor
	 * 7. done
	 */
	public static void main(String[] args) {
		SchedulerDataGramCommunicator communicator = new SchedulerDataGramCommunicator();
		Thread floorSystemThread = new Thread(new FloorSystem("floorData.txt", communicator), "Floor System");
		Thread elevatorSystemThread = new Thread(new ElevatorSystem(communicator), "Elevator System");
		
		floorSystemThread.start();
		elevatorSystemThread.start();
	}
}
