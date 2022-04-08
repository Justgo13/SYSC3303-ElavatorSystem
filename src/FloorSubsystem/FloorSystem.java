package FloorSubsystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import GUI.ElevatorFrame;
import Messages.ElevatorHardFaultMessage;
import Messages.ElevatorTransientFaultMessage;
import Messages.FloorDataMessage;
import Messages.Message;
import Messages.MessageTypes;
import SharedResources.ByteBufferCommunicator;
import SharedResources.SerializeUtils;

/**
 * @author Jason Gao
 * 
 *         The floor subsystem is in charge of sending user requests that it
 *         gets from file input to the scheduler
 *
 */
public class FloorSystem implements Runnable {
	public final static int FLOOR_SEND_PORT = 23;
	public final static int FLOOR_RECEIVE_PORT = 24;
	public final static int FAULT_SEND_PORT = 25;
	public final static int FAULT_RECEIVE_PORT = 26;
	private FloorDataParser parser = new FloorDataParser(); // reference to the floor data parser
	private static List<Message> floorMessageList = new ArrayList<Message>(); // list of floor entries where each entry
																				// is a byte array
	private ByteBufferCommunicator floorBufferCommunicator;
	private ByteBufferCommunicator faultBufferCommunicator;
	private Floor floor;

	/**
	 * Constructs a FloorSystem.
	 * 
	 * @param floorDataFilename       the filename of the data used to simulate an
	 *                                elevator system.
	 * @param floorBufferCommunicator a reference to the Scheduler's communicator
	 */
	public FloorSystem(String floorDataFilename, ByteBufferCommunicator floorBufferCommunicator,
			ByteBufferCommunicator faultBufferCommunicator) {
		this.floorBufferCommunicator = floorBufferCommunicator;
		this.faultBufferCommunicator = faultBufferCommunicator;
		floor = new Floor();
		parser.parseFile(floorDataFilename);
	}

	/**
	 * Method for adding to the floor data entry list
	 * 
	 * @param fdms The floor message as bytes
	 */
	public static void addFloorMessage(Message fdms) {
		floorMessageList.add(fdms);
	}

	/**
	 * Sends each message to the scheduler and receives and prints a message back
	 * for each entry in (floorDataFilename).txt.
	 */
	@Override
	public void run() {
		// Assume that for iteration 1, each message sent by the floor will eventually
		// be received again
		float lastTime = 0;
		for (int i = 0; i < floorMessageList.size(); i++) {
			System.out.println("Sending message from Floor System to Scheduler.");
			// current message time
			Message currentMsg = floorMessageList.get(i);
			float currentTime = 0;
			
			switch (currentMsg.getMessageType()) {
			case FLOOR_DATA_MESSAGE:
				currentMsg = (FloorDataMessage) currentMsg;
				currentTime = ((FloorDataMessage) currentMsg).getTimeStamp();
				break;
			case ELEVATOR_TRANSIENT_FAULT:
				currentMsg = (ElevatorTransientFaultMessage) currentMsg;
				currentTime = ((ElevatorTransientFaultMessage) currentMsg).getTimestamp();
				break;
				
			case ELEVATOR_HARD_FAULT:
				currentMsg = (ElevatorHardFaultMessage) currentMsg;
				currentTime = ((ElevatorHardFaultMessage) currentMsg).getTimestamp();
				break;

			default:
				break;
			}
			
			

			try {
				Thread.sleep((long) (currentTime - lastTime) * 1000);

				if (currentMsg.getMessageType() == MessageTypes.ELEVATOR_TRANSIENT_FAULT
						|| currentMsg.getMessageType() == MessageTypes.ELEVATOR_HARD_FAULT) {
					faultBufferCommunicator.sendUDPMessage(SerializeUtils.serialize(currentMsg));
				} else {
					floorBufferCommunicator.sendUDPMessage(SerializeUtils.serialize(currentMsg));
				}
				lastTime = currentTime;
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			
//			try {
//				System.out.println("Floor System received message from Scheduler: \n" + SerializeUtils.deserialize(floorBufferCommunicator.getResponseBuffer()));
//			} catch (ClassNotFoundException | IOException e) {
//				e.printStackTrace();
//			}
		}
	}

	public static void main(String[] args) {
		int sendPort = FloorSystem.FLOOR_SEND_PORT;
		int receivePort = FloorSystem.FLOOR_RECEIVE_PORT;
		ByteBufferCommunicator floorBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		sendPort = FloorSystem.FAULT_SEND_PORT;
		receivePort = FloorSystem.FAULT_RECEIVE_PORT;
		ByteBufferCommunicator faultBufferCommunicator = new ByteBufferCommunicator(sendPort, receivePort);
		
		
		// get input file
		String inputFile = null;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fileChooser.setDialogTitle("Choose input file");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			    "TXT Files", "txt");
		fileChooser.setFileFilter(filter);
		fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    inputFile = selectedFile.getAbsolutePath();
		} else {
			// must choose a file otherwise program can not progress properly
			System.exit(1);
		}
		
		
		FloorSystem floorSystem = new FloorSystem(inputFile, floorBufferCommunicator, faultBufferCommunicator);
		Thread floorSystemThread = new Thread(floorSystem); // TODO maybe make this thread be spawned by floor system itself
		Thread floorResponseHandler = new Thread(new FloorResponseHandler(floorSystem, floorBufferCommunicator));
		new Thread(floorBufferCommunicator).start();
		new Thread(faultBufferCommunicator).start();

		floorSystemThread.start();
		floorResponseHandler.start();
	}
}