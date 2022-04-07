package Messages;

public class ElevatorHardFaultMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int elevatorID;
	private float timestamp;
	private boolean isFinalMessage;
	
	public ElevatorHardFaultMessage(int elevatorID, float timestamp, boolean isFinalMessage) {
		this.elevatorID = elevatorID;
		this.timestamp = timestamp;
		this.isFinalMessage= isFinalMessage;
	}

	@Override
	public MessageTypes getMessageType() {
		// TODO Auto-generated method stub
		return MessageTypes.ELEVATOR_HARD_FAULT;
	}

	public int getElevatorID() {
		return elevatorID;
	}

	public void setElevatorID(int elevatorID) {
		this.elevatorID = elevatorID;
	}

	public float getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(float timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the isFinalMessage
	 */
	public boolean getIsFinalMessage() {
		return isFinalMessage;
	}
}
