package SchedulerSubsystem;

import java.util.ArrayList;
import SharedResources.DirectionEnum;

/**
 * 
 * @author Michael Quach
 *
 */

public class SchedulerElevatorData {
	private int currentFloor;
	private ArrayList<Integer> destinationFloor;
	private DirectionEnum direction;
	
	/**
	 * Constructs default elevator data object, stores state of given elevator
	 */
	public SchedulerElevatorData() {
		this.currentFloor = 1;
		this.destinationFloor = new ArrayList<Integer>();
		this.direction = DirectionEnum.IDLE_DIRECTION;
	}
	
	/**
	 * Constructs elevator data object, stores state of given elevator
	 * 
	 * @param currFloor - current floor of elevator
	 * @param destFloor - list of floor destinations elevator will visit
	 * @param dir - direction of elevator's travel
	 */
	public SchedulerElevatorData(int currFloor, ArrayList<Integer> destFloor, DirectionEnum dir) {
		this.currentFloor = currFloor;
		this.destinationFloor = destFloor;
		this.direction = dir;
	}
	
	/**
	 * Returns string representation of elevator data
	 */
	public String toString() {
		String toString = String.format("	Current Floor: %d. Direction: %s.\n		Destinations:", currentFloor, direction);
		for (Integer floor : destinationFloor) {
			toString += " " + floor;
		}
		return toString;
	}
	
	public int getCurrentFloor() {
		return this.currentFloor;
	}
	
	public ArrayList<Integer> getDestinationFloor() {
		return this.destinationFloor;
	}
	
	public DirectionEnum getDirection() {
		return this.direction;
	}
	
	public void setCurrentFloor(int currFloor) {
		this.currentFloor = currFloor;
	}
	
	public void setDestinationFloor(ArrayList<Integer> destFloor) {
		this.destinationFloor = destFloor;
	}
	
	public void setDirection(DirectionEnum dir) {
		this.direction = dir;
	}
	
	public void setData(int currFloor, ArrayList<Integer> destFloor, DirectionEnum dir) {
		this.currentFloor = currFloor;
		this.destinationFloor = destFloor;
		this.direction = dir;		
	}
}
