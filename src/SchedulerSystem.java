/**
 * @author Kevin Quach
 * 
 *         For iteration 1, the scheduler subsystem is in charge of acting as a pipeline of data between the floor subsystem and the elevator subsystem.
 *         In the future, it should be multi-threaded in order to handle multiple elevators simultaneously.
 *
 */
public class SchedulerSystem {
	private static final SchedulerDataGramCommunicator communicator = new SchedulerDataGramCommunicator();
	
	/** Iteration 1 sequence
	 * 1. floor reads events from file
	 * 2. floor sends inputs to scheduler
	 * 3. elevator makes calls to scheduler? ignore this
	 * 4. scheduler sends data back to elevator //TODO
	 * 5. elevator sends data back to scheduler
	 * 6. scheduler sends data back to floor
	 * 7. done
	 */
	public static void main(String[] args) {
		communicator.receiveAndEcho();
	}

}
