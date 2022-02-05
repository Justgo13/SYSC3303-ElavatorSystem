package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ElevatorSubsystem.Elevator;

class ElevatorTest {

	@Test
	void testElevator() {
		int elevatorId = 1;
		boolean doorOpen = false;
		
		Elevator elevator = new Elevator(elevatorId, doorOpen);
		
		assertEquals(elevatorId, elevator.getElevatorId());
		assertFalse(elevator.getDoorOpen());
		
		elevator.setDoorOpen(true);
		
		assertTrue(elevator.getDoorOpen());
		
	}

}
