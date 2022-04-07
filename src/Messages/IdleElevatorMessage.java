package Messages;

public class IdleElevatorMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int elevatorID;

	public IdleElevatorMessage(int elevatorID) {
		super();
		this.setElevatorID(elevatorID);
	}

	@Override
	public MessageTypes getMessageType() {
		// TODO Auto-generated method stub
		return MessageTypes.IDLE_ELEVATOR_MESSAGE;
	}

	public int getElevatorID() {
		return elevatorID;
	}

	public void setElevatorID(int elevatorID) {
		this.elevatorID = elevatorID;
	}
	
	
}