package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Messages.AcceptFloorRequestMessage;
import Messages.ArrivalElevatorMessage;
import Messages.DeclineFloorRequestMessage;

import org.junit.jupiter.api.DisplayName;

import SchedulerSubsystem.SchedulerElevatorData;
import SchedulerSubsystem.SchedulerSystem;
import SharedResources.ByteBufferCommunicator;
import SharedResources.TimeoutException;

/**
 * @author Kevin Quach and Shashaank Srivastava
 *
 */
class SchedulerSystemTest {
	
	private static SchedulerSystem schedulerSystem;
	private static ByteBufferCommunicator elevatorBufComm, floorBufComm;
	
	@BeforeAll
	static void setUp() throws Exception {
		int sendPort = 25;
		int receivePort = 26;
		floorBufComm = new ByteBufferCommunicator(sendPort, receivePort);
		
		sendPort = 73;
		receivePort =  74;
		elevatorBufComm = new ByteBufferCommunicator(sendPort, receivePort);
		schedulerSystem = new SchedulerSystem(elevatorBufComm, floorBufComm, 1);
	}

	@Test
	@DisplayName("Add elevator")
	void testAddElevators() {
		schedulerSystem = new SchedulerSystem(elevatorBufComm, floorBufComm, 1); // override what is done in setUp
		schedulerSystem.addElevators(1);
		assertEquals(2, schedulerSystem.getElevatorData().size());
	}
	
	@Test
	@DisplayName("Hard fault an elevator")
	void testHardFaultElevator() {
		schedulerSystem.hardFaultElevator(0);
		assertTrue(schedulerSystem.getElevatorData().get(0).getHardFaulted());
	}
	
	@Test
	@DisplayName("Update the state with all the different message types")
	void testUpdateElevators() {
		ArrayList<Integer> elevatorFloorBuffer = new ArrayList<>();
		elevatorFloorBuffer.add(4);
		AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(0, 0, 1, elevatorFloorBuffer);
		schedulerSystem.updateElevators(acceptMsg);
		
		assertEquals(schedulerSystem.getElevatorData().get(0).getDestinationFloor(), elevatorFloorBuffer);
		try {
			assertTrue(schedulerSystem.getRequestResponseTimed(0, System.currentTimeMillis()));
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DeclineFloorRequestMessage declineMsg = new DeclineFloorRequestMessage(0, 0, 1, elevatorFloorBuffer);
		schedulerSystem.updateElevators(declineMsg);
		
		new Thread("Cause notify") { //dummy thread just to cause an update and notifyall() after getElevatorData() is stuck waiting since there's no update
			public void run() {
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(-1, 0, -1, elevatorFloorBuffer);
				schedulerSystem.updateElevators(acceptMsg);
			}
		}.start();
		assertEquals(schedulerSystem.getElevatorData().get(0).getDestinationFloor(), elevatorFloorBuffer);
		
		try {
			assertFalse(schedulerSystem.getRequestResponseTimed(0, System.currentTimeMillis()));
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		ArrayList<Integer> emptyBuffer = new ArrayList<>();
		ArrivalElevatorMessage arrivalMsg = new ArrivalElevatorMessage(0, 4, emptyBuffer);
		schedulerSystem.updateElevators(arrivalMsg);
		
		SchedulerElevatorData data = schedulerSystem.getElevatorData().get(0);
		assertEquals(data.getDestinationFloor(), emptyBuffer);
		assertEquals(data.getCurrentFloor(), 4);
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
		try {
			assertTrue(schedulerSystem.getRequestResponseTimed(0, System.currentTimeMillis()));
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DeclineFloorRequestMessage declineMsg = new DeclineFloorRequestMessage(0, 1, 1, elevatorFloorBuffer);
		schedulerSystem.updateElevators(declineMsg);
		
		new Thread("Cause notify") { //dummy thread just to cause an update and notifyall() after getElevatorData() is stuck waiting since there's no update
			public void run() {
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(-1, 0, -1, elevatorFloorBuffer);
				schedulerSystem.updateElevators(acceptMsg);
			}
		}.start();
		assertEquals(schedulerSystem.getElevatorData().get(1).getDestinationFloor(), elevatorFloorBuffer);
		
		try {
			assertFalse(schedulerSystem.getRequestResponseTimed(0, System.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<Integer> emptyBuffer = new ArrayList<>();
		ArrivalElevatorMessage arrivalMsg = new ArrivalElevatorMessage(1, 4, emptyBuffer);
		schedulerSystem.updateElevators(arrivalMsg);

		SchedulerElevatorData data = schedulerSystem.getElevatorData().get(1);
		assertEquals(data.getDestinationFloor(), emptyBuffer);
		assertEquals(data.getCurrentFloor(), 4);
	}
}
