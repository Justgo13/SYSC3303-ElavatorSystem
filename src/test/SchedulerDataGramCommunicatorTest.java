package test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import SchedulerSubsystem.SchedulerDataGramCommunicator;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.concurrent.TimeUnit;


import org.junit.Assert;

/**
 * 
 * @author Shashaank
 * 		Testing 
 *
 */
class SchedulerDataGramCommunicatorTest {

	@Test
	@DisplayName("Elevator to floor shared box flow")
	void testElevatorToFloorCommunicator() {
		SchedulerDataGramCommunicator dsg = new SchedulerDataGramCommunicator();
		String testString = "Hello, I love testing";
		
		byte[] packet = testString.getBytes();
		dsg.elevatorToFloorPut(packet);
		
		byte[] returned = dsg.elevatorToFloorGet();
		Assert.assertEquals(packet, returned);
	}
	
	@Test
	@DisplayName("Floor to elevator shared box flow")
	void testFloorToElevatorCommunicator() {
		SchedulerDataGramCommunicator dsg = new SchedulerDataGramCommunicator();
		String testString = "Hello, I love testing";
		
		byte[] packet = testString.getBytes();
		dsg.floorToElevatorPut(packet);
		
		byte[] returned = dsg.floorToElevatorGet();
		Assert.assertEquals(packet, returned);
	}	
}
