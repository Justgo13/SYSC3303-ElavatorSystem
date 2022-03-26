package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import Messages.FloorDataMessage;
import SharedResources.DirectionEnum;
import SharedResources.SerializeUtils;

class SerializeUtilsTest {

	@Test
	@DisplayName("Serialize and deserialize flow")
	void testSerializeUtils() {
		
		float timeStamp = 0.101f;
		int floorNumber = 2;
		DirectionEnum direction = DirectionEnum.UP_DIRECTION;
		int destinationNumber = 5;
		
		FloorDataMessage originalObject = new FloorDataMessage(timeStamp, floorNumber, direction, destinationNumber);
		
		try {
			byte[] serializedObject = SerializeUtils.serialize(originalObject);
			
			FloorDataMessage newObject = SerializeUtils.deserialize(serializedObject);
			
			assertEquals(originalObject.getTimeStamp(), newObject.getTimeStamp());
			assertEquals(originalObject.getFloorNumber(), newObject.getFloorNumber());
			assertEquals(originalObject.getDirection(), newObject.getDirection());
			assertEquals(originalObject.getDestinationNumber(), newObject.getDestinationNumber());
			
		} catch (IOException e) {
			fail("Error Occured");
		} catch (ClassNotFoundException e) {
			fail("Error Occured");
		}
		
	}

}
