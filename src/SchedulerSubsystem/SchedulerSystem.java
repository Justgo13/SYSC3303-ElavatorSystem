package SchedulerSubsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ElevatorSubsystem.Elevator;
import ElevatorSubsystem.ElevatorSystem;
import FloorSubsystem.FloorResponseHandler;
import FloorSubsystem.FloorSystem;
import Messages.*;
import SharedResources.ByteBufferCommunicator;

/**
 * @author Michael Quach, Kevin Quach
 * 
 *         For iteration 2, the scheduler system holds the state of the elevators, computed from arrival messages, accept/decline service request messages.
 *         It is also responsible for scheduler request and response handler threads which communicate to the floor and elevator systems.
 *
 */
public class SchedulerSystem {
	private ByteBufferCommunicator elevatorBufferCommunicator;
	private ByteBufferCommunicator floorBufferCommunicator;
	private ArrayList<SchedulerElevatorData> elevatorData;
	private Map<Integer, Boolean> requestResponses;
	
	/**
	 * 
	 * Constructs a scheduler system connected to the elevator and floor byte buffer communicators
	 * 
	 * @param elevatorBufComm - byte buffer communicator storing elevator system requests and response
	 * @param floorBufComm  - byte buffer communicator storing floor system requests and response
	 * @param numElevators - number of elevators
	 */
	public SchedulerSystem(ByteBufferCommunicator elevatorBufComm, ByteBufferCommunicator floorBufComm, int numElevators) {
		this.elevatorBufferCommunicator = elevatorBufComm;
		this.floorBufferCommunicator = floorBufComm;
		this.elevatorData = new ArrayList<SchedulerElevatorData>();
		this.requestResponses = new HashMap<>();
		addElevators(numElevators);
	}
	
	/**
	 * Adds number of elevators to keep track of. Used during instantiation of scheduler system.
	 * 
	 * @param numElevators - number of elevators to maintain their state.
	 */
	public void addElevators(int numElevators) {
		for(int i = 0; i < numElevators; i++) {
			this.elevatorData.add(new SchedulerElevatorData());
		}
	}
	
	/**
	 * Prints out state of each elevator kept track of.
	 */
	public void printElevatorState() {
		for(int i = 0; i < this.elevatorData.size(); i++) {
			System.out.println("Elevator " + (i + 1) + ": \n" + this.elevatorData.get(i));
		}
	}
	
	/**
	 * Parses elevator response message, and typecasts message accordingly. Updates state of corresponding elevator based on contents of message.
	 * 
	 * @param updateMessage - elevator response message being parsed
	 */
	public synchronized void updateElevators(Message updateMessage) {
		switch(updateMessage.getMessageType()) {
		case ACCEPT_FLOOR_REQUEST_MESSAGE:
			AcceptFloorRequestMessage acceptMsg = (AcceptFloorRequestMessage) updateMessage;
			elevatorData.get(acceptMsg.getElevatorId()).setDestinationFloor(acceptMsg.getElevatorFloorBuffer());	//updates which floors the elevator will plan to visit, now that it has accepted
			requestResponses.put(acceptMsg.getRequestID(), true);	//Updates service request with elevator's response, via corresponding ID
			break;
		case DECLINE_FLOOR_REQUEST_MESSAGE:
			DeclineFloorRequestMessage declineMsg = (DeclineFloorRequestMessage) updateMessage;
			requestResponses.put(declineMsg.getRequestID(), false);	//Updates service request with elevator's response, via corresponding ID
			break;
		case ARRIVAL_ELEVATOR_MESSAGE:
			ArrivalElevatorMessage arrivalMsg = (ArrivalElevatorMessage) updateMessage;
			elevatorData.get(arrivalMsg.getElevatorId()).setCurrentFloor(arrivalMsg.getCurrentFloor());
			elevatorData.get(arrivalMsg.getElevatorId()).setDestinationFloor(arrivalMsg.getFloorBuffer());	
			break;
		default:
			System.out.println("Unexpected message type.");
			break;		
		}
		notifyAll();
	}
	
	/**
	 * Returns arraylist of elevator states. Waits on response buffer to be empty - should any outstanding elevator response messages be unparsed, retrieving the elevator states
	 * 	prematurely will yield a potentially inaccurate state.
	 * 
	 * @return arraylist of elevator states.
	 */
	public synchronized ArrayList<SchedulerElevatorData> getElevatorData() {
		while(!elevatorBufferCommunicator.isMessageListEmpty()) {	//Need to wait until all changes to elevator states are made before retrieving
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		notifyAll();
		return elevatorData;
	}
	
	/**
	 * Retrieves elevator's response (whether it accepted or declined the service request) according to requestID
	 * 
	 * @param requestID - ID of service request message and corresponding elevator response
	 * @return boolean corresponding to elevator response to service request
	 */
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
	
	/** 
	 * Start the floor, elevator, and scheduler subsystems.
	 * 
	 * Iteration 2 sequence
	 * 1. Floor reads events from file
	 * 2. Floor parses and stores events as messages
	 * 3. Floor sends messages to scheduler
	 * 4. Scheduler receives request from floor, and retrieves elevator states
	 * 5. Scheduler computes which elevator should service request
	 * 6. Scheduler sends service request message to Elevator
	 * 7. Elevator receives request message from Scheduler and directs Elevator accordingly, if possible. Elevator sends an accept or decline response appropriately to Scheduler.
	 * 8. Scheduler receives accept/decline response from Elevator. If declined, Scheduler recomputes which elevator should service request.
	 * 9. Repeat 3-8.
	 * 
	 * @param args A list of string args
	 */
	public static void main(String[] args) {
		// floor
		int sendPort = 23;
		int receivePort = 24;
		ByteBufferCommunicator floorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		FloorSystem floorSystem = new FloorSystem("floorData.txt", floorBufferCommunicator);
		Thread floorSystemThread = new Thread(floorSystem);	//TODO maybe make this thread be spawned by floor system itself
		Thread floorResponseHandler = new Thread(new FloorResponseHandler(floorSystem, floorBufferCommunicator));
		floorBufferCommunicator.start();
		

		floorSystemThread.start();
		floorResponseHandler.start();
		
		
		// elevator
		sendPort = 69;
		receivePort =  70;
		ByteBufferCommunicator elevatorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		Elevator elevator1 = new Elevator(0, false, elevatorBufferCommunicator, 1);
		elevatorBufferCommunicator.start();
	     
     	ArrayList<Elevator> elevators = new ArrayList<Elevator>();
     	elevators.add(elevator1);      
      
     	Thread elevatorSystem = new Thread(new ElevatorSystem(elevatorBufferCommunicator, elevators));
     	elevatorSystem.start();		
     	
     	//  scheduler <-> floor (thread 1)
		sendPort = 24;
		receivePort = 23;
		ByteBufferCommunicator floorBufferCommunicator2 = new ByteBufferCommunicator(sendPort, receivePort);
		
		// scheduler <-> elevator (thread 2)
		sendPort = 70;
		receivePort = 69;
		ByteBufferCommunicator elevatorBufferCommunicator2 = new ByteBufferCommunicator(sendPort, receivePort);
		
		
		
		SchedulerSystem schedulerSystem = new SchedulerSystem(elevatorBufferCommunicator2, floorBufferCommunicator2, elevators.size());
		Thread schedulerRequestHandler = new Thread(new SchedulerRequestHandler(elevatorBufferCommunicator2, floorBufferCommunicator2, schedulerSystem));
		Thread schedulerResponseHandler = new Thread(new SchedulerResponseHandler(elevatorBufferCommunicator2, floorBufferCommunicator2, schedulerSystem));
		
		schedulerRequestHandler.start();
		schedulerResponseHandler.start();
		
		
		
		floorBufferCommunicator2.start();
		elevatorBufferCommunicator2.start();
		
	}
}
