package ElevatorSubsystem;

import java.io.IOException;
import java.util.ArrayList;

import SchedulerSubsystem.SchedulerDataGramCommunicator;
import SharedResources.SerializeUtils;
import SharedResources.*;
import Messages.*;

/**
 * @author Michael Quach
 * 
 *         For iteration 1, the elevator subsystem receives and echoes back a
 *         message from and to the scheduler subsystem. Manages the elevators in
 *         a system.
 *
 */
public class ElevatorSystem implements Runnable {
	private Buffer getBuffer;
	private Buffer putBuffer;
	private ArrayList<Elevator> elevators;

	/**
	 * Constructs an ElevatorSystem.
	 * 
	 * @param communicator a reference to the Scheduler's communicator
	 */
	public ElevatorSystem(Buffer getBuffer, Buffer putBuffer, ArrayList<Elevator> elevators) {
		this.getBuffer = getBuffer;
		this.putBuffer = putBuffer;
		this.elevators = elevators;
	}

	/**
	 * Receives a message from the scheduler (from the floor), prints it, then sends
	 * it back to the scheduler (to the floor).
	 */
	@Override
	public void run() {
		while (true) {
			Message message = null;
			byte[] bytes = getBuffer.get();
			try {
				message = SerializeUtils.deserialize(bytes);
				
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			
			switch (message.getMessageType()) {
			
			case ServiceFloorRequestMessage:
				ServiceFloorRequestMessage serviceFloorRequestMessage = (ServiceFloorRequestMessage) message;
				Integer elevatorId = serviceFloorRequestMessage.getElevatorId();
				Message confirmationMessage = createConfirmationMessage(elevatorId, serviceFloorRequestMessage);
				
				byte[] confirmationMessageBytes = null;
				
				try {
					confirmationMessageBytes = SerializeUtils.serialize(confirmationMessage);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				putBuffer.put(confirmationMessageBytes);
				break;

			default:
				System.out.println("Elevator System does not handle messages of type: " + message.getMessageType());
				break;
			}
			
			
//			byte[] message = communicator.floorToElevatorGet();
//			try {
//				System.out.println(
//						"Elevator System received message from Scheduler: \n" + SerializeUtils.deserialize(message));
//			} catch (ClassNotFoundException | IOException e) {
//				e.printStackTrace();
//			}
//			System.out.println("Sending message from Elevator System to Scheduler.");
//			communicator.elevatorToFloorPut(message);
		}

	}

	private Message createConfirmationMessage(Integer elevatorId, ServiceFloorRequestMessage serviceFloorRequestMessage) {
		Message msg = null;
		for (Elevator elevator: elevators) {
			if (elevator.getElevatorId() == elevatorId) {
				boolean confirm = elevator.processServiceRequest(serviceFloorRequestMessage);
				if (confirm) {
					msg = new AcceptFloorRequestMessage(elevator.getElevatorId(), elevator.getCurrentFloor() , elevator.getFloorBuffer());
				} else {
					msg = new DeclineFloorRequestMessage(elevator.getElevatorId(), elevator.getCurrentFloor() , elevator.getFloorBuffer());
				}
				break;
			}
		}
		return msg;
	}
}
