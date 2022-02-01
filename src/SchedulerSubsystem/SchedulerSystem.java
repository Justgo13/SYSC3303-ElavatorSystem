package SchedulerSubsystem;

import SharedResources.*;

/**
 * @author Kevin Quach
 * 
 *         For iteration 1, the scheduler subsystem is in charge of acting as a pipeline of data between the floor subsystem and the elevator subsystem.
 *         In the future, it should be multi-threaded in order to handle multiple elevators simultaneously.
 *
 */
public class SchedulerSystem {
	private static final SchedulerDataGramCommunicator floorCommunicator = new SchedulerDataGramCommunicator(5000);
	private static final SchedulerDataGramCommunicator elevatorCommunicator = new SchedulerDataGramCommunicator(6000, 5001);
	
	/** Iteration 1 sequence
	 * 1. floor reads events from file
	 * 2. floor sends inputs to scheduler
	 * 3. elevator makes calls to scheduler? ignore this
	 * 4. scheduler sends data back to elevator
	 * 5. elevator sends data back to scheduler
	 * 6. scheduler sends data back to floor
	 * 7. done
	 */
	public static void main(String[] args) {
		FloorDataMessageSerializable msg = floorCommunicator.receive();
		elevatorCommunicator.send(msg);
		msg = elevatorCommunicator.receive(); //technically, elevator subsystem could talk to port 5000 and floorCommunicator could be used here instead of 5001 and elevatorCommunicator
		floorCommunicator.send(msg);

		floorCommunicator.closeSocket();
		elevatorCommunicator.closeSocket();
	}

}
