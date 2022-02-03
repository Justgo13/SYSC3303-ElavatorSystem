package SharedResources;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BoxTest {

	@Test
	void testBox() {
		Box box = new Box();
		
		assertTrue(box.checkEmpty());
		
		String inputString = "Testing Box";
		byte[] byteArray = inputString.getBytes();
		
		box.put(byteArray);
		
		assertFalse(box.checkEmpty());
		
		byte[] output = box.get();
		
		assertTrue(box.checkEmpty());
		
		String outputString = new String(output,0, output.length);  
		
		assertEquals(inputString, outputString);
		
	}

}
