package SchedulerSubsystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Messages.ArrivalElevatorMessage;
import Messages.ElevatorHardFaultMessage;
import Messages.ElevatorTransientFaultMessage;
import Messages.FloorDataMessage;
import Messages.Message;
import Messages.ServiceFloorRequestMessage;
import SharedResources.ByteBufferCommunicator;
import SharedResources.DirectionEnum;
import SharedResources.SerializeUtils;

public class SchedulerFaultHandler implements Runnable {
	private SchedulerSystem schedulerSystem;
	private ByteBufferCommunicator elevatorBufferCommunicator;
	private ByteBufferCommunicator floorFaultBufferCommunicator;
	
	/**
	 * Constructs SchedulerFaultHandler, a thread responsible for receiving fault messages, updating the elevator fault states, and sending messages to the elevator system accordingly
	 * 
	 * @param elevatorBufferCommunicator - Byte buffer communicator responsible for holding elevator requests and responses
	 * @param floorFaultBufferCommunicator - Byte buffer communicator responsible for holding floor fault messages
	 * @param schedulerSystem - Scheduler system which stores states of elevators
	 */
	public SchedulerFaultHandler(ByteBufferCommunicator elevatorBufferCommunicator, ByteBufferCommunicator floorFaultBufferCommunicator, SchedulerSystem schedulerSystem) {
		this.schedulerSystem = schedulerSystem;
		this.elevatorBufferCommunicator = elevatorBufferCommunicator;
		this.floorFaultBufferCommunicator = floorFaultBufferCommunicator;
	}
	
	public void parseFaultMessage(Message faultMessage) {
		switch(faultMessage.getMessageType()) {
		case ELEVATOR_HARD_FAULT:
			if (((ElevatorHardFaultMessage)faultMessage).getIsFinalMessage()) {
            	schedulerSystem.setFinalMessageArrived(true);
            }
//			ElevatorHardFaultMessage hfMessage = (ElevatorHardFaultMessage) faultMessage;
//			int elevatorID = hfMessage.getElevatorID();
//			schedulerSystem.hardFaultElevator(elevatorID);	//update state of elevator accordingly
			sendElevatorFaultMessage(faultMessage);			//forward fault message to elevator subsystem
			break;
		case ELEVATOR_TRANSIENT_FAULT:
			if (((ElevatorTransientFaultMessage)faultMessage).getIsFinalMessage()) {
            	schedulerSystem.setFinalMessageArrived(true);
            }
			sendElevatorFaultMessage(faultMessage);			//forward fault message to elevator subsystem
			break;
		default:
			//We received a message that wasn't a fault message, we shouldn't be here
			break;		
		}
	}
	
	public void sendElevatorFaultMessage(Message faultMessage) {
		try {
			byte[] faultMessageBytes = SerializeUtils.serialize((Message) faultMessage);
			elevatorBufferCommunicator.sendUDPMessage(faultMessageBytes);	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
        while (true) {
            byte[] messageBytes = floorFaultBufferCommunicator.getUDPMessage();
            
            try {
                Message faultMessage = (Message) SerializeUtils.deserialize(messageBytes);                
                parseFaultMessage(faultMessage);
                
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }		
	}
}
