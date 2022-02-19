/**
 * 
 */
package ElevatorSubsystem;
import java.io.IOException;
import java.util.ArrayList;

import Messages.Message;
import Messages.ServiceFloorRequestMessage;
import SharedResources.*;

/**
 * @author Harjap Gill
 *
 */
public class TemporaryElevatorTestMain {

	/**
	 * 
	 */
	public TemporaryElevatorTestMain() {
		// TODO Auto-generated constructor stub
	}
	
	public static void sendFloorRequestMessage(int srcFloor, int destFloor, String direction, ByteBufferCommunicator buffer, int elevatorId) {
		
		try {
			Message msg = new ServiceFloorRequestMessage(srcFloor, destFloor, direction, elevatorId);
			byte[] msgBytes = SerializeUtils.serialize(msg);
			buffer.putRequestBuffer(msgBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	public static Message recieveFromResponseBuffer(ByteBufferCommunicator buffer) {
		Message msg = null;
        try {
        	byte[] msgBytes = buffer.getResponseBuffer();
			msg = SerializeUtils.deserialize(msgBytes);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(msg.getMessageType());
        return msg;
	}
	
	public static void main(String[] args) {
		
		/** Test1: 1 Message interrupt during moving state   */
//	    System.out.println("Running Test");   
//        ByteBufferCommunicator bufferCommunicator = new ByteBufferCommunicator();
//        Elevator elevator1 = new Elevator(1, false, bufferCommunicator, 0);
//        
//        ArrayList<Elevator> elevators = new ArrayList<Elevator>();
//        elevators.add(elevator1);
//        
//        
//        Thread elevatorSystem = new Thread(new ElevatorSystem(bufferCommunicator, elevators));
//        elevatorSystem.start();
//        
//        // Test
//        Message msg = new ServiceFloorRequestMessage(2, 4, "up", 1);
//        try {
//            byte[] msgBytes = SerializeUtils.serialize(msg);
//            bufferCommunicator.putRequestBuffer(msgBytes);
//            
//            byte[] confirmationMsg = bufferCommunicator.getResponseBuffer();
//            
//            Message confirmation = SerializeUtils.deserialize(confirmationMsg);
//            System.out.println(confirmation.getMessageType());
//            Thread.sleep(1000);
//            //SECOND REQUEST, SHOULD BE DECLINED
//            msg = new ServiceFloorRequestMessage(3, 5, "up", 1);
//            
//            msgBytes = SerializeUtils.serialize(msg);
//            bufferCommunicator.putRequestBuffer(msgBytes);
//            
//            confirmationMsg = bufferCommunicator.getResponseBuffer();
//
//            confirmation = SerializeUtils.deserialize(confirmationMsg);
//            System.out.println(confirmation.getMessageType());
//            
//            byte[] arrivalMsg = bufferCommunicator.getResponseBuffer();
//            Message arrival = SerializeUtils.deserialize(arrivalMsg);
//            System.out.println(arrival.toString());
//            
//            arrivalMsg = bufferCommunicator.getResponseBuffer();
//            arrival = SerializeUtils.deserialize(arrivalMsg);
//            System.out.println(arrival.toString());
//            
//            
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
    /** Test2: Multiple interrupt during moving state  */
//        ByteBufferCommunicator bufferCommunicator = new ByteBufferCommunicator();
//        Elevator elevator1 = new Elevator(1, false, bufferCommunicator, 0);
//        
//        ArrayList<Elevator> elevators = new ArrayList<Elevator>();
//        elevators.add(elevator1);
//        
//        
//        Thread elevatorSystem = new Thread(new ElevatorSystem(bufferCommunicator, elevators));
//        elevatorSystem.start();
//        
//        Message msg;
//        // Test
//        try {
//            sendFloorRequestMessage(1, 9, "up", bufferCommunicator, 1);
//            
//            // Accept Msg
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            
//            // Arrival Msg for floor 1
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            System.out.println(msg.toString());
//            
//            
//            Thread.sleep(13000);
//            //SECOND REQUEST, SHOULD BE DECLINED
//            
//            sendFloorRequestMessage(5, 4, "down", bufferCommunicator, 1);
//            
//            // Decline msg
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            
//            //THIRD REQUEST
//            sendFloorRequestMessage(7, 8, "up", bufferCommunicator, 1);
//
//            // Accept Msg
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            
//            // Accept Msg for floor 7
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            System.out.println(msg.toString());
//            
//            // Accept Msg for floor 8
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            System.out.println(msg.toString());
//            
//            // Accept Msg for floor 9
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            System.out.println(msg.toString());
//
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
	/** */
		
		
	/**Test3: Multiple interrupts during door open state  */
//        ByteBufferCommunicator bufferCommunicator = new ByteBufferCommunicator();
//        Elevator elevator1 = new Elevator(1, false, bufferCommunicator, 0);
//        
//        ArrayList<Elevator> elevators = new ArrayList<Elevator>();
//        elevators.add(elevator1);
//        
//        
//        Thread elevatorSystem = new Thread(new ElevatorSystem(bufferCommunicator, elevators));
//        elevatorSystem.start();
//        
//        Message msg;
//        // Test
//        try {
//            sendFloorRequestMessage(1, 6, "up", bufferCommunicator, 1);
//            
//            // Accept Msg
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            
//            // Arrival Msg for floor 1
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            System.out.println(msg.toString());
//            
//            Thread.sleep(1000);
//            
//            // Send Request
//            sendFloorRequestMessage(5, 4, "down", bufferCommunicator, 1);
//           
//            // Decline Msg
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            
//            Thread.sleep(500);
//            
//            // Send Request
//            sendFloorRequestMessage(4, 5, "up", bufferCommunicator, 1);
//            
//            // Accept Msg
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            
//            Thread.sleep(500);
//            
//            // Send Request
//            sendFloorRequestMessage(1, 3, "up", bufferCommunicator, 1);
//            
//            // Accept Msg
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            
//            // Arrival Msg for Floor 3
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            System.out.println(msg.toString());
//            // Arrival Msg for Floor 4
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            System.out.println(msg.toString());
//            // Arrival Msg for Floor 5
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            System.out.println(msg.toString());
//            // Arrival Msg for Floor 6
//            msg = recieveFromResponseBuffer(bufferCommunicator);
//            System.out.println(msg.toString());
//
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
		
	/** */	
		
		
	/** Test4: Handling changes in directions */
//      ByteBufferCommunicator bufferCommunicator = new ByteBufferCommunicator();
//      Elevator elevator1 = new Elevator(1, false, bufferCommunicator, 7);
//      
//      ArrayList<Elevator> elevators = new ArrayList<Elevator>();
//      elevators.add(elevator1);
//      
//      
//      Thread elevatorSystem = new Thread(new ElevatorSystem(bufferCommunicator, elevators));
//      elevatorSystem.start();
//      
//      Message msg;
//      // Test
//      try {
//          sendFloorRequestMessage(4, 5, "up", bufferCommunicator, 1);
//          
//          // Accept Msg
//          msg = recieveFromResponseBuffer(bufferCommunicator);
//          Thread.sleep(10);
//          
//          sendFloorRequestMessage(6, 5, "down", bufferCommunicator, 1);
//          
//          // Accept Msg
//          msg = recieveFromResponseBuffer(bufferCommunicator);
//          
//          
//          // Arrival Msg for floor 6
//          msg = recieveFromResponseBuffer(bufferCommunicator);
//          System.out.println(msg.toString());
//          
//          // Arrival Msg for floor 4
//          msg = recieveFromResponseBuffer(bufferCommunicator);
//          System.out.println(msg.toString());
//          
//          // Arrival Msg for floor 5
//          msg = recieveFromResponseBuffer(bufferCommunicator);
//          System.out.println(msg.toString());
//         
//
//      } catch (InterruptedException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//      }
	/** */
	}

}
