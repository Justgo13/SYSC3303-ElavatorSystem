package Messages;

public class StartTransientFaultMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int elevatorID;

	public StartTransientFaultMessage(int elevatorID) {
		super();
		this.setElevatorID(elevatorID);
	}

	@Override
	public MessageTypes getMessageType() {
		// TODO Auto-generated method stub
		return MessageTypes.START_TRANSIENT_FAULT;
	}

	public int getElevatorID() {
		return elevatorID;
	}

	public void setElevatorID(int elevatorID) {
		this.elevatorID = elevatorID;
	}
	
	
}
