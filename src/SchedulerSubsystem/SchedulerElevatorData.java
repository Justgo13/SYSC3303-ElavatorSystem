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
	private boolean hardFaulted;
	private boolean transientFaulted;
	private boolean idle;
	
	/**
	 * Constructs default elevator data object, stores state of given elevator
	 */
	public SchedulerElevatorData() {
		this.currentFloor = 1;
		this.destinationFloor = new ArrayList<Integer>();
		this.direction = DirectionEnum.IDLE_DIRECTION;
		this.hardFaulted = false;
		this.transientFaulted = false;
		this.idle = true;
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
		this.hardFaulted = false;
		this.transientFaulted = false;
		this.idle = true;
	}
	
	/**
	 * Returns string representation of elevator data
	 */
	public String toString() {
		String toString = String.format("	Current Floor: %d. Direction: %s.\n		Destinations:", currentFloor, direction);
		for (Integer floor : destinationFloor) {
			toString += " " + floor;
		}
		if(hardFaulted) {
			toString += "\n Hardfaulted. \n";
		}
		if(transientFaulted) {
			toString += "\n TransientFaulted. \n";
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
	
	public boolean getHardFaulted() {
		return this.hardFaulted;
	}
	
	public boolean getTransientFaulted() {
		return this.transientFaulted;
	}
	
	public boolean getIdle() {
		return this.idle;
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
	
	public void setHardFaulted(boolean fault) {
		this.hardFaulted = fault;
	}
	
	public void setTransientFaulted(boolean fault) {
		this.transientFaulted = fault;
	}
	
	public void setData(int currFloor, ArrayList<Integer> destFloor, DirectionEnum dir) {
		this.currentFloor = currFloor;
		this.destinationFloor = destFloor;
		this.direction = dir;		
	}
	
	public void setIdle(boolean idle) {
		this.idle = idle;
	}
}
