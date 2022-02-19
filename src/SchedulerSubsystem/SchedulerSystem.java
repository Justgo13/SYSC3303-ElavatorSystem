package SchedulerSubsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ElevatorSubsystem.Elevator;
import ElevatorSubsystem.ElevatorSystem;
import FloorSubsystem.FloorSystem;
import Messages.*;
import SharedResources.ByteBufferCommunicator;

/**
 * @author Kevin Quach, Michael Quach
 * 
 *         For iteration 1, the scheduler subsystem is in charge of acting as a pipeline of data between the floor subsystem and the elevator subsystem.
 *         In the future, it should be multi-threaded in order to handle multiple elevators simultaneously.
 *
 */
public class SchedulerSystem {
	private ByteBufferCommunicator elevatorBufferCommunicator;
	private ByteBufferCommunicator floorBufferCommunicator;
	private ArrayList<SchedulerElevatorData> elevatorData;
	private ArrayList<FloorDataMessage> requestData;	//stores floor request messages for now, might make floor request data objects later
	private Map<Integer, Boolean> requestResponses;
	
	public SchedulerSystem(ByteBufferCommunicator elevatorBufComm, ByteBufferCommunicator floorBufComm, int numElevators) {
		this.elevatorBufferCommunicator = elevatorBufComm;
		this.floorBufferCommunicator = floorBufComm;
		this.elevatorData = new ArrayList<SchedulerElevatorData>();
		this.requestData = new ArrayList<FloorDataMessage>();
		this.requestResponses = new HashMap<>();
		addElevators(numElevators);
	}
	
	public void addElevators(int numElevators) {
		for(int i = 0; i < numElevators; i++) {
			this.elevatorData.add(new SchedulerElevatorData());
		}
	}
	
	public void printElevatorState() {
		for(int i = 0; i < this.elevatorData.size(); i++) {
			System.out.println("Elevator " + (i + 1) + ": \n" + this.elevatorData.get(i));
		}
	}
	
	//Just need synchronized to ensure that we don't update at the same time that we retrieve the state of the elevators
	public synchronized void updateElevators(Message updateMessage) {
		//typecast and read updateMessage here
			//then update elevators accordingly
		switch(updateMessage.getMessageType()) {
		case ACCEPT_FLOOR_REQUEST_MESSAGE:
			AcceptFloorRequestMessage acceptMsg = (AcceptFloorRequestMessage) updateMessage;
			elevatorData.get(acceptMsg.getElevatorId()).setDestinationFloor(acceptMsg.getElevatorFloorBuffer());	//updates which floors the elevator will plan to visit, now that it has accepted
			//need more information attached to accept/decline message e.g. originating floor of request, direction
			requestResponses.put(acceptMsg.getRequestID(), true);
			//todo need to remove request from array once accepted
			break;
		case DECLINE_FLOOR_REQUEST_MESSAGE:
			DeclineFloorRequestMessage declineMsg = (DeclineFloorRequestMessage) updateMessage;
			requestResponses.put(declineMsg.getRequestID(), false);
			//todo need to tell request handler that request was not satisfied, no change in state
			break;
		case ARRIVAL_ELEVATOR_MESSAGE:
			ArrivalElevatorMessage arrivalMsg = (ArrivalElevatorMessage) updateMessage;
			elevatorData.get(arrivalMsg.getElevatorId()).setCurrentFloor(arrivalMsg.getCurrentFloor());
			elevatorData.get(arrivalMsg.getElevatorId()).setDestinationFloor(arrivalMsg.getFloorBuffer());
			//need more information attached to arrival message e.g. to set direction
			//elevatorData.get(arrivalMsg.getElevatorId()).setData(arrivalMsg.getCurrentFloor(), arrivalMsg.getFloorBuffer(), arrivalMsg.get);			
			break;
		default:
			System.out.println("Unexpected message type.");
			break;		
		}
		notifyAll();
	}
	
	//If there are many responses piled in the elevator response buffer, we should process all of them first before retrieving the state of the elevators
	public synchronized ArrayList<SchedulerElevatorData> getElevatorData() {
		while(!elevatorBufferCommunicator.responseBufferEmpty()) {	//Need to wait until all changes to elevator states are made before retrieving
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		notifyAll();
		return elevatorData;
	}
	
	public synchronized boolean getRequestResponse(int requestID) {
		while (requestResponses.get(requestID) == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("error moment");
				return false;
			}
		}
		notifyAll();
		boolean response = requestResponses.get(requestID);
		requestResponses.remove(requestID);
		return response;
	}
	
	//Just need to ensure we don't put at the same time we are retrieving, no need to wait for something else
	public synchronized void putFloorRequestMessage(FloorDataMessage request) {
		requestData.add(request);
	}
	
	//If there are many requests piled in the floor request buffer, we should add all of them first to our container, then retrieve it once done
	public synchronized ArrayList<FloorDataMessage> getFloorRequestData() {
		while(!floorBufferCommunicator.requestBufferEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		notifyAll();
		return requestData;
	}
	
	/** 
	 * Start the floor and elevator subsystems.
	 * 
	 * Iteration 1 sequence
	 * 1. floor reads events from file
	 * 2. floor sends inputs to scheduler
	 * 4. scheduler sends data back to elevator
	 * 5. elevator sends data back to scheduler
	 * 6. scheduler sends data back to floor
	 * 7. done
	 * 
	 * @param args A list of string args
	 */
	public static void main(String[] args) {
		/*SchedulerDataGramCommunicator communicator = new SchedulerDataGramCommunicator();
		Thread floorSystemThread = new Thread(new FloorSystem("floorData.txt", communicator), "Floor System");
		Thread elevatorSystemThread = new Thread(new ElevatorSystem(communicator), "Elevator System");
		
		floorSystemThread.start();
		elevatorSystemThread.start();*/
		
		ByteBufferCommunicator floorBufferCommunicator = new ByteBufferCommunicator();
		Thread floorSystem = new Thread(new FloorSystem("floorData.txt", floorBufferCommunicator));
		floorSystem.start();
		
		ByteBufferCommunicator elevatorBufferCommunicator = new ByteBufferCommunicator();
		Elevator elevator1 = new Elevator(0, false, elevatorBufferCommunicator, 0);
     
     	ArrayList<Elevator> elevators = new ArrayList<Elevator>();
     	elevators.add(elevator1);      
      
     	Thread elevatorSystem = new Thread(new ElevatorSystem(elevatorBufferCommunicator, elevators));
     	elevatorSystem.start();		
		
		SchedulerSystem schedulerSystem = new SchedulerSystem(elevatorBufferCommunicator, floorBufferCommunicator, elevators.size());
		Thread schedulerRequestHandler = new Thread(new SchedulerRequestHandler(elevatorBufferCommunicator, floorBufferCommunicator, schedulerSystem));
		Thread schedulerResponseHandler = new Thread(new SchedulerResponseHandler(schedulerSystem, elevatorBufferCommunicator));
		
		schedulerRequestHandler.start();
		schedulerResponseHandler.start();
		//scheduler.printElevatorState();
	}
}
