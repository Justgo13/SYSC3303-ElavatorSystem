package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import Messages.ArrivalElevatorMessage;
import Messages.FloorLightResponseMessage;
import Messages.MessageTypes;
import SchedulerSubsystem.SchedulerResponseHandler;
import SchedulerSubsystem.SchedulerSystem;
import SharedResources.ByteBufferCommunicator;
import SharedResources.DirectionEnum;
import SharedResources.SerializeUtils;

/**
 * @author Kevin Quach and Shashaank Srivastava
 *
 */
class SchedulerResponseHandlerTest {

	private static SchedulerResponseHandler responseHandler;
	private static ByteBufferCommunicator floorBufferCommunicator;
	private static ByteBufferCommunicator testBufferCommunicator;
	private static ByteBufferCommunicator elevatorBufferCommunicator;

	@BeforeAll
	static void setUp() throws Exception {
		int sendPort = 23;
		int receivePort = 24;
		floorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		testBufferCommunicator = new ByteBufferCommunicator(receivePort, sendPort);
		
		new Thread(floorBufferCommunicator).start();
		new Thread(testBufferCommunicator).start();

		sendPort = 69;
		receivePort = 70;
		elevatorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		new Thread(elevatorBufferCommunicator).start();
		SchedulerSystem schedulerSystem = new SchedulerSystem(elevatorBufferCommunicator, floorBufferCommunicator, 0);
		responseHandler = new SchedulerResponseHandler(elevatorBufferCommunicator, floorBufferCommunicator,
				schedulerSystem);
	}

	@Test
	@DisplayName("Send a response message")
	void testSendFloorResponseMessage() {
		responseHandler.sendFloorResponseMessage(0, 1, DirectionEnum.UP_DIRECTION);
		try {
			FloorLightResponseMessage msg = (FloorLightResponseMessage) SerializeUtils
					.deserialize(testBufferCommunicator.getUDPMessage());
			assertEquals(msg.getMessageType(), MessageTypes.FLOOR_LIGHT_RESPONSE_MESSAGE);
			assertEquals(msg.getElevatorID(), 0);
			assertEquals(msg.getCurrentFloor(), 1);
			assertEquals(msg.getDirection(), DirectionEnum.UP_DIRECTION);
			assertEquals(msg.toString(), "Elevator 0 arrived at floor 1. Lighting up direction lamp.");

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@DisplayName("Parse an arrival message")
	void testParseArrivalMessage() {
		ArrayList<Integer> floorBuffer = new ArrayList<>();
		floorBuffer.add(3);
		ArrivalElevatorMessage arrivalMsg = new ArrivalElevatorMessage(2, 4, floorBuffer);
		responseHandler.parseArrivalMessage(arrivalMsg);
		try {
			responseHandler.sendFloorResponseMessage(0, 1, DirectionEnum.UP_DIRECTION);

			FloorLightResponseMessage msg = (FloorLightResponseMessage) SerializeUtils
					.deserialize(testBufferCommunicator.getUDPMessage());
			assertEquals(msg.getMessageType(), MessageTypes.FLOOR_LIGHT_RESPONSE_MESSAGE);
			assertEquals(msg.getElevatorID(), 2);
			assertEquals(msg.getCurrentFloor(), 4);
			assertEquals(msg.getDirection(), DirectionEnum.DOWN_DIRECTION);
			assertEquals(msg.toString(), "Elevator 2 arrived at floor 4. Lighting down direction lamp.");

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
