/**
 * 
 */
package ElevatorSubsystem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import Messages.*;
import SharedResources.*;

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
	// Amount of time doors stay open
	private final static double TIME_DOORS_OPEN_MS = 9.175 * 1000;

	private static enum STATES {
		IDLE, MOVING, STOPPED, DOORS_OPEN, DOORS_CLOSED
	};

	private ByteBufferCommunicator schedulerBuffer;
	private boolean interruptedWhileMoving;
	private boolean interruptedWhileDoorsOpen;
	private boolean doorOpen;
	private int elevatorId;
	private int currentFloor;
	private String direction;

	private long departureTime;
	private long doorsOpenTime;

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
	public Elevator(int id, boolean doorOpen, ByteBufferCommunicator schedulerBuffer, int currentFloor) {
		this.elevatorId = id;
		this.doorOpen = doorOpen;
		this.floorBuffer = new ArrayList<>();
		this.floorRequestBuffer = new LinkedList<>();
		this.elevatorResponseBuffer = new ArrayList<>();
		this.currentState = STATES.IDLE; // TODO might need to pass state in
		this.direction = "";
		this.currentFloor = currentFloor;
		this.departureTime = 0;
		this.interruptedWhileMoving = false;
		this.interruptedWhileDoorsOpen = false;
		this.schedulerBuffer = schedulerBuffer;
	}

	/**
	 * Elevator system adding floor requests to an elevator
	 * 
	 * @param msg The message the elevator system sends
	 */
	public synchronized void putFloorRequest(Message msg) {
		this.floorRequestBuffer.add(msg);
		notifyAll();
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
		notifyAll();
		return this.elevatorResponseBuffer.remove(0);
//		return this.elevatorResponseBuffer.get(0);
	}

	/**
	 * Add a confirmation message if elevator can handle the request
	 * 
	 * @param msg The confirmation message to add
	 */
	private synchronized void putConfirmationMessage(Message msg) {
		this.elevatorResponseBuffer.add(msg);
		notifyAll();
	}

	private synchronized Message getFloorRequestTimed(long timeToTravel, long departureTime) {
		while (this.floorRequestBuffer.isEmpty()) {
			try {
				if (timeToTravel != 0 && System.currentTimeMillis() - departureTime >= timeToTravel) {
					throw new TimeoutException();
				}
				wait(timeToTravel);
			} catch (InterruptedException e) {
				return null;
			} catch (TimeoutException e) {
				// This exception is throw when wait() time has exceeded timeToTravel
				// TODO determine if we need notifyAll()
				// TODO see if we are breaking functionality by no longer properly handling
				// Interruped Exception
				notifyAll();
				return null;
			}
		}
		notifyAll();
		return this.floorRequestBuffer.remove();
	}

	/**
	 * @param floorBuffer add floor
	 */
	private void addToFloorBufferHead(int floor) {
		if (!this.floorBuffer.contains(floor)) {
			this.floorBuffer.add(0, floor);
		}
	}

	/**
	 * Remove first floor
	 * 
	 * @return int The first floor
	 */
	private int removeFloorBufferHead() {
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

	@Override
	public void run() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

		while (true) {
			// IDLE, MOVING, STOPPED, DOORS_OPEN, DOORS_CLOSED
			switch (this.currentState) {
			case IDLE: {
				System.out.println("State: Idle -> " + formatter.format(new Date(System.currentTimeMillis())));
				this.interruptedWhileMoving = false;
				ServiceFloorRequestMessage msg = (ServiceFloorRequestMessage) getFloorRequestTimed(0, 0); // wait time =
																											// 0 means
				// wait indefinitely
				int srcFloor = msg.getFloorNumber();
				int destFloor = msg.getDestinationNumber();

				// check the direction the elevator should initially be
				// going regardless of requested direction
				if (srcFloor == this.currentFloor) {
					// open doors
					this.currentState = STATES.DOORS_OPEN;
					this.addToFloorBufferHead(destFloor);
				} else if (srcFloor > this.currentFloor) {
					// elevator idle on floor 3, request wants elevator to start at floor 5
					this.addToFloorBufferHead(destFloor);
					this.addToFloorBufferHead(srcFloor);
					this.direction = "up";
					this.currentState = STATES.MOVING;
				} else if (srcFloor < this.currentFloor) {
					// elevator idle on floor 3, request wants elevator to start at floor 1
					this.addToFloorBufferHead(destFloor);
					this.addToFloorBufferHead(srcFloor);
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
				if (!this.interruptedWhileMoving) {
					this.departureTime = System.currentTimeMillis();
				}
				System.out.println("State: Moving -> " + formatter.format(new Date(System.currentTimeMillis())));

				// The floor the elevator is currently attempting to reach
				int destFloor = this.floorBuffer.get(0);

				// Setting direction based destination floor
				this.direction = this.currentFloor > destFloor ? "down" : "up";
				int floorsToTravel = Math.abs(this.currentFloor - destFloor);
				long timeToTravel = (long) (floorsToTravel * DISTANCE_BTWN_FLOOR * Math.pow(SPEED_M_PER_SEC, -1)
						* 1000);
				// ie if floor to travel is 3, time = 3 * 4 m * 1/1.42 s/m = 8.45 or 8450 ms

				//
				if (this.interruptedWhileMoving) {
					timeToTravel = timeToTravel - (System.currentTimeMillis() - this.departureTime);

					if (timeToTravel <= 0) {
						// TODO handle case of negative time to travel
						continue;
					}
				}

				ServiceFloorRequestMessage msg = (ServiceFloorRequestMessage) getFloorRequestTimed(timeToTravel,
						System.currentTimeMillis());

				if (msg == null) {
					// we reached the first floor in floorBuffer
					// int floor = this.removeFirstFloor(); // TODO move to Stopped state
					this.currentState = STATES.STOPPED;
					// this.currentFloor = floor; // TODO move to Stopped state
					// TODO Send arrival event to scheduler
					break;
				} else {
					// We set our state to say we have interrupted our moving state with a message
					this.interruptedWhileMoving = true;

					int srcFloorMessage = msg.getFloorNumber();
					int destFloorMessage = msg.getDestinationNumber();
					String directionMessage = msg.getDirection();

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
					if (!this.direction.equals(directionMessage)) {
						DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(this.getElevatorId(),
								this.getCurrentFloor(), this.getFloorBuffer());
						this.putConfirmationMessage(acceptMsg);
					} else {
						// check if we can service request, request direction same as elevator direction
						switch (this.direction) {
						case "up":
							// if the floor request is in the same direction of travel, and both the
							// requested floors are between
							// our current location and our destination, service the request
							if (srcFloorMessage > currFloor && destFloorMessage <= destFloor) {
								this.addToFloorBufferHead(destFloorMessage);
								this.addToFloorBufferHead(srcFloorMessage);

								AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(
										this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
								this.putConfirmationMessage(acceptMsg);
							} else {
								DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(
										this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
								this.putConfirmationMessage(acceptMsg);
							}
							break;

						case "down":
							// if the floor request is in the same direction of travel, and both the
							// requested floors are between
							// our current location and our destination, service the request
							if (srcFloorMessage < currFloor && destFloorMessage >= destFloor) {
								this.addToFloorBufferHead(destFloorMessage);
								this.addToFloorBufferHead(srcFloorMessage);

								AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(
										this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
								this.putConfirmationMessage(acceptMsg);
							} else {
								DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(
										this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
								this.putConfirmationMessage(acceptMsg);
							}
							break;
						}
					}

				}
				break;
			}
			case STOPPED:

				int floor = this.removeFloorBufferHead();
				System.out.println("State: Stopped at floor " + floor + " FloorBuffer: " + this.floorBuffer.toString()
						+ " -> " + formatter.format(new Date(System.currentTimeMillis())));
				this.setCurrentFloor(floor);
				ArrivalElevatorMessage arrivalMessage = new ArrivalElevatorMessage(this.getElevatorId(),
						this.getCurrentFloor(), this.getFloorBuffer());

				try {
					this.schedulerBuffer.putResponseBuffer(SerializeUtils.serialize(arrivalMessage));
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.currentState = STATES.DOORS_OPEN;
				break;

			case DOORS_OPEN:
				System.out.println("State: DoorsOpen -> " + formatter.format(new Date(System.currentTimeMillis())));
				this.doorOpen = true;
				if (!this.interruptedWhileDoorsOpen) {
					this.doorsOpenTime = System.currentTimeMillis();
				}

				long timeToWait = (long) TIME_DOORS_OPEN_MS;

				// If we were interrupted while waiting, reduce wait time by how long we have
				// already waited
				if (this.interruptedWhileDoorsOpen) {
					timeToWait = timeToWait - (System.currentTimeMillis() - this.doorsOpenTime);

					if (timeToWait <= 0) {
						// TODO handle case of negative time to wait
						continue;
					}
				}

				ServiceFloorRequestMessage msg = (ServiceFloorRequestMessage) getFloorRequestTimed(timeToWait,
						System.currentTimeMillis());
				if (msg == null) {
					this.currentState = STATES.DOORS_CLOSED;
					break;
				} else {
					// We set our state to say we have interrupted our moving state with a message
					this.interruptedWhileDoorsOpen = true;

					int srcFloorMessage = msg.getFloorNumber();
					int destFloorMessage = msg.getDestinationNumber();
					String directionMessage = msg.getDirection();

					// If we have no scheduled floors, then we will always accept
					if (this.floorBuffer.isEmpty()) {
						this.addToFloorBufferHead(destFloorMessage);
						this.addToFloorBufferHead(srcFloorMessage);

						AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(this.getElevatorId(),
								this.getCurrentFloor(), this.getFloorBuffer());
						this.putConfirmationMessage(acceptMsg);
					} else {
						int destFloor = this.floorBuffer.get(0);
						this.direction = this.currentFloor > destFloor ? "down" : "up";

						// check if request direction matches elevator direction
						if (!this.direction.equals(directionMessage)) {
							DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(this.getElevatorId(),
									this.getCurrentFloor(), this.getFloorBuffer());
							this.putConfirmationMessage(acceptMsg);
						} else {
							// check if we can service request, request direction same as elevator direction
							switch (this.direction) {
							case "up":
								// if the floor request is in the same direction of travel, and both the
								// requested floors are between
								// our current location and our destination, service the request
								if (srcFloorMessage >= this.currentFloor && destFloorMessage <= destFloor) {
									this.addToFloorBufferHead(destFloorMessage);
									// Don't add srcFloor if we are already at that floor with doorsOpen
									if (this.currentFloor != srcFloorMessage) {
										this.addToFloorBufferHead(srcFloorMessage);
									}

									AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(
											this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
									this.putConfirmationMessage(acceptMsg);
								} else {
									DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(
											this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
									this.putConfirmationMessage(acceptMsg);
								}
								break;

							case "down":
								// if the floor request is in the same direction of travel, and both the
								// requested floors are between
								// our current location and our destination, service the request
								if (srcFloorMessage <= this.currentFloor && destFloorMessage >= destFloor) {
									this.addToFloorBufferHead(destFloorMessage);
									// Don't add srcFloor if we are already at that floor with doorsOpen
									if (this.currentFloor != srcFloorMessage) {
										this.addToFloorBufferHead(srcFloorMessage);
									}

									AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(
											this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
									this.putConfirmationMessage(acceptMsg);
								} else {
									DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(
											this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
									this.putConfirmationMessage(acceptMsg);
								}
								break;
							}
						}
					}
				}
				break;

			case DOORS_CLOSED:
				System.out.println("State: DoorsClosed -> " + formatter.format(new Date(System.currentTimeMillis())));
				this.interruptedWhileDoorsOpen = false;
				this.interruptedWhileMoving = false;

				if (this.floorBuffer.isEmpty()) {
					this.currentState = STATES.IDLE;
				} else {
					this.currentState = STATES.MOVING;
				}
				break;

			default:
				break;
			}
		}

	}

}
