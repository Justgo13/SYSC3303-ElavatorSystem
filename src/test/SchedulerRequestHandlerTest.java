/**
 * 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import Messages.FloorDataMessage;
import Messages.ServiceFloorRequestMessage;
import SchedulerSubsystem.SchedulerElevatorData;
import SchedulerSubsystem.SchedulerRequestHandler;
import SchedulerSubsystem.SchedulerSystem;
import SharedResources.ByteBufferCommunicator;
import SharedResources.DirectionEnum;

/**
 * @author Kevin Quach and Shashaank Srivastava
 *
 */
class SchedulerRequestHandlerTest {
	
	private static SchedulerRequestHandler requestHandler;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		int sendPort = 23;
		int receivePort = 24;
		ByteBufferCommunicator floorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		
		sendPort = 69;
		receivePort =  70;
		ByteBufferCommunicator elevatorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		SchedulerSystem schedulerSystem = new SchedulerSystem(elevatorBufferCommunicator, floorBufferCommunicator, 0);
		requestHandler = new SchedulerRequestHandler(elevatorBufferCommunicator, floorBufferCommunicator, schedulerSystem);
	}

	//below tests show the order of precedence (if such an elevator exists that meets the test's conditions, choose that one, otherwise try the other elevators that the tests below it evaluate)
	@Test
	@DisplayName("Chooses the idle elevator at the requesting floor")
	void testSameFloorIdle() {
		FloorDataMessage request = new FloorDataMessage(0.0f, 1, DirectionEnum.UP_DIRECTION, 0);
		ArrayList<SchedulerElevatorData> elevators = new ArrayList<>();
		ArrayList<Integer> e1Dest = new ArrayList<>();
		elevators.add(new SchedulerElevatorData(1, e1Dest, DirectionEnum.IDLE_DIRECTION));
		ArrayList<Integer> e2Dest = new ArrayList<>();
		e2Dest.add(5);
		e2Dest.add(10);
		elevators.add(new SchedulerElevatorData(0, e2Dest, DirectionEnum.UP_DIRECTION));
		
		List<ServiceFloorRequestMessage> madeRequest = requestHandler.makeRequest(request, elevators);
		assertEquals(madeRequest.get(0).getElevatorId(), 0);
		assertEquals(madeRequest.get(0).getDirection(), DirectionEnum.DOWN_DIRECTION);
	}
	
	@Test
	@DisplayName("Chooses the closest elevator that will travel in the path of the request and meet the requesting floor")
	void testSameDirectionClosest() {
		FloorDataMessage request = new FloorDataMessage(0.0f, 4, DirectionEnum.UP_DIRECTION, 6);
		ArrayList<SchedulerElevatorData> elevators = new ArrayList<>();
		ArrayList<Integer> e1Dest = new ArrayList<>();
		e1Dest.add(15);
		elevators.add(new SchedulerElevatorData(0, e1Dest, DirectionEnum.UP_DIRECTION));
		ArrayList<Integer> e2Dest = new ArrayList<>();
		e2Dest.add(16);
		elevators.add(new SchedulerElevatorData(3, e2Dest, DirectionEnum.UP_DIRECTION));
		
		List<ServiceFloorRequestMessage> madeRequest = requestHandler.makeRequest(request, elevators);
		assertEquals(madeRequest.get(0).getElevatorId(), 1);
		assertEquals(madeRequest.get(0).getDirection(), DirectionEnum.UP_DIRECTION);
		
		request = new FloorDataMessage(0.0f, 7, DirectionEnum.DOWN_DIRECTION, 4);
		elevators = new ArrayList<>();
		e1Dest = new ArrayList<>();
		e1Dest.add(2);
		elevators.add(new SchedulerElevatorData(15, e1Dest, DirectionEnum.DOWN_DIRECTION));
		e2Dest = new ArrayList<>();
		e2Dest.add(6);
		e2Dest.add(5);
		elevators.add(new SchedulerElevatorData(13, e2Dest, DirectionEnum.DOWN_DIRECTION));
		
		madeRequest = requestHandler.makeRequest(request, elevators);
		assertEquals(madeRequest.get(0).getElevatorId(), 1);
		assertEquals(madeRequest.get(0).getDirection(), DirectionEnum.DOWN_DIRECTION);
	}
	
	@Test
	@DisplayName("Chooses the closest idle elevator")
	void testIdleClosest() {
		FloorDataMessage request = new FloorDataMessage(0.0f, 4, DirectionEnum.UP_DIRECTION, 6);
		ArrayList<SchedulerElevatorData> elevators = new ArrayList<>();
		ArrayList<Integer> e1Dest = new ArrayList<>();
		e1Dest.add(3);
		elevators.add(new SchedulerElevatorData(3, e1Dest, DirectionEnum.DOWN_DIRECTION));
		ArrayList<Integer> e2Dest = new ArrayList<>();
		elevators.add(new SchedulerElevatorData(3, e2Dest, DirectionEnum.IDLE_DIRECTION));
		
		List<ServiceFloorRequestMessage> madeRequest = requestHandler.makeRequest(request, elevators);
		assertEquals(madeRequest.get(0).getElevatorId(), 1);
		assertEquals(madeRequest.get(0).getDirection(), DirectionEnum.UP_DIRECTION);
		
		request = new FloorDataMessage(0.0f, 7, DirectionEnum.DOWN_DIRECTION, 4);
		elevators = new ArrayList<>();
		e1Dest = new ArrayList<>();
		e1Dest.add(9);
		elevators.add(new SchedulerElevatorData(8, e1Dest, DirectionEnum.UP_DIRECTION));
		e2Dest = new ArrayList<>();
		elevators.add(new SchedulerElevatorData(8, e2Dest, DirectionEnum.IDLE_DIRECTION));
		
		madeRequest = requestHandler.makeRequest(request, elevators);
		assertEquals(madeRequest.get(0).getElevatorId(), 1);
		assertEquals(madeRequest.get(0).getDirection(), DirectionEnum.DOWN_DIRECTION);
	}
	
	@Test
	@DisplayName("Chooses the elevator with the closest final destination to the requesting floor")
	void testDestinationClosest() {
		FloorDataMessage request = new FloorDataMessage(0.0f, 4, DirectionEnum.UP_DIRECTION, 6);
		ArrayList<SchedulerElevatorData> elevators = new ArrayList<>();
		ArrayList<Integer> e1Dest = new ArrayList<>();
		e1Dest.add(6);
		e1Dest.add(5);
		e1Dest.add(3);
		elevators.add(new SchedulerElevatorData(7, e1Dest, DirectionEnum.DOWN_DIRECTION));
		ArrayList<Integer> e2Dest = new ArrayList<>();
		e2Dest.add(6);
		e2Dest.add(5);
		e2Dest.add(2);
		elevators.add(new SchedulerElevatorData(7, e2Dest, DirectionEnum.DOWN_DIRECTION));
		
		List<ServiceFloorRequestMessage> madeRequest = requestHandler.makeRequest(request, elevators);
		assertEquals(madeRequest.get(0).getElevatorId(), 0);
		assertEquals(madeRequest.get(0).getDirection(), DirectionEnum.UP_DIRECTION);
		
		request = new FloorDataMessage(0.0f, 7, DirectionEnum.UP_DIRECTION, 12);
		elevators = new ArrayList<>();
		e1Dest = new ArrayList<>();
		e1Dest.add(4);
		e1Dest.add(5);
		e1Dest.add(8);
		elevators.add(new SchedulerElevatorData(3, e1Dest, DirectionEnum.UP_DIRECTION));
		e2Dest = new ArrayList<>();
		e2Dest.add(4);
		e2Dest.add(5);
		e2Dest.add(9);
		elevators.add(new SchedulerElevatorData(3, e2Dest, DirectionEnum.UP_DIRECTION));
		
		madeRequest = requestHandler.makeRequest(request, elevators);
		assertEquals(madeRequest.get(0).getElevatorId(), 0);
		assertEquals(madeRequest.get(0).getDirection(), DirectionEnum.UP_DIRECTION);
	}
	
	@Test
	@DisplayName("Chooses the only elevator that has not faulted, despite it being the least optimal")
	void testFaults() {
		FloorDataMessage request = new FloorDataMessage(0.0f, 4, DirectionEnum.UP_DIRECTION, 6);
		ArrayList<SchedulerElevatorData> elevators = new ArrayList<>();
		ArrayList<Integer> e1Dest = new ArrayList<>();
		elevators.add(new SchedulerElevatorData(4, e1Dest, DirectionEnum.UP_DIRECTION));
		elevators.get(0).setHardFaulted(true);
		ArrayList<Integer> e2Dest = new ArrayList<>();
		elevators.add(new SchedulerElevatorData(4, e2Dest, DirectionEnum.UP_DIRECTION));
		elevators.get(1).setTransientFaulted(true);
		ArrayList<Integer> e3Dest = new ArrayList<>();
		e2Dest.add(2);
		e2Dest.add(1);
		elevators.add(new SchedulerElevatorData(3, e3Dest, DirectionEnum.DOWN_DIRECTION));
		
		List<ServiceFloorRequestMessage> madeRequest = requestHandler.makeRequest(request, elevators);
		assertEquals(madeRequest.size(), 1);
		assertEquals(madeRequest.get(0).getElevatorId(), 2);
		assertEquals(madeRequest.get(0).getDirection(), DirectionEnum.UP_DIRECTION);
	}

}
