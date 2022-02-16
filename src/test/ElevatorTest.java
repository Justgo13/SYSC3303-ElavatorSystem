package test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.Elevator;
import ElevatorSubsystem.ElevatorSystem;
import Messages.Message;
import Messages.MessageTypes;
import Messages.ServiceFloorRequestMessage;
import SharedResources.ByteBufferCommunicator;
import SharedResources.SerializeUtils;

class ElevatorTest {

	@Test
	@DisplayName("Elevator initialization")
	void testElevator() {
		int elevatorId = 1;
		boolean doorOpen = false;
		
		Elevator elevator = new Elevator(elevatorId, doorOpen);
		
		assertEquals(elevatorId, elevator.getElevatorId());
		assertFalse(elevator.getDoorOpen());
		
		elevator.setDoorOpen(true);
		
		assertTrue(elevator.getDoorOpen());
		
	}
	
	@Test
	@DisplayName("Elevators system accepts a floor request")
	void testElevatorAcceptFloorRequest() {
		Elevator elevator1 = new Elevator(1, false);

		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
	
		elevators.add(elevator1);
		
		ByteBufferCommunicator bufferCommunicator = new ByteBufferCommunicator();
		
		Thread elevatorSystem = new Thread(new ElevatorSystem(bufferCommunicator, elevators));
		elevatorSystem.start();
		
		// Test
		Message msg = new ServiceFloorRequestMessage(5, 10, "Up", 1);
		try {
			byte[] msgBytes = SerializeUtils.serialize(msg);
			bufferCommunicator.putRequestBuffer(msgBytes);
			
			assertTrue(bufferCommunicator.responseBufferEmpty());
			byte[] confirmationMsg = bufferCommunicator.getResponseBuffer();
			
			Message confirmation = SerializeUtils.deserialize(confirmationMsg);
			assertEquals(confirmation.getMessageType(), MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
