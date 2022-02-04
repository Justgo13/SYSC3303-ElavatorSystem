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
	void testElevatorToFloor() {
		SchedulerDataGramCommunicator dsg = new SchedulerDataGramCommunicator();
		String testString = "Hello, I love testing";
		
		byte[] packet = testString.getBytes();
		dsg.sendToElevator(packet);
		
		byte[] returned = dsg.receiveFromFloor();
		Assert.assertEquals(packet, returned);
	}
	
	@Test
	void testFloorToElevator() {
		SchedulerDataGramCommunicator dsg = new SchedulerDataGramCommunicator();
		String testString = "Hello, I love testing";
		
		byte[] packet = testString.getBytes();
		dsg.sendToFloor(packet);
		
		byte[] returned = dsg.receiveFromElevator();
		Assert.assertEquals(packet, returned);
	}	
	
	@Test
	void tesFloorToElevatorWait() {
		SchedulerDataGramCommunicator dsg = new SchedulerDataGramCommunicator();
		String testString = "Hello, I love testing";
		
		byte[] returned = dsg.receiveFromElevator();

		byte[] packet = testString.getBytes();
		dsg.sendToFloor(packet);
		
		Assert.assertEquals(packet, returned);
	}	
}
