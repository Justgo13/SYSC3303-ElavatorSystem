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
 * Elevator class which handles the state of an individual elevator along
 * receiving and responding to messages from the ElevatorSystem
 * 
 * @author Harjap Gill, Jason Gao
 * 
 *
 */
public class Elevator implements Runnable {
	/** Distance in meters between floors */
	private final static int DISTANCE_BTWN_FLOOR = 4;
	/** Speed in meters/sec of the elevator */
	private final static double SPEED_M_PER_SEC = 1.42;
	/** Amount of time it takes to travel 1 floor: 2.816 sec */
	private final static double TIME_PER_FLOOR_SEC = DISTANCE_BTWN_FLOOR / SPEED_M_PER_SEC;
	/** Amount of time it takes to travel 1 floor in milliseconds: 2816 ms */
	private final static double TIME_PER_FLOOR_MS = TIME_PER_FLOOR_SEC * 1000;
	/** Amount of time doors stay open in milliseconds */
	private final static double TIME_DOORS_OPEN_MS = 9.175 * 1000;

	/** Different states elevator can be in */
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

	/**
	 * Time stamp from when elevator first starts moving
	 */
	private long departureTime;

	/**
	 * Time stamp from when door is first opened
	 */
	private long doorsOpenTime;

	/**
	 * Buffer holding the floors this elevator is planning on servicing in order
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

	/**
	 * Begin a wait that can be interrupted by a floorRequest being received or by the 
	 * declared timeout time being elapsed
	 * 
	 * @param timeToTravel The max wait time in milliseconds after which wait will return
	 * @param departureTime Initial time before wait begins (created by System.getCurrentTimeInMillis())
	 * @return null if timeout reached or returns Message if message was received during wait
	 */
	private synchronized Message getFloorRequestTimed(long timeToTravel, long departureTime) {
		while (this.floorRequestBuffer.isEmpty()) {
			try {
				// If the amount of time so far that has passed exceeds timeToTravel, we throw TimeoutException
				if (timeToTravel != 0 && System.currentTimeMillis() - departureTime >= timeToTravel) {
					throw new TimeoutException();
				}
				wait(timeToTravel);
			} catch (InterruptedException e) {
				return null;
			} catch (TimeoutException e) {
				// This exception is throw when wait() time has exceeded timeToTravel
				notifyAll();
				return null;
			}
		}
		notifyAll();
		return this.floorRequestBuffer.remove();
	}

	/**
	 * Add floor to front of floor buffer
	 * 
	 * @param floor add floor
	 */
	private void addToFloorBufferHead(int floor) {
		if (!this.floorBuffer.contains(floor)) {
			this.floorBuffer.add(0, floor);
		}
	}

	/**
	 * Remove first floor from floor Buffer
	 * 
	 * @return The first floor
	 */
	private int removeFloorBufferHead() {
		return this.floorBuffer.remove(0);
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

	
	/**
	 * Loops through all the different states of the Elevator
	 */
	@Override
	public void run() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

		while (true) {
			// IDLE, MOVING, STOPPED, DOORS_OPEN, DOORS_CLOSED
			switch (this.currentState) {
			case IDLE: {
				System.out.println("Elevator " + this.elevatorId + ": Idle -> " + formatter.format(new Date(System.currentTimeMillis())));
				this.interruptedWhileMoving = false;
				
				// Wait until we receive a ServiceFloorRequest
				ServiceFloorRequestMessage msg = (ServiceFloorRequestMessage) getFloorRequestTimed(0, 0); 
				
				int srcFloor = msg.getFloorNumber();
				int destFloor = msg.getDestinationNumber();
				int msgID = msg.getRequestID();


				if (srcFloor == this.currentFloor) {
					// If the requested floor is the same as current floor, go directly to DoorOpen state
					this.currentState = STATES.DOORS_OPEN;
					this.addToFloorBufferHead(destFloor);
				} else if (srcFloor > this.currentFloor) {
					// Travel Up
					this.addToFloorBufferHead(destFloor);
					this.addToFloorBufferHead(srcFloor);
					this.direction = "up";
					this.currentState = STATES.MOVING;
				} else if (srcFloor < this.currentFloor) {
					// Travel Down
					this.addToFloorBufferHead(destFloor);
					this.addToFloorBufferHead(srcFloor);
					this.direction = "down";
					this.currentState = STATES.MOVING;
				} else {
					System.out.printf("Elevator " + this.elevatorId + ": Invalid floor combo src: %d dest: %d", srcFloor, destFloor);
					break;
				}
				
				// Send Accept Response
				AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(msgID, this.getElevatorId(),
						this.getCurrentFloor(), this.getFloorBuffer());
				this.putConfirmationMessage(acceptMsg);

				break;
			}

			case MOVING: {
				// If we are entering Moving state for the first time, set departureTime
				if (!this.interruptedWhileMoving) {
					this.departureTime = System.currentTimeMillis();
				}
				System.out.println("Elevator " + this.elevatorId + ": State: Moving -> " + formatter.format(new Date(System.currentTimeMillis())));

				// Floor elevator is going to move towards
				int destFloor = this.floorBuffer.get(0);

				// Setting direction based destination floor
				this.direction = this.currentFloor > destFloor ? "down" : "up";
				
				// Calculate how long the elevator must move for to reach the destination floor
				int floorsToTravel = Math.abs(this.currentFloor - destFloor);
				long timeToTravel = (long) (floorsToTravel * DISTANCE_BTWN_FLOOR * Math.pow(SPEED_M_PER_SEC, -1)
						* 1000);
				
				// If we have been in the Moving state before, subtract timeToTravel by the length of time we already waited
				if (this.interruptedWhileMoving) {
					timeToTravel = timeToTravel - (System.currentTimeMillis() - this.departureTime);

					if (timeToTravel <= 0) {
						// TODO handle case of negative time to travel
						continue;
					}
				}

				ServiceFloorRequestMessage msg = (ServiceFloorRequestMessage) getFloorRequestTimed(timeToTravel, System.currentTimeMillis());
				
				if (msg == null) {
					// We have reached our destination floor and will now stop
					this.currentState = STATES.STOPPED;
					break;
				} else {
					// We have received a ServiceFloorRequest WHILE we are still moving in the elevator
					this.interruptedWhileMoving = true;

					int srcFloorMessage = msg.getFloorNumber();
					int destFloorMessage = msg.getDestinationNumber();
					String directionMessage = msg.getDirection();
					int msgID = msg.getRequestID();

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

					long currTime = System.currentTimeMillis();
					
					// The amount of time we have already spent moving in the elevator
					long timeDiff = currTime - departureTime; 

					// The amount floors we have traveled in the elevator
					double floorsTravelled = timeDiff / TIME_PER_FLOOR_MS; // i.e 5000 ms / 2819 ms

					// The floor we CANNOT SERVICE
					// While moving in the elevator, we have already passed this floor
					int currFloor = this.direction == "up" ? this.currentFloor + (int) Math.floor(floorsTravelled)
							: this.currentFloor - (int) Math.floor(floorsTravelled);

					// If the direction of the request does not match elevator direction, Decline request
					if (!this.direction.equals(directionMessage)) {
						DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(msgID,
								this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
						this.putConfirmationMessage(acceptMsg);
					} else {
						// Request direction matches elevator movement direction
						switch (this.direction) {
							case "up":
								// Service request if both request floors are between our currentFloor and our
								// destination floor
								if (srcFloorMessage > currFloor && destFloorMessage <= destFloor) {
									this.addToFloorBufferHead(destFloorMessage);
									this.addToFloorBufferHead(srcFloorMessage);
	
									AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(msgID,
											this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
									this.putConfirmationMessage(acceptMsg);
								} else {
									DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(msgID,
											this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
									this.putConfirmationMessage(acceptMsg);
								}
								break;
	
							case "down":
								// Service request if both request floors are between our currentFloor and our
								// destination floor
								if (srcFloorMessage < currFloor && destFloorMessage >= destFloor) {
									this.addToFloorBufferHead(destFloorMessage);
									this.addToFloorBufferHead(srcFloorMessage);
	
									AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(msgID,
											this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
									this.putConfirmationMessage(acceptMsg);
								} else {
									DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(msgID,
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
				// Elevator has now stopped
				
				// Remove the floor we have stopped at from the floorBuffer
				int floor = this.removeFloorBufferHead();
				
				System.out.println("Elevator " + this.elevatorId + ": State: Stopped at floor " + floor + " FloorBuffer: " + this.floorBuffer.toString()
						+ " -> " + formatter.format(new Date(System.currentTimeMillis())));
				this.setCurrentFloor(floor);
				
				// Send an Arrival message to notify that we have reached a floor
				ArrivalElevatorMessage arrivalMessage = new ArrivalElevatorMessage(this.getElevatorId(),
						this.getCurrentFloor(), this.getFloorBuffer());

				try {
					this.schedulerBuffer.sendUDPMessage(SerializeUtils.serialize(arrivalMessage));
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.currentState = STATES.DOORS_OPEN;
				break;

			case DOORS_OPEN:
				// Elevator doors are now open
				System.out.println("Elevator " + this.elevatorId + ": State: DoorsOpen -> " + " FloorBuffer: " + this.floorBuffer.toString() + " "
						+ formatter.format(new Date(System.currentTimeMillis())));
				this.doorOpen = true;
				
				// If we are entering DoorOpen state for the first time, set doorOpenTime
				if (!this.interruptedWhileDoorsOpen) {
					this.doorsOpenTime = System.currentTimeMillis();
				}

				long timeToWait = (long) TIME_DOORS_OPEN_MS;

				// If we were in the DoorOpen state last state, reduce timeToWait by amount of time we already waited
				if (this.interruptedWhileDoorsOpen) {
					timeToWait = timeToWait - (System.currentTimeMillis() - this.doorsOpenTime);

					if (timeToWait <= 0) {
						// TODO handle case of negative time to wait
						continue;
					}
				}
				
				ServiceFloorRequestMessage msg = (ServiceFloorRequestMessage) getFloorRequestTimed(timeToWait, System.currentTimeMillis());
				
				
				if (msg == null) {
					// We waited with doors open for the set time and doors will now close
					this.currentState = STATES.DOORS_CLOSED;
					break;
				} else {
					// We received a message while we were waiting with doors open
					
					// Set flag indicating we were interrupted while waiting with doors open
					this.interruptedWhileDoorsOpen = true;

					int srcFloorMessage = msg.getFloorNumber();
					int destFloorMessage = msg.getDestinationNumber();
					String directionMessage = msg.getDirection();
					int msgID = msg.getRequestID();

					// If we have no scheduled floors, then we will always accept floor request
					if (this.floorBuffer.isEmpty()) {
						this.addToFloorBufferHead(destFloorMessage);
						
						// If request is for a floor we are currently servicing with the door open, we do not add it to floorBuffer
						if (srcFloorMessage != this.currentFloor) {
							this.addToFloorBufferHead(srcFloorMessage);
						}
						
						// Send AcceptRequest message
						AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(msgID, this.getElevatorId(),
								this.getCurrentFloor(), this.getFloorBuffer());
						this.putConfirmationMessage(acceptMsg);
					} else {
						
						int destFloor = this.floorBuffer.get(0);
						// Direction elevator will travel once doors close
						this.direction = this.currentFloor > destFloor ? "down" : "up";

						// If direction of request does not match our intended next direction, Decline request
						if (!this.direction.equals(directionMessage)) {
							DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(msgID,
									this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
							this.putConfirmationMessage(acceptMsg);
						} else {
							switch (this.direction) {
								case "up":
									// Service request if both request floors are between our currentFloor and our
									// destination floor
									if (srcFloorMessage >= this.currentFloor && destFloorMessage <= destFloor) {
										this.addToFloorBufferHead(destFloorMessage);
										// Don't add srcFloor if we are already at that floor with doorsOpen
										if (this.currentFloor != srcFloorMessage) {
											this.addToFloorBufferHead(srcFloorMessage);
										}
	
										AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(msgID,
												this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
										this.putConfirmationMessage(acceptMsg);
									} else {
										DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(msgID,
												this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
										this.putConfirmationMessage(acceptMsg);
									}
									break;
	
								case "down":
									// Service request if both request floors are between our currentFloor and our
									// destination floor
									if (srcFloorMessage <= this.currentFloor && destFloorMessage >= destFloor) {
										this.addToFloorBufferHead(destFloorMessage);
										// Don't add srcFloor if we are already at that floor with doorsOpen
										if (this.currentFloor != srcFloorMessage) {
											this.addToFloorBufferHead(srcFloorMessage);
										}
	
										AcceptFloorRequestMessage acceptMsg = new AcceptFloorRequestMessage(msgID,
												this.getElevatorId(), this.getCurrentFloor(), this.getFloorBuffer());
										this.putConfirmationMessage(acceptMsg);
									} else {
										DeclineFloorRequestMessage acceptMsg = new DeclineFloorRequestMessage(msgID,
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
				System.out.println("Elevator " + this.elevatorId + ": State: DoorsClosed -> " + formatter.format(new Date(System.currentTimeMillis())));
				
				// Reset both flags that indicate if we are interrupted by messages in those states
				this.interruptedWhileDoorsOpen = false;
				this.interruptedWhileMoving = false;

				if (this.floorBuffer.isEmpty()) {
					// If we have no more scheduled floors, move to Idle state
					this.currentState = STATES.IDLE;
				} else {
					// If we have a floor to service next, move to Moving state
					this.currentState = STATES.MOVING;
				}
				break;

			default:
				break;
			}
		}

	}

}
