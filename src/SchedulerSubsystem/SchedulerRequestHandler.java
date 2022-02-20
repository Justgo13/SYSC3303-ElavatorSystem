package SchedulerSubsystem;

import java.io.IOException;
import java.util.ArrayList;

import Messages.FloorDataMessage;
import Messages.Message;
import Messages.ServiceFloorRequestMessage;
import SharedResources.ByteBufferCommunicator;
import SharedResources.DirectionEnum;
import SharedResources.SerializeUtils;

/**
 * @author Kevin Quach
 *
 */
public class SchedulerRequestHandler implements Runnable {
	private ByteBufferCommunicator elevatorBufferCommunicator;
	private ByteBufferCommunicator floorBufferCommunicator;
	private SchedulerSystem schedulerSystem;
	
	public SchedulerRequestHandler(ByteBufferCommunicator elevatorBufferCommunicator, ByteBufferCommunicator floorBufferCommunicator, SchedulerSystem schedulerSystem) {
		this.elevatorBufferCommunicator = elevatorBufferCommunicator;
		this.floorBufferCommunicator = floorBufferCommunicator;
		this.schedulerSystem = schedulerSystem;
	}
	
	public ServiceFloorRequestMessage makeRequest(FloorDataMessage request, ArrayList<SchedulerElevatorData> elevators) {
		int originFloor = request.getFloorNumber();
		int destinationFloor = request.getDestinationNumber();

		int minOriginDistance = Integer.MAX_VALUE;
		int minDestinationDistance = Integer.MAX_VALUE;
		
		//if there's an idle elevator at the requesting floor, use it, otherwise, check all the elevators
		for (SchedulerElevatorData elevator : elevators) {
			if (elevator.getDirection() == DirectionEnum.IDLE_DIRECTION && elevator.getCurrentFloor() == originFloor) { //todo: check if they also have any destinations
				String direction = (originFloor > destinationFloor) ? "down" : "up";
				return new ServiceFloorRequestMessage(originFloor, destinationFloor, direction, elevators.indexOf(elevator));
			}
			
			//also, determine the distances from each elevator's current position and the origin floor
										   //and each elevator's last destination floor and the origin floor
			if (Math.abs(originFloor - elevator.getCurrentFloor()) < minOriginDistance) {
				minOriginDistance = Math.abs(originFloor - elevator.getCurrentFloor());
			}
			if (elevator.getDestinationFloor().size() != 0 &&
					Math.abs(originFloor - elevator.getDestinationFloor().get(elevator.getDestinationFloor().size() - 1)) < minDestinationDistance) {
				minDestinationDistance = Math.abs(originFloor - elevator.getDestinationFloor().get(elevator.getDestinationFloor().size() - 1));
			}
		}
		
		//send it to the closest elevator moving in the same direction (including the direction of the request)
			//closest means the elevator's current floor is the closest to the request's origin floor
		DirectionEnum desiredDirection = (originFloor > destinationFloor) ? DirectionEnum.DOWN_DIRECTION : DirectionEnum.UP_DIRECTION;
		DirectionEnum requestDirection = (request.getDirection().toLowerCase().equals("down")) ? DirectionEnum.DOWN_DIRECTION : DirectionEnum.UP_DIRECTION;
		
		for (SchedulerElevatorData elevator : elevators) {
			if (elevator.getDirection() == desiredDirection && elevator.getDirection() == requestDirection && Math.abs(originFloor - elevator.getCurrentFloor()) == minOriginDistance) {
				String direction = (originFloor > destinationFloor) ? "down" : "up";
				return new ServiceFloorRequestMessage(originFloor, destinationFloor, direction, elevators.indexOf(elevator));
			}
		}
		
		//send it to the closest idle elevator, if there are any idle
			//closest means the elevator's current floor is the closest to the request's origin floor
			//direction moved after arriving at origin doesn't matter because it was empty
		for (SchedulerElevatorData elevator : elevators) {
			if (elevator.getDirection() == DirectionEnum.IDLE_DIRECTION && Math.abs(originFloor - elevator.getCurrentFloor()) == minOriginDistance) {
				String direction = (originFloor > destinationFloor) ? "down" : "up";
				return new ServiceFloorRequestMessage(originFloor, destinationFloor, direction, elevators.indexOf(elevator));
			}
		}
		
		//below should be changed to not be necessary with an alternate way to handle requests
		//send it to the closest elevator whose last destination is closest to the requesting floor
			//can move in the opposite direction after arriving at origin, which is not ideal, but guarantees that it is serviced
		for (SchedulerElevatorData elevator : elevators) {
			//it should be guaranteed that there is such an elevator
			if (elevator.getDestinationFloor().size() != 0 && Math.abs(originFloor - elevator.getDestinationFloor().get(elevator.getDestinationFloor().size() - 1)) == minDestinationDistance) {
				String direction = (originFloor > destinationFloor) ? "down" : "up";
				return new ServiceFloorRequestMessage(originFloor, destinationFloor, direction, elevators.indexOf(elevator));
			}
		}
		
		return null; //this shouldn't ever return
	}
	
	@Override
	public void run() {
		while (true) {
			byte[] firstRequest = floorBufferCommunicator.getRequestBuffer();
			
			try {
				Message firstMessage = SerializeUtils.deserialize(firstRequest);
				FloorDataMessage firstRequestMessage = (FloorDataMessage) firstMessage;
				
				boolean acceptedRequest = false;
				
				while (!acceptedRequest) {
					
					ArrayList<SchedulerElevatorData> elevators = schedulerSystem.getElevatorData();
					ServiceFloorRequestMessage requestMessage = makeRequest(firstRequestMessage, elevators);
					elevatorBufferCommunicator.putRequestBuffer(SerializeUtils.serialize(requestMessage));
					acceptedRequest = schedulerSystem.getRequestResponse(requestMessage.getRequestID()); //message ids need to be implemented
					//System.out.printf("Request %d handled \n", requestMessage.getRequestID());
					System.out.println(requestMessage);
					
					// sleep 5 seconds before retrying failed requests
					if (!acceptedRequest) {
						Thread.sleep(5000);
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
