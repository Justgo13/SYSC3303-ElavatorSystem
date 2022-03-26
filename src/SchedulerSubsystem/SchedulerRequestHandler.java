package SchedulerSubsystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Messages.FloorDataMessage;
import Messages.Message;
import Messages.ServiceFloorRequestMessage;
import SharedResources.ByteBufferCommunicator;
import SharedResources.DirectionEnum;
import SharedResources.SerializeUtils;

/**
 * @author Kevin Quach
 *
 *		Handles incoming FloorDataMessages by checking the state of the elevators and determining a priority of elevators to send requests to.
 *
 */
public class SchedulerRequestHandler implements Runnable {
    private ByteBufferCommunicator elevatorBufferCommunicator;
    private ByteBufferCommunicator floorBufferCommunicator;
    private SchedulerSystem schedulerSystem;
    
    /**
     * Constructs a request handler connected to the scheduler system, elevator and floor byte buffer communicators
     * @param elevatorBufferCommunicator - byte buffer communicator storing responses
     * @param floorBufferCommunicator - byte buffer communicator storing requests
     * @param schedulerSystem - contains elevator states
     */
    public SchedulerRequestHandler(ByteBufferCommunicator elevatorBufferCommunicator, ByteBufferCommunicator floorBufferCommunicator, SchedulerSystem schedulerSystem) {
        this.elevatorBufferCommunicator = elevatorBufferCommunicator;
        this.floorBufferCommunicator = floorBufferCommunicator;
        this.schedulerSystem = schedulerSystem;
    }
    
    /**
     * Checks if the elevator has already been prioritized
     * @param messages - the ordered list of messages
     * @param elevatorID - the elevator to check
     * @return true if the elevator has been prioritized, false if the elevator is missing
     */
    private boolean checkElevatorInRequestMessageList(List<ServiceFloorRequestMessage> messages, int elevatorID) {
        for (ServiceFloorRequestMessage msg : messages) {
            if (msg.getElevatorId() == elevatorID) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Forms an ordered list of potential requests to send to elevators, based on a request
     * @param request - the request from floor
     * @param elevators - the elevators in the state
     * @return an ordered list of requests to every elevator in the state
     */
    public List<ServiceFloorRequestMessage> makeRequest(FloorDataMessage request, ArrayList<SchedulerElevatorData> elevators) {
        int originFloor = request.getFloorNumber();
        int destinationFloor = request.getDestinationNumber();

        int minOriginDistance = Integer.MAX_VALUE;
        int minDestinationDistance = Integer.MAX_VALUE;

        ArrayList<ServiceFloorRequestMessage> prioritizedMessageList = new ArrayList<>();
        //priority is from first to last (most to least priority)
        //printed priorities: 0 = highest priority, increasing number = lower priority

        //if there's an idle elevator at the requesting floor, use it, otherwise, check all the elevators
        for (SchedulerElevatorData elevator : elevators) {
            if (!elevator.getTransientFaulted() && !elevator.getHardFaulted() && !checkElevatorInRequestMessageList(prioritizedMessageList, elevators.indexOf(elevator)) && elevator.getDirection() == DirectionEnum.IDLE_DIRECTION && elevator.getCurrentFloor() == originFloor) { //todo: check if they also have any destinations
                DirectionEnum direction = (originFloor > destinationFloor) ? DirectionEnum.DOWN_DIRECTION : DirectionEnum.UP_DIRECTION;
                prioritizedMessageList.add(new ServiceFloorRequestMessage(originFloor, destinationFloor, direction, elevators.indexOf(elevator)));
                System.out.println("Elevator " + elevators.indexOf(elevator) + " has priority 0");
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
        DirectionEnum requestDirection = (request.getDirection() == DirectionEnum.DOWN_DIRECTION) ? DirectionEnum.DOWN_DIRECTION : DirectionEnum.UP_DIRECTION;

        for (SchedulerElevatorData elevator : elevators) {
            if (!elevator.getTransientFaulted() && !elevator.getHardFaulted() && !checkElevatorInRequestMessageList(prioritizedMessageList, elevators.indexOf(elevator)) && elevator.getDirection() == desiredDirection && elevator.getDirection() == requestDirection && Math.abs(originFloor - elevator.getCurrentFloor()) == minOriginDistance) {
                DirectionEnum direction = (originFloor > destinationFloor) ? DirectionEnum.DOWN_DIRECTION : DirectionEnum.UP_DIRECTION;
                prioritizedMessageList.add(new ServiceFloorRequestMessage(originFloor, destinationFloor, direction, elevators.indexOf(elevator)));
                System.out.println("Elevator " + elevators.indexOf(elevator) + " has priority 1");
            }
        }

        //send it to the closest idle elevator, if there are any idle
        //closest means the elevator's current floor is the closest to the request's origin floor
        //direction moved after arriving at origin doesn't matter because it was empty
        for (SchedulerElevatorData elevator : elevators) {
            if (!elevator.getTransientFaulted() && !elevator.getHardFaulted() && !checkElevatorInRequestMessageList(prioritizedMessageList, elevators.indexOf(elevator)) && elevator.getDirection() == DirectionEnum.IDLE_DIRECTION && Math.abs(originFloor - elevator.getCurrentFloor()) == minOriginDistance) {
                DirectionEnum direction = (originFloor > destinationFloor) ? DirectionEnum.DOWN_DIRECTION : DirectionEnum.UP_DIRECTION;
                prioritizedMessageList.add(new ServiceFloorRequestMessage(originFloor, destinationFloor, direction, elevators.indexOf(elevator)));
                System.out.println("Elevator " + elevators.indexOf(elevator) + " has priority 2");
            }
        }

        //below should be changed to not be necessary with an alternate way to handle requests
        //send it to the closest elevator whose last destination is closest to the requesting floor
        //can move in the opposite direction after arriving at origin, which is not ideal, but guarantees that it is serviced
        for (SchedulerElevatorData elevator : elevators) {
            //it should be guaranteed that there is such an elevator
            if (!elevator.getTransientFaulted() && !elevator.getHardFaulted() && !checkElevatorInRequestMessageList(prioritizedMessageList, elevators.indexOf(elevator)) && elevator.getDestinationFloor().size() != 0 && Math.abs(originFloor - elevator.getDestinationFloor().get(elevator.getDestinationFloor().size() - 1)) == minDestinationDistance) {
                DirectionEnum direction = (originFloor > destinationFloor) ? DirectionEnum.DOWN_DIRECTION : DirectionEnum.UP_DIRECTION;
                prioritizedMessageList.add(new ServiceFloorRequestMessage(originFloor, destinationFloor, direction, elevators.indexOf(elevator)));
                System.out.println("Elevator " + elevators.indexOf(elevator) + " has priority 3");
            }
        }

        //try every other remaining elevator
        for (SchedulerElevatorData elevator : elevators) {
            //it should be guaranteed that there is such an elevator
            if (!elevator.getTransientFaulted() && !elevator.getHardFaulted() && !checkElevatorInRequestMessageList(prioritizedMessageList, elevators.indexOf(elevator))) {
                DirectionEnum direction = (originFloor > destinationFloor) ? DirectionEnum.DOWN_DIRECTION : DirectionEnum.UP_DIRECTION;
                prioritizedMessageList.add(new ServiceFloorRequestMessage(originFloor, destinationFloor, direction, elevators.indexOf(elevator)));
                System.out.println("Elevator " + elevators.indexOf(elevator) + " has priority 4");
            }
        }

        String priorityMsg = "Elevator Priorities for " + originFloor + " to " + destinationFloor + ": ";

        for (ServiceFloorRequestMessage msg : prioritizedMessageList) {
            priorityMsg += msg.getElevatorId() + " ";
        }

        System.out.println(priorityMsg);

        return prioritizedMessageList;
    }

    /**
    * Loop below indefinitely:
    * 1. Get floor request
    * 2. Loop below until an elevator has accepted a request
    * 3. Get elevator state (waits until state has changed since last elevator state get)
    * 4. Form ordered list of requests to send to elevators based on 1 and 3
    * 5. Repeat below until an elevator has accepted a request
    * 6. Send a request to an elevator based on 4 (try the next elevator if denied)
    */
    @Override
    public void run() {
        while (true) {
            byte[] firstRequest = floorBufferCommunicator.getUDPMessage();

            try {
                Message firstMessage = SerializeUtils.deserialize(firstRequest);
                FloorDataMessage firstRequestMessage = (FloorDataMessage) firstMessage;

                boolean acceptedRequest = false;

                while (!acceptedRequest) {

                    ArrayList<SchedulerElevatorData> elevators = schedulerSystem.getElevatorData(); //todo: break this wait if there's a different request we could try to fulfill?
                    List<ServiceFloorRequestMessage> requestMessages = makeRequest(firstRequestMessage, elevators);
                    int i = 0;

                    while (!acceptedRequest && i < requestMessages.size()) {
                        System.out.println(requestMessages.get(i));
                        elevatorBufferCommunicator.sendUDPMessage(SerializeUtils.serialize(requestMessages.get(i)));
                        acceptedRequest = schedulerSystem.getRequestResponse(requestMessages.get(i).getRequestID()); //message ids need to be implemented
                        //System.out.printf("Request %d handled \n", requestMessage.getRequestID());
                        i++;
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}