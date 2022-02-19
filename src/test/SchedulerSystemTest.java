package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Messages.AcceptFloorRequestMessage;
import Messages.ArrivalElevatorMessage;
import Messages.DeclineFloorRequestMessage;

import org.junit.jupiter.api.DisplayName;

import SchedulerSubsystem.SchedulerSystem;
import SharedResources.ByteBufferCommunicator;

/**
 * @author Kevin Quach
 *
 */
class SchedulerSystemTest {
	
	private SchedulerSystem schedulerSystem;
	private ByteBufferCommunicator elevatorBufComm, floorBufComm;
	
	@BeforeEach
	void setUp() throws Exception {
		elevatorBufComm = new ByteBufferCommunicator();
		floorBufComm = new ByteBufferCommunicator();
		schedulerSystem = new SchedulerSystem(elevatorBufComm, floorBufComm, 1);
	}

	@Test
	@DisplayName("Add elevator")
	void testAddElevators() {
		schedulerSystem.addElevators(1);
		assertEquals(schedulerSystem.getElevatorData().size(), 2);
	}
	
	@Test
	@DisplayName("Update the state with all the different message types")
	void testUpdateElevators() {
		ArrayList<Integer> elevatorFloorBuffer = new ArrayList<>();
		elevatorFloorBuffer.add(4);
		AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(0, 0, 1, elevatorFloorBuffer);
		schedulerSystem.updateElevators(acceptMsg);
		
		assertEquals(schedulerSystem.getElevatorData().get(0).getDestinationFloor(), elevatorFloorBuffer);
		assertTrue(schedulerSystem.getRequestResponse(0));
		
		
		DeclineFloorRequestMessage declineMsg = new DeclineFloorRequestMessage(0, 0, 1, elevatorFloorBuffer);
		schedulerSystem.updateElevators(declineMsg);
		
		assertEquals(schedulerSystem.getElevatorData().get(0).getDestinationFloor(), elevatorFloorBuffer);
		assertFalse(schedulerSystem.getRequestResponse(0));
		
		
		ArrayList<Integer> emptyBuffer = new ArrayList<>();
		ArrivalElevatorMessage arrivalMsg = new ArrivalElevatorMessage(0, 4, emptyBuffer);
		schedulerSystem.updateElevators(arrivalMsg);
		
		assertEquals(schedulerSystem.getElevatorData().get(0).getDestinationFloor(), emptyBuffer);
		assertEquals(schedulerSystem.getElevatorData().get(0).getCurrentFloor(), 4);
	}
	
	@Test
	@DisplayName("Update the state with all the different message types for a new elevator")
	void testUpdateAdditionalElevator() {
		schedulerSystem.addElevators(1);
		
		ArrayList<Integer> elevatorFloorBuffer = new ArrayList<>();
		elevatorFloorBuffer.add(4);
		AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(0, 1, 1, elevatorFloorBuffer);
		schedulerSystem.updateElevators(acceptMsg);
		
		assertEquals(schedulerSystem.getElevatorData().get(1).getDestinationFloor(), elevatorFloorBuffer);
		assertTrue(schedulerSystem.getRequestResponse(0));
		
		
		DeclineFloorRequestMessage declineMsg = new DeclineFloorRequestMessage(0, 1, 1, elevatorFloorBuffer);
		schedulerSystem.updateElevators(declineMsg);
		
		assertEquals(schedulerSystem.getElevatorData().get(1).getDestinationFloor(), elevatorFloorBuffer);
		assertFalse(schedulerSystem.getRequestResponse(0));
		
		
		ArrayList<Integer> emptyBuffer = new ArrayList<>();
		ArrivalElevatorMessage arrivalMsg = new ArrivalElevatorMessage(1, 4, emptyBuffer);
		schedulerSystem.updateElevators(arrivalMsg);
		
		assertEquals(schedulerSystem.getElevatorData().get(1).getDestinationFloor(), emptyBuffer);
		assertEquals(schedulerSystem.getElevatorData().get(1).getCurrentFloor(), 4);
	}

}
