package test;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import FloorSubsystem.Floor;

class FloorTest {

	@Test
	void checkFloorInitialization() {
		Floor floor = new Floor();
		Assert.assertEquals(false, floor.isUpButtonPressed());
		Assert.assertEquals(false, floor.isDownButtonPressed());
		Assert.assertEquals(false, floor.isUpButtonLamp());
		Assert.assertEquals(false, floor.isDownButtonLamp());
		Assert.assertEquals(0, floor.getFloorNumber());
	}
	
	@Test
	void checkFloorSets(){
		Floor floor = new Floor();
		floor.setFloorNumber(5);
		floor.setUpButtonPressed(true);
		floor.setUpButtonLamp(true);
		floor.setDownButtonPressed(true);
		floor.setDownButtonLamp(true);
		Assert.assertEquals(true, floor.isUpButtonPressed());
		Assert.assertEquals(true, floor.isDownButtonPressed());
		Assert.assertEquals(true, floor.isUpButtonLamp());
		Assert.assertEquals(true, floor.isDownButtonLamp());
		Assert.assertEquals(5, floor.getFloorNumber());
	}
}
