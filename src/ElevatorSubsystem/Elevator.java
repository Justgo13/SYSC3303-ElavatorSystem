/**
 * 
 */
package ElevatorSubsystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import Messages.*;

/**
 * @author Harjap Gill, Jason Gao
 * 
 *         Elevator class that holds the state of an elevator
 *
 */
public class Elevator implements Runnable {
	private final static int DISTANCE_BTWN_FLOOR = 4;
	private final static double SPEED_M_PER_SEC = 1.42;
	private final static double TIME_PER_FLOOR_SEC = DISTANCE_BTWN_FLOOR / SPEED_M_PER_SEC; // 2.816 s
	private final static double TIME_PER_FLOOR_MS = TIME_PER_FLOOR_SEC * 1000; // 2816 ms

	private static enum STATES {
		IDLE, MOVING, STOPPED, DOORS_OPEN, DOORS_CLOSED
	};
	
	private boolean movingInterrupt;
	private boolean doorOpen;
	private int elevatorId;
	private int currentFloor;
	private String direction;

	private long departureTime;

	/**
	 * Buffer holding the floors this elevator is planning on servicing
	 */
	private ArrayList<Integer> floorBuffer;

	/**
	 * The buffer holding floor requests from the elevator system
	 */
	private Queue<Message> floorRequestBuffer;

	/**
	 * Buffer holding messages sent by elevator to elevator system
	 */
	private ArrayList<Message> elevatorResponseBuffer;

	/**
	 * Current state of the elevator
	 */
	private STATES currentState;

	/**
	 * Constructor to create elevator object
	 * 
	 * @param id       of elevator to create
	 * @param doorOpen boolean of elevator door state
	 */
	public Elevator(int id, boolean doorOpen) {
		this.elevatorId = id;
		this.doorOpen = doorOpen;
		this.floorBuffer = new ArrayList<>();
		this.floorRequestBuffer = new LinkedList<>();
		this.elevatorResponseBuffer = new ArrayList<>();
		this.currentState = STATES.IDLE; // TODO might need to pass state in
		this.direction = "";
		this.departureTime = 0;
		this.movingInterrupt = false;
	}

	/**
	 * Elevator system adding floor requests to an elevator
	 * 
	 * @param msg The message the elevator system sends
	 */
	public synchronized void putFloorRequest(Message msg) {
		this.floorRequestBuffer.add(msg);
	}

	/**
	 * Get the message the elevator responds with
	 * 
	 * @return A confirmation message
	 */
	public synchronized Message getConfirmationMessage() {
		while (this.elevatorResponseBuffer.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println(e);
				return null;
			}
		}
		return this.elevatorResponseBuffer.get(0);
	}

	/**
	 * Add a confirmation message if elevator can handle the request
	 * 
	 * @param msg The confirmation message to add
	 */
	private synchronized void putConfirmationMessage(Message msg) {
		this.elevatorResponseBuffer.add(msg);
	}

	private synchronized Message getFloorRequest(long timeToTravel) {
		while (this.floorRequestBuffer.isEmpty()) {
			try {
				wait(timeToTravel);
			} catch (InterruptedException e) {
				System.out.println(e);
				return null;
			}
		}
		return this.floorRequestBuffer.remove();
	}

	/**
	 * @param floorBuffer add floor
	 */
	private void addFloor(int floor) {
		if (!this.floorBuffer.contains(floor)) {
			this.floorBuffer.add(floor);
		}
		
	}

	/**
	 * Remove first floor
	 * 
	 * @return int The first floor
	 */
	private int removeFirstFloor() {
		return this.floorBuffer.remove(0);
	}

	public boolean processServiceRequest(ServiceFloorRequestMessage serviceFloorRequestMessage) {
		// look at current elevator state, time passed
		return true;
	}

	/**
	 * Set the state of the elevator's door
	 * 
	 * @param open True if door is open, false if not
	 */
	public void setDoorOpen(boolean open) {
		this.doorOpen = open;
	}

	/**
	 * @return the floorBuffer
	 */
	public ArrayList<Integer> getFloorBuffer() {
		return floorBuffer;
	}

	/**
	 * @param elevatorId the elevatorId to set
	 */
	public void setElevatorId(int elevatorId) {
		this.elevatorId = elevatorId;
	}

	/**
	 * 
	 * @return the state of the door, True if open, False if closed
	 */
	public boolean getDoorOpen() {
		return this.doorOpen;
	}

	/**
	 * 
	 * @return the id of the elevator
	 */
	public int getElevatorId() {
		return this.elevatorId;
	}

	/**
	 * @return the currentFloor
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}

	/**
	 * @param currentFloor the currentFloor to set
	 */
	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	
	private <T> void ascendingSortInt(ArrayList<Integer> arr) {
		Collections.sort(arr);
	}
	
	private void descendingSortInt(ArrayList<Integer> arr) {
		Collections.sort(arr, Collections.reverseOrder());
	}
	
	/**
	 * @return the movingInterrupt
	 */
	private boolean isMovingInterrupt() {
		return movingInterrupt;
	}

	/**
	 * @param movingInterrupt the movingInterrupt to set
	 */
	private void setMovingInterrupt(boolean movingInterrupt) {
		this.movingInterrupt = movingInterrupt;
	}

	
	
	
	@Override
	public void run() {
		while (true) {
			// IDLE, MOVING, STOPPED, DOORS_OPEN, DOORS_CLOSED
			switch (this.currentState) {
			case IDLE: {
				this.setMovingInterrupt(false);
				ServiceFloorRequestMessage msg = (ServiceFloorRequestMessage) getFloorRequest(0); // wait time = 0 means
																									// wait indefinitely
				int srcFloor = msg.getFloorNumber();
				int destFloor = msg.getDestinationNumber();

				// check the direction the elevator should initially be
				// going regardless of requested direction
				if (srcFloor == this.currentFloor) {
					// open doors
					this.currentState = STATES.DOORS_OPEN;
					this.addFloor(destFloor);
				} else if (srcFloor > this.currentFloor && srcFloor < destFloor) {
					// elevator idle on floor 3, request wants elevator to start at floor 5
					this.addFloor(srcFloor);
					this.addFloor(destFloor);
					this.direction = "up";
					this.currentState = STATES.MOVING;
				} else if (srcFloor < this.currentFloor && srcFloor > destFloor) {
					// elevator idle on floor 3, request wants elevator to start at floor 1
					this.addFloor(srcFloor);
					this.addFloor(destFloor);
					this.direction = "down";
					this.currentState = STATES.MOVING;
				} else {
					System.out.printf("Invalid floor combo src: %d dest: %d", srcFloor, destFloor);
					break;
				}

				AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(this.getElevatorId(),
						this.getCurrentFloor(), this.getFloorBuffer());
				this.putConfirmationMessage(acceptMsg);

				break;
			}

			case MOVING: {
				
				int nextFloor = this.floorBuffer.get(0);
				int floorsToTravel = Math.abs(this.currentFloor - nextFloor);
				long timeToTravel = (long) (floorsToTravel * DISTANCE_BTWN_FLOOR * Math.pow(SPEED_M_PER_SEC, -1)
						* 1000);
				// ie if floor to travel is 3, time = 3 * 4 m * 1/1.42 s/m = 8.45 or 8450 ms

				// star timer
				this.departureTime = System.currentTimeMillis(); // time in millis when elevator starts moving
				ServiceFloorRequestMessage msg = (ServiceFloorRequestMessage) getFloorRequest(timeToTravel);

				if (msg == null) {
					// we reached the first floor in floorBuffer
					//int floor = this.removeFirstFloor(); // TODO move to Stopped state
					this.currentState = STATES.STOPPED;
					//this.currentFloor = floor; // TODO move to Stopped state
					// TODO Send arrival event to scheduler
					break;
				} else {
					int srcFloor = msg.getFloorNumber();
					int destFloor = msg.getDestinationNumber();
					String direction = msg.getDirection();

					// still moving
					/**
					 * example
					 * 
					 * request is for moving from floor 2 to floor 9 we are at currently at floor 2
					 * 
					 * let time since departure to current time be 5000 ms let floors traveled be
					 * 1.76, with floor division it would be 1
					 * 
					 * The current floor would be current floor + floors traveled (2 +- 1) = 3
					 * 
					 * Example use cases: i. If we get a request to go up from floor 3 to 5, we
					 * decline since we are already past floor 3
					 * 
					 * ii. If we get a request to go up from floor 4 to 5, we accept since we
					 * haven't got to floor 4
					 * 
					 * iii. Decline requests that are in the opposite direction
					 *
					 */

					long currTime = System.currentTimeMillis(); // end timer
					long timeDiff = currTime - departureTime; // time from starting to move to floor request received
																// time

					double floorsTravelled = timeDiff / TIME_PER_FLOOR_MS; // i.e 5000 ms / 2819 ms

					// this is the floor we can no longer service
					int currFloor = this.direction == "up" ? this.currentFloor + (int) Math.floor(floorsTravelled)
							: this.currentFloor - (int) Math.floor(floorsTravelled);

					// check if request direction matches elevator direction
					if (!this.direction.equals(direction)) {
						DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(this.getElevatorId(),
								this.getCurrentFloor(), this.getFloorBuffer());
						this.putConfirmationMessage(acceptMsg);
					} else {
						// check if we can service request, request direction same as elevator direction
						switch (direction) {
						case "up":
							if (srcFloor > currFloor) {
								// can handle request
								this.addFloor(srcFloor);
								this.addFloor(destFloor);
								
								ascendingSortInt(this.floorBuffer);
								
								AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(
										this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
								this.putConfirmationMessage(acceptMsg);
							} else {
								// can't handle request
								DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(
										this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
								this.putConfirmationMessage(acceptMsg);
							}
							break;
						case "down":
							if (srcFloor < currFloor) {
								// can handle request
								this.addFloor(srcFloor);
								this.addFloor(destFloor);
								
								descendingSortInt(this.floorBuffer);
								
								AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(
										this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
								this.putConfirmationMessage(acceptMsg);
							} else {
								// can't handle request
								DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(
										this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
								this.putConfirmationMessage(acceptMsg);
							}
							break;
						default:
							break;
						}
					}

				}
				break;
			}
			case STOPPED:

				break;

			case DOORS_OPEN:

				break;

			case DOORS_CLOSED:

				break;

			default:
				break;
			}
		}

	}

	
}
