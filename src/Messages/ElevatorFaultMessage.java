package Messages;

public class ElevatorFaultMessage extends Message{
	
	public enum FaultType{
		NO_FAULT(0), TRANSIENT(1), HARD(2);

	    public final int fId;

	    public FaultType(int id) {
	        this.fId = id;
	    }
	}
	
	private static final long serialVersionUID = 1L;
	private float timeStamp;
	private FaultType faultType;
	
	public ElevatorFaultMessage(float timeStamp, int faultNum) {
		super();
		this.timeStamp = timeStamp;
		this.faultType = FaultType(faultNum);
		
	}

	@Override
	public MessageTypes getMessageType() {
		// TODO Auto-generated method stub
		return null;
	}

}
