package SchedulerSubsystem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import junit.framework.Assert;

class SchedulerDataGramCommunicatorTest {

	@Test
	void testSchedularDataGramCommunicator() {
		SchedulerDataGramCommunicator sdg = new SchedulerDataGramCommunicator();
		String testString = "Hello, I love testing";
		byte[] packet = testString.getBytes();
		sdg.sendToElevator(packet);
		byte[] returned = sdg.receiveFromElevator();
		org.junit.Assert.assertEquals(packet, returned);
		
		
	}

}
