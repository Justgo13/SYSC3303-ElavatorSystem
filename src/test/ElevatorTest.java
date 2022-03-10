package test;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.Elevator;
import ElevatorSubsystem.ElevatorSystem;
import Messages.ArrivalElevatorMessage;
import Messages.Message;
import Messages.MessageTypes;
import Messages.ServiceFloorRequestMessage;
import SharedResources.ByteBufferCommunicator;
import SharedResources.SerializeUtils;

/**
 * 
 * @author Shashaank Srivastav and Harjap Gill
 *
 */
class ElevatorTest {
	private static ByteBufferCommunicator testBufferCommunicator, elevatorBufferCommunicator;
	private static ElevatorSystem elevatorSystem;
	
	public static void sendFloorRequestMessage(int srcFloor, int destFloor, String direction,
			ByteBufferCommunicator buffer, int elevatorId) {

		try {
			Message msg = new ServiceFloorRequestMessage(srcFloor, destFloor, direction, elevatorId);
			byte[] msgBytes = SerializeUtils.serialize(msg);
			buffer.sendUDPMessage(msgBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Message recieveFromResponseBuffer(ByteBufferCommunicator buffer) {
		Message msg = null;
		try {
			byte[] msgBytes = buffer.getUDPMessage();
			msg = SerializeUtils.deserialize(msgBytes);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(msg.getMessageType());
		return msg;
	}
	
	@BeforeAll
	static void setUp() {
		int sendPort = 69;
		int receivePort = 70;
		elevatorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		testBufferCommunicator = new ByteBufferCommunicator(receivePort, sendPort);
		Elevator elevator = new Elevator(1, false, elevatorBufferCommunicator, 0);

		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		elevators.add(elevator);

		elevatorSystem = new ElevatorSystem(elevatorBufferCommunicator, elevators);
		
		new Thread(elevatorBufferCommunicator).start();
		new Thread(testBufferCommunicator).start();
		new Thread(elevatorSystem).start();
	}

	@Test
	@DisplayName("Elevator initialization")
	void testElevator() {
		int elevatorId = 1;
		boolean doorOpen = false;

		Elevator elevator = new Elevator(elevatorId, doorOpen, null, 1);

		Assert.assertEquals(elevatorId, elevator.getElevatorId());
		Assert.assertFalse(elevator.getDoorOpen());

		elevator.setDoorOpen(true);

		Assert.assertTrue(elevator.getDoorOpen());

	}

	@Test
	@DisplayName("Message interrupt during moving state")
	void testElevatorInterrupt() {
		Message msg;
		
		try {
			sendFloorRequestMessage(2, 4, "up", testBufferCommunicator, 1);
			
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			Thread.sleep(1000);
			
			// SECOND REQUEST, SHOULD BE DECLINED
			sendFloorRequestMessage(3, 5, "up", testBufferCommunicator, 1);
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.DECLINE_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(2, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(4, ((ArrivalElevatorMessage) msg).getCurrentFloor());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@DisplayName("Multiple interrupt during moving state")
	void testMultipleInterruptsWhileMoving() {
		Message msg;
		// Test
		try {
			sendFloorRequestMessage(1, 9, "up", testBufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			// Arrival Msg for floor 1
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(1, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			Thread.sleep(13000);
			// SECOND REQUEST, SHOULD BE DECLINED

			sendFloorRequestMessage(5, 4, "down", testBufferCommunicator, 1);

			// Decline msg
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.DECLINE_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			// THIRD REQUEST
			sendFloorRequestMessage(7, 8, "up", testBufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			// ARRIVAL Msg for floor 7
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(7, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			// ARRIVAL Msg for floor 8
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(8, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			// ARRIVAL Msg for floor 9
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(9, ((ArrivalElevatorMessage) msg).getCurrentFloor());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@DisplayName("Multiple interrupts during door open state")
	void testMultipleInterruptsDuringDoorOpen() {
		Message msg;
		// Test
		try {
			sendFloorRequestMessage(1, 6, "up", testBufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			// Arrival Msg for floor 1
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());

			Assert.assertEquals(1, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			Thread.sleep(1000);

			// Send Request
			sendFloorRequestMessage(5, 4, "down", testBufferCommunicator, 1);

			// Decline Msg
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.DECLINE_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			Thread.sleep(500);

			// Send Request
			sendFloorRequestMessage(4, 5, "up", testBufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			Thread.sleep(500);

			// Send Request
			sendFloorRequestMessage(1, 3, "up", testBufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			// Arrival Msg for Floor 3
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(3, ((ArrivalElevatorMessage) msg).getCurrentFloor());
			// Arrival Msg for Floor 4
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(4, ((ArrivalElevatorMessage) msg).getCurrentFloor());
			// Arrival Msg for Floor 5
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(5, ((ArrivalElevatorMessage) msg).getCurrentFloor());
			// Arrival Msg for Floor 6
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(6, ((ArrivalElevatorMessage) msg).getCurrentFloor());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@DisplayName("Handling direction changes")
	void testElevatorDirectionChanges() {
		// elevator starts at floor 7 so we must override what was done in setUp()
		ByteBufferCommunicator elevatorBufferCommunicator = new ByteBufferCommunicator(71, 72);
		ByteBufferCommunicator testBufferCommunicator = new ByteBufferCommunicator(72, 71);
		Elevator elevator = new Elevator(1, false, elevatorBufferCommunicator, 7);

		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		elevators.add(elevator);

		ElevatorSystem elevatorSystem = new ElevatorSystem(elevatorBufferCommunicator, elevators);
		
		new Thread(elevatorBufferCommunicator).start();
		new Thread(testBufferCommunicator).start();
		new Thread(elevatorSystem).start();
		
		Message msg;
		// Test
		try {
			sendFloorRequestMessage(4, 5, "up", testBufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());
			Thread.sleep(10);

			sendFloorRequestMessage(6, 5, "down", testBufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			// Arrival Msg for floor 6
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(6, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			// Arrival Msg for floor 4
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(4, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			// Arrival Msg for floor 5
			msg = recieveFromResponseBuffer(testBufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(5, ((ArrivalElevatorMessage) msg).getCurrentFloor());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
