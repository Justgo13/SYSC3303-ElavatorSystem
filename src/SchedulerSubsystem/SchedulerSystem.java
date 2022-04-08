package SchedulerSubsystem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import ElevatorSubsystem.Elevator;
import ElevatorSubsystem.ElevatorSystem;
import FloorSubsystem.FloorResponseHandler;
import FloorSubsystem.FloorSystem;
import GUI.ElevatorFrame;
import Messages.*;
import SharedResources.*;

/**
 * @author Michael Quach, Kevin Quach
 * 
 *         For iteration 3, the scheduler system holds the state of the
 *         elevators, computed from arrival udp messages, accept/decline service
 *         request udp messages. It is also responsible for scheduler request
 *         and response handler threads which communicate to the floor and
 *         elevator systems via udp.
 *
 */
public class SchedulerSystem {
	private static final long HARD_FAULT_TIMEOUT_DURATION = 7000;
	private ByteBufferCommunicator elevatorBufferCommunicator;
	private ArrayList<SchedulerElevatorData> elevatorData;
	private Map<Integer, Boolean> requestResponses;
	private boolean elevatorsStateChanged;
	private long startingTime;
	private boolean finalMessageArrived;

	/**
	 * 
	 * Constructs a scheduler system connected to the elevator and floor byte buffer communicators
	 * 
	 * @param elevatorBufComm - byte buffer communicator storing elevator system requests and response
	 * @param floorBufComm    - byte buffer communicator storing floor system requests and response
	 * @param numElevators    - number of elevators
	 */
	public SchedulerSystem(ByteBufferCommunicator elevatorBufComm, ByteBufferCommunicator floorBufComm,
			int numElevators) {
		this.elevatorBufferCommunicator = elevatorBufComm;
		this.elevatorData = new ArrayList<SchedulerElevatorData>();
		this.requestResponses = new HashMap<>();
		addElevators(numElevators);
		this.elevatorsStateChanged = true; // so the first getElevatorData() attempt will work regardless of if the
											// state has changed yet, as it was only first created
	}

	/**
	 * Adds number of elevators to keep track of. Used during instantiation of scheduler system.
	 * 
	 * @param numElevators - number of elevators to maintain their state.
	 */
	public void addElevators(int numElevators) {
		for (int i = 0; i < numElevators; i++) {
			this.elevatorData.add(new SchedulerElevatorData());
		}
	}

	/**
	 * Prints out state of each elevator kept track of.
	 */
	public void printElevatorState() {
		for (int i = 0; i < this.elevatorData.size(); i++) {
			System.out.println("Elevator " + i + ": \n" + this.elevatorData.get(i));
		}
	}
	
	public void addStartingTime(long time) {
		this.startingTime = time;
	}
	
	public void setFinalMessageArrived(boolean bool) {
		finalMessageArrived = bool;
	}

	/**
	 * Parses elevator response message, and typecasts message accordingly. Updates
	 * state of corresponding elevator based on contents of message.
	 * 
	 * @param updateMessage - elevator response message being parsed
	 */
	public synchronized void updateElevators(Message updateMessage) {
		switch (updateMessage.getMessageType()) {
		case ACCEPT_FLOOR_REQUEST_MESSAGE:
			AcceptFloorRequestMessage acceptMsg = (AcceptFloorRequestMessage) updateMessage;
			elevatorData.get(acceptMsg.getElevatorId()).setDestinationFloor(acceptMsg.getElevatorFloorBuffer()); 
			// updates which floors the elevator will plan to visit, now that it has accepted
			requestResponses.put(acceptMsg.getRequestID(), true); // Updates service request with elevator's response, via corresponding ID
			elevatorData.get(acceptMsg.getElevatorId()).setIdle(false);
			this.elevatorsStateChanged = true;
			break;
		case DECLINE_FLOOR_REQUEST_MESSAGE:
			DeclineFloorRequestMessage declineMsg = (DeclineFloorRequestMessage) updateMessage;
			requestResponses.put(declineMsg.getRequestID(), false); // Updates service request with elevator's response, via corresponding ID
			break;
		case ARRIVAL_ELEVATOR_MESSAGE:
			ArrivalElevatorMessage arrivalMsg = (ArrivalElevatorMessage) updateMessage;
			elevatorData.get(arrivalMsg.getElevatorId()).setCurrentFloor(arrivalMsg.getCurrentFloor());
			elevatorData.get(arrivalMsg.getElevatorId()).setDestinationFloor(arrivalMsg.getFloorBuffer());
			elevatorData.get(arrivalMsg.getElevatorId()).setIdle(false);
			this.elevatorsStateChanged = true;
			break;
		case START_TRANSIENT_FAULT:
			StartTransientFaultMessage startTransientFaultMsg = (StartTransientFaultMessage) updateMessage;
			elevatorData.get(startTransientFaultMsg.getElevatorID()).setTransientFaulted(true);
			this.elevatorsStateChanged = true;
			printElevatorState();
			break;
		case END_TRANSIENT_FAULT:
			EndTransientFaultMessage endTransientFaultMsg = (EndTransientFaultMessage) updateMessage;
			elevatorData.get(endTransientFaultMsg.getElevatorID()).setTransientFaulted(false);
			this.elevatorsStateChanged = true;
			printElevatorState();
			break;
		case IDLE_ELEVATOR_MESSAGE:
			IdleElevatorMessage idleMessage = (IdleElevatorMessage) updateMessage;
			elevatorData.get(idleMessage.getElevatorID()).setIdle(true);
			this.elevatorsStateChanged = true;
			break;
		default:
			System.out.println("Unexpected message type.");
			break;
		}
		
		boolean stillRunning = false;
		for (SchedulerElevatorData elevatorData: elevatorData) {
			if (!elevatorData.getIdle() && !elevatorData.getHardFaulted()) {
				stillRunning = true;
				break;
			}
		}
		
		if (stillRunning == false && this.finalMessageArrived) {
			long timeElapsed = System.currentTimeMillis() - this.startingTime;
			System.out.println("Simulation finished in " + timeElapsed + " milliseconds!");
			JOptionPane.showMessageDialog(null,"Simulation finished in " + timeElapsed / 1000 + " seconds!");
		}
		
		notifyAll();
	}
	
	/**
	 * Shuts down elevator that received a hard fault
	 * 
	 * @param elevatorID - elevator that should be shut down due to hard fault
	 */
	public synchronized void hardFaultElevator(int elevatorID) {
		System.out.println("Scheduler System has hard faulted elevator " + elevatorID);
		elevatorData.get(elevatorID).setHardFaulted(true);
		this.elevatorsStateChanged = true;
		printElevatorState();
		notifyAll();
	}

	/**
	 * Returns arraylist of elevator states. Waits on response buffer to be empty -
	 * should any outstanding elevator response messages be unparsed, retrieving the
	 * elevator states prematurely will yield a potentially inaccurate state.
	 * 
	 * @return arraylist of elevator states.
	 */
	public synchronized ArrayList<SchedulerElevatorData> getElevatorData() {
		while (!elevatorBufferCommunicator.isMessageListEmpty() || !this.elevatorsStateChanged) { 
			// Need to wait until all changes to elevator states are made before retrieving and the elevator state has changed
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		notifyAll();
		this.elevatorsStateChanged = false;
		return elevatorData;
	}

	/**
	 * Retrieves elevator's response (whether it accepted or declined the service
	 * request) according to requestID
	 * 
	 * @param requestID - ID of service request message and corresponding elevator response
	 * @param messageSentTime - time at which the request was initially sent
	 * @throws TimeoutException thrown if we have waited past the hard fault timeout
	 * @return boolean corresponding to elevator response to service request
	 */
	public synchronized boolean getRequestResponseTimed(int requestID, long messageSentTime) throws TimeoutException {
		while (requestResponses.get(requestID) == null) {
			try {
				long timeWaited = System.currentTimeMillis() - messageSentTime;
				if(timeWaited >= HARD_FAULT_TIMEOUT_DURATION) {
					throw new TimeoutException();
				} else {
					wait(HARD_FAULT_TIMEOUT_DURATION - timeWaited);
				}
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
	 * Iteration 3 sequence 1. Floor reads events from file 2. Floor parses and
	 * stores events as messages 3. Floor sends messages to scheduler 4. Scheduler
	 * receives request from floor, and retrieves elevator states 5. Scheduler
	 * computes which elevator should service request 6. Scheduler sends service
	 * request message to Elevator 7. Elevator receives request message from
	 * Scheduler and directs Elevator accordingly, if possible. Elevator sends an
	 * accept or decline response appropriately to Scheduler. 8. Scheduler receives
	 * accept/decline response from Elevator. If declined, Scheduler recomputes
	 * which elevator should service request. 9. Repeat 3-8.
	 * 
	 * @param args A list of string args
	 */
	public static void main(String[] args) {
		// elevator
		ElevatorFrame elevatorFrame = new ElevatorFrame();
		int sendPort = ElevatorSystem.SEND_PORT;
		int receivePort = ElevatorSystem.RECEIVE_PORT;
		ByteBufferCommunicator elevatorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		Elevator elevator1 = new Elevator(0, false, elevatorBufferCommunicator, 1, elevatorFrame);
		Elevator elevator2 = new Elevator(1, false, elevatorBufferCommunicator, 1, elevatorFrame);
		Elevator elevator3 = new Elevator(2, false, elevatorBufferCommunicator, 2, elevatorFrame);
		Elevator elevator4 = new Elevator(3, false, elevatorBufferCommunicator, 2, elevatorFrame);
		new Thread(elevatorBufferCommunicator).start();

		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		elevators.add(elevator1);
		elevators.add(elevator2);
		elevators.add(elevator3);
		elevators.add(elevator4);
		
		Thread elevatorSystem = new Thread(new ElevatorSystem(elevatorBufferCommunicator, elevators));
		elevatorSystem.start();
		
		// floor
		String inputFile = null;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fileChooser.setDialogTitle("Choose input file");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			    "TXT Files", "txt");
		fileChooser.setFileFilter(filter);
		fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    inputFile = selectedFile.getAbsolutePath();
		} else {
			// must choose a file otherwise program can not progress properly
			System.exit(1);
		}
		
		sendPort = FloorSystem.FLOOR_SEND_PORT;
		receivePort = FloorSystem.FLOOR_RECEIVE_PORT;
		ByteBufferCommunicator floorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		sendPort = FloorSystem.FAULT_SEND_PORT;
		receivePort = FloorSystem.FAULT_RECEIVE_PORT;
		ByteBufferCommunicator faultBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		FloorSystem floorSystem = new FloorSystem(inputFile, floorBufferCommunicator, faultBufferCommunicator);
		Thread floorSystemThread = new Thread(floorSystem); // TODO maybe make this thread be spawned by floor system itself
		Thread floorResponseHandler = new Thread(new FloorResponseHandler(floorSystem, floorBufferCommunicator));
		new Thread(floorBufferCommunicator).start();
		new Thread(faultBufferCommunicator).start();

		floorSystemThread.start();
		floorResponseHandler.start();

		// scheduler <-> floor (thread 1)
		sendPort = FloorSystem.FLOOR_RECEIVE_PORT;
		receivePort = FloorSystem.FLOOR_SEND_PORT;
		ByteBufferCommunicator floorBufferCommunicator2 = new ByteBufferCommunicator(sendPort, receivePort);

		// scheduler <-> elevator (thread 2)
		sendPort = ElevatorSystem.RECEIVE_PORT;
		receivePort = ElevatorSystem.SEND_PORT;
		ByteBufferCommunicator elevatorBufferCommunicator2 = new ByteBufferCommunicator(sendPort, receivePort);
		
		// scheduler <-> floor fault (thread 3)
		sendPort = FloorSystem.FAULT_RECEIVE_PORT;
		receivePort = FloorSystem.FAULT_SEND_PORT;
		ByteBufferCommunicator faultBufferCommunicator2 = new ByteBufferCommunicator(sendPort, receivePort);
		
		// TODO Might need to give faultBufferCommunicator
		SchedulerSystem schedulerSystem = new SchedulerSystem(elevatorBufferCommunicator2, floorBufferCommunicator2,
				ElevatorSystem.NUM_ELEVATORS);
		Thread schedulerRequestHandler = new Thread(
				new SchedulerRequestHandler(elevatorBufferCommunicator2, floorBufferCommunicator2, schedulerSystem));
		Thread schedulerResponseHandler = new Thread(
				new SchedulerResponseHandler(elevatorBufferCommunicator2, floorBufferCommunicator2, schedulerSystem));
		Thread schedulerFaultHandler = new Thread(
				new SchedulerFaultHandler(elevatorBufferCommunicator2, faultBufferCommunicator2, schedulerSystem));

		schedulerRequestHandler.start();
		schedulerResponseHandler.start();
		schedulerFaultHandler.start();

		new Thread(floorBufferCommunicator2).start();
		new Thread(elevatorBufferCommunicator2).start();
		new Thread(faultBufferCommunicator2).start();

	}
}