package SchedulerSubsystem;
/**
 * @author Kevin Quach
 * 
 *         A class for sending and receiving UDP Datagram packets to and from the scheduler.
 *         Behaves as a server.
 *		   Currently comprised of code from the SimpleEchoServer.java example.
 */

import java.io.*;
import java.net.*;
import java.util.Arrays;
import SharedResources.*;

public class SchedulerDataGramCommunicator {

   //private DatagramPacket sendPacket, receivePacket;
   private DatagramSocket schedulerSocket;
   private int sendPort;

   public SchedulerDataGramCommunicator(int schedulerPort)
   {
      try {
         // Construct a datagram socket and bind it to any available 
         // port on the local host machine. This socket will be used to
         // send UDP Datagram packets.

         // Construct a datagram socket and bind it to port 5000 
         // on the local host machine. This socket will be used to
         // receive UDP Datagram packets.
         schedulerSocket = new DatagramSocket(schedulerPort);
         // to test socket timeout (2 seconds)
         //receiveSocket.setSoTimeout(2000);
      } catch (SocketException se) {
         se.printStackTrace();
         System.exit(1);
      } 
   }
   
   public SchedulerDataGramCommunicator(int schedulerPort, int subsystemPort)
   {
      try {
         // Construct a datagram socket and bind it to any available 
         // port on the local host machine. This socket will be used to
         // send UDP Datagram packets.
         sendPort = subsystemPort;

         // Construct a datagram socket and bind it to port 5000 
         // on the local host machine. This socket will be used to
         // receive UDP Datagram packets.
         schedulerSocket = new DatagramSocket(schedulerPort);
         
         // to test socket timeout (2 seconds)
         //receiveSocket.setSoTimeout(2000);
      } catch (SocketException se) {
         se.printStackTrace();
         System.exit(1);
      } 
   }
   /**
    * make sure to do null checks
    * @return
    */
   public FloorDataMessageSerializable receive()
   {
   	  // Construct a DatagramPacket for receiving packets up 
      // to 300 bytes long (the length of the byte array).

      byte data[] = new byte[300];
      DatagramPacket receivePacket = new DatagramPacket(data, data.length);
      System.out.println("Server: Waiting for Packet.\n");

      // Block until a datagram packet is received from receiveSocket.
      try {        
         System.out.println("Waiting..."); // so we know we're waiting
         schedulerSocket.receive(receivePacket);
      } catch (IOException e) {
         System.out.print("IO Exception: likely:");
         System.out.println("Receive Socket Timed Out.\n" + e);
         e.printStackTrace();
         System.exit(1);
      }

      // Process the received datagram.
      System.out.println("Server: Packet received:");
      System.out.println("From host: " + receivePacket.getAddress());
      System.out.println("Host port: " + receivePacket.getPort());
      int len = receivePacket.getLength();
      System.out.println("Length: " + len);
      
      sendPort = receivePacket.getPort();
      //could probably set a field for received address here
      
      //Unpacking message (for now we assume that the message passed is FloorDataMessageSerializable)
      //Clean up this try catch later
      try {
    	  FloorDataMessageSerializable message = SerializeUtils.deserialize(data);
    	  System.out.println(message);
    	  return message;
	  } catch (ClassNotFoundException e1) {
		  e1.printStackTrace();
	  } catch (IOException e1) {
		  e1.printStackTrace();
	  }
	  return null;
   }
   
   public void send(FloorDataMessageSerializable message)
   {
      // Construct a DatagramPacket for sending packets up 
      // to 300 bytes long (the length of the byte array).

      byte data[] = new byte[300];
      try {
		data = SerializeUtils.serialize(message);
	  } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	  }

      // Construct a datagram packet that is to be sent to a specified port 
      // on a specified host.
      // The arguments are:
      //  data - the packet data (a byte array). This is the packet data
      //         that was received from the client.
      //  receivePacket.getLength() - the length of the packet data.
      //    Since we are echoing the received packet, this is the length 
      //    of the received packet's data. 
      //    This value is <= data.length (the length of the byte array).
      //  receivePacket.getAddress() - the Internet address of the 
      //     destination host. Since we want to send a packet back to the 
      //     client, we extract the address of the machine where the
      //     client is running from the datagram that was sent to us by 
      //     the client.
      //  receivePacket.getPort() - the destination port number on the 
      //     destination host where the client is running. The client
      //     sends and receives datagrams through the same socket/port,
      //     so we extract the port that the client used to send us the
      //     datagram, and use that as the destination port for the echoed
      //     packet.
      DatagramPacket sendPacket = null;
      try {
		sendPacket = new DatagramPacket(data, data.length,
			  InetAddress.getLocalHost(), sendPort); //TODO: allow for different subsystems to be on different IP addresses
	  } catch (UnknownHostException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	  }

      System.out.println( "Server: Sending packet:");
      System.out.println("To host: " + sendPacket.getAddress());
      System.out.println("Destination host port: " + sendPacket.getPort());
      int len = sendPacket.getLength();
      System.out.println("Length: " + len);
      //System.out.print("Containing: ");
      //System.out.println(new String(sendPacket.getData(),0,len));
      // or (as we should be sending back the same thing)
      // System.out.println(received); 
        
      // Send the datagram packet to the client via the send socket. 
      try {
         schedulerSocket.send(sendPacket);
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(1);
      }
      System.out.println("Server: packet sent");
   }
   
   /**
	 * Closes the scheduler's socket
	 */
	public void closeSocket() {
		schedulerSocket.close();
	}
}

