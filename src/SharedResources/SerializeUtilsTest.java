package SharedResources;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class SerializeUtilsTest {

	@Test
	void testSerializeUtils() {
		
		float timeStamp = 0.101f;
		int floorNumber = 2;
		String direction = "Up";
		int destinationNumber = 5;
		
		FloorDataMessageSerializable originalObject = new FloorDataMessageSerializable(timeStamp, floorNumber, direction, destinationNumber);
		
		try {
			byte[] serializedObject = SerializeUtils.serialize(originalObject);
			
			FloorDataMessageSerializable newObject = SerializeUtils.deserialize(serializedObject);
			
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
