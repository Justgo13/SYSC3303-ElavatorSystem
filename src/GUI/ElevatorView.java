package GUI;

import ElevatorSubsystem.ElevatorStates;

public interface ElevatorView {
	void setCurrentFloor(int elevatorID, int currFloor);
	void setStatus(int elevatorID, ElevatorStates status);
}
