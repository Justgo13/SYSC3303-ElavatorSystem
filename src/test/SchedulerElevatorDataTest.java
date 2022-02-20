/**
 * 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import SchedulerSubsystem.SchedulerElevatorData;
import SharedResources.DirectionEnum;

/**
 * @author Kevin Quach and Shashaank Srivastava
 *
 */
class SchedulerElevatorDataTest {

	@Test
	@DisplayName("Construct elevators")
	void testConstructor() {
		SchedulerElevatorData e1 = new SchedulerElevatorData();
		ArrayList<Integer> destinations = new ArrayList<>();
		String newElevator = "	Current Floor: 1. Direction: IDLE_DIRECTION.\n		Destinations:";
		assertEquals(e1.toString(), newElevator);
		assertEquals(e1.getCurrentFloor(), 1);
		assertEquals(e1.getDestinationFloor(), destinations);
		assertEquals(e1.getDirection(), DirectionEnum.IDLE_DIRECTION);
		
		destinations.add(1);
		SchedulerElevatorData e2 = new SchedulerElevatorData(3, destinations, DirectionEnum.DOWN_DIRECTION);
		newElevator = "	Current Floor: 3. Direction: DOWN_DIRECTION.\n		Destinations: 1";
		assertEquals(e2.toString(), newElevator);
		assertEquals(e2.getCurrentFloor(), 3);
		assertEquals(e2.getDestinationFloor(), destinations);
		assertEquals(e2.getDirection(), DirectionEnum.DOWN_DIRECTION);
	}
	
	@Test
	@DisplayName("Set elevator data")
	void testSetData() {
		SchedulerElevatorData e1 = new SchedulerElevatorData();
		ArrayList<Integer> destinations = new ArrayList<>();
		destinations.add(5);
		destinations.add(10);
		e1.setData(4, destinations, DirectionEnum.UP_DIRECTION);
		String elevator = "	Current Floor: 4. Direction: UP_DIRECTION.\n		Destinations: 5 10";
		assertEquals(e1.toString(), elevator);
		assertEquals(e1.getCurrentFloor(), 4);
		assertEquals(e1.getDestinationFloor(), destinations);
		assertEquals(e1.getDirection(), DirectionEnum.UP_DIRECTION);
	}
	
	@Test
	@DisplayName("Set current floor")
	void testSetCurrentFloor() {
		SchedulerElevatorData e1 = new SchedulerElevatorData();
		e1.setCurrentFloor(7);
		String elevator = "	Current Floor: 7. Direction: IDLE_DIRECTION.\n		Destinations:";
		assertEquals(e1.toString(), elevator);
		assertEquals(e1.getCurrentFloor(), 7);
	}

	@Test
	@DisplayName("Set destinations")
	void testSetDestinationFloor() {
		SchedulerElevatorData e1 = new SchedulerElevatorData();
		ArrayList<Integer> destinations = new ArrayList<>();
		destinations.add(5);
		e1.setDestinationFloor(destinations);
		String elevator = "	Current Floor: 1. Direction: IDLE_DIRECTION.\n		Destinations: 5";
		assertEquals(e1.toString(), elevator);
		assertEquals(e1.getDestinationFloor(), destinations);
	}

	@Test
	@DisplayName("Set direction")
	void testSetDirection() {
		SchedulerElevatorData e1 = new SchedulerElevatorData();
		e1.setDirection(DirectionEnum.DOWN_DIRECTION);
		String elevator = "	Current Floor: 1. Direction: DOWN_DIRECTION.\n		Destinations:";
		assertEquals(e1.toString(), elevator);
		assertEquals(e1.getDirection(), DirectionEnum.DOWN_DIRECTION);
	}

}
