package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorSystem;
import FloorSubsystem.FloorSystem;
import Messages.ElevatorHardFaultMessage;
import Messages.ElevatorTransientFaultMessage;
import SchedulerSubsystem.SchedulerFaultHandler;
import SchedulerSubsystem.SchedulerSystem;
import SharedResources.ByteBufferCommunicator;
import SharedResources.SerializeUtils;

/**
 * @author Kevin Quach and Shashaank Srivastava
 *
 */
class SchedulerFaultHandlerTest {

	private static SchedulerFaultHandler faultHandler;
	private static ByteBufferCommunicator floorBufferCommunicator;
	private static ByteBufferCommunicator testBufferCommunicator;
	private static ByteBufferCommunicator floorFaultBufferCommunicator;
	private static ByteBufferCommunicator elevatorBufferCommunicator;

	@BeforeAll
	static void setUp() throws Exception {
		int sendPort = ElevatorSystem.SEND_PORT;
		int receivePort = ElevatorSystem.RECEIVE_PORT;
		testBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		new Thread(testBufferCommunicator).start();
		
		sendPort = FloorSystem.FLOOR_RECEIVE_PORT;
		receivePort = FloorSystem.FLOOR_SEND_PORT;
		floorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		new Thread(floorBufferCommunicator).start();

		sendPort = ElevatorSystem.RECEIVE_PORT;
		receivePort = ElevatorSystem.SEND_PORT;
		elevatorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		new Thread(elevatorBufferCommunicator).start();
		
		sendPort = FloorSystem.FAULT_RECEIVE_PORT;
		receivePort = FloorSystem.FAULT_SEND_PORT;
		floorFaultBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		new Thread(floorFaultBufferCommunicator).start();
		SchedulerSystem schedulerSystem = new SchedulerSystem(elevatorBufferCommunicator, floorBufferCommunicator, 0);
		faultHandler = new SchedulerFaultHandler(elevatorBufferCommunicator, floorFaultBufferCommunicator,
				schedulerSystem);
	}
	
	@Test
	@DisplayName("Parse a fault message")
	void testParseFaultMessage() {
		faultHandler.parseFaultMessage(new ElevatorHardFaultMessage(0, 0));
		
		byte[] messageBytes = testBufferCommunicator.getUDPMessage();
        try {
        	ElevatorHardFaultMessage hardFaultMessage = (ElevatorHardFaultMessage) SerializeUtils.deserialize(messageBytes);
            assertEquals(hardFaultMessage.getElevatorID(), 0);
            assertEquals(hardFaultMessage.getTimestamp(), 0);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            fail();
        }
        
		faultHandler.parseFaultMessage(new ElevatorTransientFaultMessage(0, 0, 1));
		
		messageBytes = testBufferCommunicator.getUDPMessage();
        try {
        	ElevatorTransientFaultMessage transientFaultMessage = (ElevatorTransientFaultMessage) SerializeUtils.deserialize(messageBytes);
            assertEquals(transientFaultMessage.getElevatorID(), 0);
            assertEquals(transientFaultMessage.getTimeOfFault(), 0);
            assertEquals(transientFaultMessage.getTimestamp(), 1);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            fail();
        }
	}
}
