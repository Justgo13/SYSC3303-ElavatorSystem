package SchedulerSubsystem;

import java.io.IOException;

import Messages.Message;
import SharedResources.ByteBufferCommunicator;
import SharedResources.SerializeUtils;

public class SchedulerResponseHandler implements Runnable {
	private SchedulerSystem schedulerSystem;
	private ByteBufferCommunicator byteBufferCommunicator;
	private Message responseMessage;
	
	public SchedulerResponseHandler(SchedulerSystem schedulerSystem, ByteBufferCommunicator byteBufferCommunicator) {
		this.schedulerSystem = schedulerSystem;
		this.byteBufferCommunicator = byteBufferCommunicator;
	}

	@Override
	public void run() {
		while (true) {
			byte[] elevatorResponseBytes = byteBufferCommunicator.getResponseBuffer();
			try {
				Message elevatorResponseMessage = SerializeUtils.deserialize(elevatorResponseBytes);
				schedulerSystem.updateElevators(elevatorResponseMessage);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
