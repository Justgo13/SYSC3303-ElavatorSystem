package ElevatorSubsystem;

import java.io.IOException;

import SchedulerSubsystem.SchedulerDataGramCommunicator;
import SharedResources.SerializeUtils;

public class ElevatorSystem implements Runnable {
	private SchedulerDataGramCommunicator communicator;
	
	public ElevatorSystem(SchedulerDataGramCommunicator communicator) {
		this.communicator = communicator;
	}

	@Override
	public void run() {

		byte[] message = communicator.receiveFromFloor();
		try {
			System.out.println("Elevator System received message from Scheduler: \n" + SerializeUtils.deserialize(message));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("Sending message from Elevator System to Scheduler.");
		communicator.sendToFloor(message);
	}
}
