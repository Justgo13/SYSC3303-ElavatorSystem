package FloorSubsystem;
/**
 * 
 */

/**
 * Floor class to hold state of a floor
 * 
 * @author jgao2
 *
 */
public class Floor {
	private boolean upButtonPressed, downButtonPressed, upButtonLamp, downButtonLamp;
	private int floorNumber;
	
	public Floor() {
		this.upButtonPressed = false;
		this.downButtonPressed = false;
		this.upButtonLamp = false;
		this.downButtonLamp = false;
	}

	/**
	 * @return the upButtonPressed
	 */
	public boolean isUpButtonPressed() {
		return upButtonPressed;
	}

	/**
	 * @param upButtonPressed the upButtonPressed to set
	 */
	public void setUpButtonPressed(boolean upButtonPressed) {
		this.upButtonPressed = upButtonPressed;
	}

	/**
	 * @return the downButtonPressed
	 */
	public boolean isDownButtonPressed() {
		return downButtonPressed;
	}

	/**
	 * @param downButtonPressed the downButtonPressed to set
	 */
	public void setDownButtonPressed(boolean downButtonPressed) {
		this.downButtonPressed = downButtonPressed;
	}

	/**
	 * @return the upButtonLamp
	 */
	public boolean isUpButtonLamp() {
		return upButtonLamp;
	}

	/**
	 * @param upButtonLamp the upButtonLamp to set
	 */
	public void setUpButtonLamp(boolean upButtonLamp) {
		this.upButtonLamp = upButtonLamp;
	}

	/**
	 * @return the downButtonLamp
	 */
	public boolean isDownButtonLamp() {
		return downButtonLamp;
	}

	/**
	 * @param downButtonLamp the downButtonLamp to set
	 */
	public void setDownButtonLamp(boolean downButtonLamp) {
		this.downButtonLamp = downButtonLamp;
	}

	/**
	 * @return the floorNumber
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

	/**
	 * @param floorNumber the floorNumber to set
	 */
	public void setFloorNumber(int floorNumber) {
		this.floorNumber = floorNumber;
	}
	
	
	
	
}
