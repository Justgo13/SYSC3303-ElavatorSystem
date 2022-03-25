package Messages;

public class ElevatorTransientFaultMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float timeOfFault;
	private int elevatorID;
	private float timestamp;
	
	public ElevatorTransientFaultMessage(float timeOfFault, int elevatorID, float timestamp) {
		this.elevatorID = elevatorID;
		this.timeOfFault = timeOfFault;
		this.timestamp = timestamp;
	}

	@Override
	public MessageTypes getMessageType() {
		// TODO Auto-generated method stub
		return MessageTypes.ELEVATOR_TRANSIENT_FAULT;
	}

	/**
	 * @return the timeOfFault
	 */
	public float getTimeOfFault() {
		return timeOfFault;
	}

	/**
	 * @param timeOfFault the timeOfFault to set
	 */
	public void setTimeOfFault(float timeOfFault) {
		this.timeOfFault = timeOfFault;
	}

	/**
	 * @return the elevatorID
	 */
	public int getElevatorID() {
		return elevatorID;
	}

	/**
	 * @param elevatorID the elevatorID to set
	 */
	public void setElevatorID(int elevatorID) {
		this.elevatorID = elevatorID;
	}

	public float getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(float timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
