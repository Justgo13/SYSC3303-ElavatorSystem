package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import SharedResources.FloorDataMessageSerializable;

class FloorDataMessageSerializableTest {

	@Test
	void testFloorDataMessageSerializable() {
		
		float timeStamp = 0.101f;
		int floorNumber = 2;
		String direction = "Up";
		int destinationNumber = 5;
		
		FloorDataMessageSerializable msg = new FloorDataMessageSerializable(timeStamp, floorNumber, direction, destinationNumber);
		
		
		assertEquals(msg.getTimeStamp(), timeStamp);
		assertEquals(msg.getFloorNumber(), floorNumber);
		assertEquals(msg.getDirection(), direction);
		assertEquals(msg.getDestinationNumber(), destinationNumber);
		
	}

}
