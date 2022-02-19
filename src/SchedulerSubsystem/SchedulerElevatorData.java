package SchedulerSubsystem;

import java.util.ArrayList;
import SharedResources.DirectionEnum;

public class SchedulerElevatorData {
	private int currentFloor;
	private ArrayList<Integer> destinationFloor;
	private DirectionEnum direction;
	
	public SchedulerElevatorData() {
		this.currentFloor = 1;
		this.destinationFloor = new ArrayList<Integer>();
		this.direction = DirectionEnum.IDLE_DIRECTION;
	}
	
	public SchedulerElevatorData(int currFloor, ArrayList<Integer> destFloor, DirectionEnum dir) {
		this.currentFloor = currFloor;
		this.destinationFloor = destFloor;
		this.direction = dir;
	}
	
	public String toString() {
		return String.format("	Current Floor: %d. Destination Floor: %d. Direction: %s", currentFloor, destinationFloor, direction);
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
