package ElevatorSubsystem;

import java.io.IOException;

import SchedulerSubsystem.SchedulerDataGramCommunicator;
import SharedResources.SerializeUtils;

/**
 * @author Michael Quach
 * 
 *         For iteration 1, the elevator subsystem receives and echoes back a message from and to the scheduler subsystem.
 *         Manages the elevators in a system.
 *
 */
public class ElevatorSystem implements Runnable {
	private SchedulerDataGramCommunicator communicator;
	private Elevator elevator = new Elevator(1, false);
	
	/**
	 * Constructs an ElevatorSystem.
	 * @param communicator a reference to the Scheduler's communicator
	 */
	public ElevatorSystem(SchedulerDataGramCommunicator communicator) {
		this.communicator = communicator;
	}
	
	/**
	 * Receives a message from the scheduler (from the floor), prints it, then sends it back to the scheduler (to the floor).
	 */
	@Override
	public void run() {
		//assumes that only one message is sent, which is to be sent back
		byte[] message = communicator.elevatorToFloorGet();
		try {
			System.out.println("Elevator System received message from Scheduler: \n" + SerializeUtils.deserialize(message));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("Sending message from Elevator System to Scheduler.");
		communicator.floorToElevatorPut(message);
	}
}
