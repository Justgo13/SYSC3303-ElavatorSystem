package SchedulerSubsystem;

import java.io.IOException;

import Messages.*;
import SharedResources.ByteBufferCommunicator;
import SharedResources.DirectionEnum;
import SharedResources.SerializeUtils;

/**
 * 
 * @author Michael Quach
 *
 */
public class SchedulerResponseHandler implements Runnable {
	private SchedulerSystem schedulerSystem;
	private ByteBufferCommunicator elevatorBufferCommunicator;
	private ByteBufferCommunicator floorBufferCommunicator;
	
	/**
	 * Constructs SchedulerReponseHandler, a thread responsible for receiving elevator responses, updating the elevator states, and sending messages to the floor system accordingly
	 * 
	 * @param elevatorBufferCommunicator - Byte buffer communicator responsible for holding elevator requests and responses
	 * @param floorBufferCommunicator - Byte buffer communicator responsible for holding floor responses
	 * @param schedulerSystem - Scheduler system which stores states of elevators
	 */
	public SchedulerResponseHandler(ByteBufferCommunicator elevatorBufferCommunicator, ByteBufferCommunicator floorBufferCommunicator, SchedulerSystem schedulerSystem) {
		this.schedulerSystem = schedulerSystem;
		this.elevatorBufferCommunicator = elevatorBufferCommunicator;
		this.floorBufferCommunicator = floorBufferCommunicator;
	}
	
	public void parseArrivalMessage(ArrivalElevatorMessage message) {
		int elevatorID = message.getElevatorId();
		int currFloor = message.getCurrentFloor();
		
		DirectionEnum direction;
		
		if(!message.getFloorBuffer().isEmpty()) {
			if(message.getFloorBuffer().get(0) - currFloor > 0) {	//Check if next destined floor is above the current floor
				direction = DirectionEnum.UP_DIRECTION;
			}
			else if(message.getFloorBuffer().get(0) - currFloor < 0) {	//Check if next destined floor is below the current floor
				direction = DirectionEnum.DOWN_DIRECTION;
			}
			else {
				direction = DirectionEnum.IDLE_DIRECTION;	//Edge case that next destined floor is same floor - shouldn't occur
			}
		}
		else {	//There is no next destined floor
			direction = DirectionEnum.IDLE_DIRECTION;
		}
		sendFloorResponseMessage(elevatorID, currFloor, direction);
	}
	
	public void sendFloorResponseMessage(int elevatorID, int currFloor, DirectionEnum direction) {
		FloorLightResponseMessage floorResponseMessage = new FloorLightResponseMessage(elevatorID, currFloor, direction);
		try {
			byte[] floorResponseBytes = SerializeUtils.serialize((Message) floorResponseMessage);
			floorBufferCommunicator.putResponseBuffer(floorResponseBytes);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * Retrieves message from elevator buffer, updating state of elevators accordingly
	 * If message was an arrival message, a message is sent to the floor system to update their lights accordingly
	 */
	public void run() {
		while (true) {
			byte[] elevatorResponseBytes = elevatorBufferCommunicator.getResponseBuffer();
			try {
				Message elevatorResponseMessage = SerializeUtils.deserialize(elevatorResponseBytes);
				schedulerSystem.updateElevators(elevatorResponseMessage);
				
				if(elevatorResponseMessage.getMessageType() == MessageTypes.ARRIVAL_ELEVATOR_MESSAGE) {
					ArrivalElevatorMessage elevatorArrivalMessage = (ArrivalElevatorMessage) elevatorResponseMessage;
					parseArrivalMessage(elevatorArrivalMessage);
				}
				
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}

}
