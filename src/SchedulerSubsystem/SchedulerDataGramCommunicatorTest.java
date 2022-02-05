package SchedulerSubsystem;

import org.junit.jupiter.api.Test;

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
	void testElevatorToFloorCommunicator() {
		SchedulerDataGramCommunicator dsg = new SchedulerDataGramCommunicator();
		String testString = "Hello, I love testing";
		
		byte[] packet = testString.getBytes();
		dsg.elevatorToFloorPut(packet);
		
		byte[] returned = dsg.elevatorToFloorGet();
		Assert.assertEquals(packet, returned);
	}
	
	@Test
	void testFloorToElevatorCommunicator() {
		SchedulerDataGramCommunicator dsg = new SchedulerDataGramCommunicator();
		String testString = "Hello, I love testing";
		
		byte[] packet = testString.getBytes();
		dsg.floorToElevatorPut(packet);
		
		byte[] returned = dsg.floorToElevatorGet();
		Assert.assertEquals(packet, returned);
	}	
}
