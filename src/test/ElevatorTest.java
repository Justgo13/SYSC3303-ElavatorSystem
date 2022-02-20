package test;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
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

	public static void sendFloorRequestMessage(int srcFloor, int destFloor, String direction,
			ByteBufferCommunicator buffer, int elevatorId) {

		try {
			Message msg = new ServiceFloorRequestMessage(srcFloor, destFloor, direction, elevatorId);
			byte[] msgBytes = SerializeUtils.serialize(msg);
			buffer.putRequestBuffer(msgBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Message recieveFromResponseBuffer(ByteBufferCommunicator buffer) {
		Message msg = null;
		try {
			byte[] msgBytes = buffer.getResponseBuffer();
			msg = SerializeUtils.deserialize(msgBytes);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(msg.getMessageType());
		return msg;
	}

	public static void main(String[] args) {

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
		System.out.println("Running Test");
		ByteBufferCommunicator bufferCommunicator = new ByteBufferCommunicator();
		Elevator elevator1 = new Elevator(1, false, bufferCommunicator, 0);

		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		elevators.add(elevator1);

		Thread elevatorSystem = new Thread(new ElevatorSystem(bufferCommunicator, elevators));
		elevatorSystem.start();

		// Test
		Message msg = new ServiceFloorRequestMessage(2, 4, "up", 1);
		try {
			byte[] msgBytes = SerializeUtils.serialize(msg);
			bufferCommunicator.putRequestBuffer(msgBytes);

			byte[] confirmationMsg = bufferCommunicator.getResponseBuffer();

			Message confirmation = SerializeUtils.deserialize(confirmationMsg);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, confirmation.getMessageType());

			System.out.println(confirmation.getMessageType());
			Thread.sleep(1000);
			// SECOND REQUEST, SHOULD BE DECLINED
			msg = new ServiceFloorRequestMessage(3, 5, "up", 1);

			msgBytes = SerializeUtils.serialize(msg);
			bufferCommunicator.putRequestBuffer(msgBytes);

			confirmationMsg = bufferCommunicator.getResponseBuffer();

			confirmation = SerializeUtils.deserialize(confirmationMsg);
			Assert.assertEquals(MessageTypes.DECLINE_FLOOR_REQUEST_MESSAGE, confirmation.getMessageType());

			byte[] arrivalMsg = bufferCommunicator.getResponseBuffer();
			Message arrival = SerializeUtils.deserialize(arrivalMsg);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, arrival.getMessageType());
			Assert.assertEquals(2, ((ArrivalElevatorMessage) arrival).getCurrentFloor());

			arrivalMsg = bufferCommunicator.getResponseBuffer();
			arrival = SerializeUtils.deserialize(arrivalMsg);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, arrival.getMessageType());
			Assert.assertEquals(4, ((ArrivalElevatorMessage) arrival).getCurrentFloor());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@DisplayName("Multiple interrupt during moving state")
	void testMultipleInterruptsWhileMoving() {
		ByteBufferCommunicator bufferCommunicator = new ByteBufferCommunicator();
		Elevator elevator1 = new Elevator(1, false, bufferCommunicator, 0);

		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		elevators.add(elevator1);

		Thread elevatorSystem = new Thread(new ElevatorSystem(bufferCommunicator, elevators));
		elevatorSystem.start();

		Message msg;
		// Test
		try {
			sendFloorRequestMessage(1, 9, "up", bufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			// Arrival Msg for floor 1
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(1, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			Thread.sleep(13000);
			// SECOND REQUEST, SHOULD BE DECLINED

			sendFloorRequestMessage(5, 4, "down", bufferCommunicator, 1);

			// Decline msg
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.DECLINE_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			// THIRD REQUEST
			sendFloorRequestMessage(7, 8, "up", bufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			// ARRIVAL Msg for floor 7
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(7, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			// ARRIVAL Msg for floor 8
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(8, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			// ARRIVAL Msg for floor 9
			msg = recieveFromResponseBuffer(bufferCommunicator);
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
		ByteBufferCommunicator bufferCommunicator = new ByteBufferCommunicator();
		Elevator elevator1 = new Elevator(1, false, bufferCommunicator, 0);

		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		elevators.add(elevator1);

		Thread elevatorSystem = new Thread(new ElevatorSystem(bufferCommunicator, elevators));
		elevatorSystem.start();

		Message msg;
		// Test
		try {
			sendFloorRequestMessage(1, 6, "up", bufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			// Arrival Msg for floor 1
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());

			Assert.assertEquals(1, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			Thread.sleep(1000);

			// Send Request
			sendFloorRequestMessage(5, 4, "down", bufferCommunicator, 1);

			// Decline Msg
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.DECLINE_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			Thread.sleep(500);

			// Send Request
			sendFloorRequestMessage(4, 5, "up", bufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			Thread.sleep(500);

			// Send Request
			sendFloorRequestMessage(1, 3, "up", bufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			// Arrival Msg for Floor 3
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(3, ((ArrivalElevatorMessage) msg).getCurrentFloor());
			// Arrival Msg for Floor 4
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(4, ((ArrivalElevatorMessage) msg).getCurrentFloor());
			// Arrival Msg for Floor 5
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(5, ((ArrivalElevatorMessage) msg).getCurrentFloor());
			// Arrival Msg for Floor 6
			msg = recieveFromResponseBuffer(bufferCommunicator);
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
		ByteBufferCommunicator bufferCommunicator = new ByteBufferCommunicator();
		Elevator elevator1 = new Elevator(1, false, bufferCommunicator, 7);

		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		elevators.add(elevator1);

		Thread elevatorSystem = new Thread(new ElevatorSystem(bufferCommunicator, elevators));
		elevatorSystem.start();

		Message msg;
		// Test
		try {
			sendFloorRequestMessage(4, 5, "up", bufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());
			Thread.sleep(10);

			sendFloorRequestMessage(6, 5, "down", bufferCommunicator, 1);

			// Accept Msg
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ACCEPT_FLOOR_REQUEST_MESSAGE, msg.getMessageType());

			// Arrival Msg for floor 6
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(6, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			// Arrival Msg for floor 4
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(4, ((ArrivalElevatorMessage) msg).getCurrentFloor());

			// Arrival Msg for floor 5
			msg = recieveFromResponseBuffer(bufferCommunicator);
			Assert.assertEquals(MessageTypes.ARRIVAL_ELEVATOR_MESSAGE, msg.getMessageType());
			Assert.assertEquals(5, ((ArrivalElevatorMessage) msg).getCurrentFloor());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
