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

   DatagramPacket sendPacket, receivePacket;
   DatagramSocket sendSocket, receiveSocket;

   public SchedulerDataGramCommunicator()
   {
      try {
         // Construct a datagram socket and bind it to any available 
         // port on the local host machine. This socket will be used to
         // send UDP Datagram packets.
         sendSocket = new DatagramSocket();

         // Construct a datagram socket and bind it to port 5000 
         // on the local host machine. This socket will be used to
         // receive UDP Datagram packets.
         receiveSocket = new DatagramSocket(5000);
         
         // to test socket timeout (2 seconds)
         //receiveSocket.setSoTimeout(2000);
      } catch (SocketException se) {
         se.printStackTrace();
         System.exit(1);
      } 
   }

   public void receiveAndEcho()
   {
      // Construct a DatagramPacket for receiving packets up 
      // to 100 bytes long (the length of the byte array).

      byte data[] = new byte[100];
      receivePacket = new DatagramPacket(data, data.length);
      System.out.println("Server: Waiting for Packet.\n");

      // Block until a datagram packet is received from receiveSocket.
      try {        
         System.out.println("Waiting..."); // so we know we're waiting
         receiveSocket.receive(receivePacket);
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
      
      //Unpacking message
      if(data[0] == 0) {
    	  System.out.println("Message type: Floor Data");
    	  float msgTime = ByteParser.bytesToFloat(Arrays.copyOfRange(data, 1, 5));
    	  int msgFloor = ByteParser.bytesToInt(Arrays.copyOfRange(data, 5, 9));
    	  int msgDirection = data[9];
    	  int msgDestination = ByteParser.bytesToInt(Arrays.copyOfRange(data, 10, 14));
    	  System.out.println(String.format("	Time: %f\n	Floor: %d\n	Direction: %d\n	Car Button: %d\n", msgTime, msgFloor, msgDirection, msgDestination));
      }
      
      // Slow things down (wait 5 seconds)
      try {
          Thread.sleep(5000);
      } catch (InterruptedException e ) {
          e.printStackTrace();
          System.exit(1);
      }
 
      // Create a new datagram packet containing the string received from the client.

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

      sendPacket = new DatagramPacket(data, receivePacket.getLength(),
                               receivePacket.getAddress(), receivePacket.getPort());

      System.out.println( "Server: Sending packet:");
      System.out.println("To host: " + sendPacket.getAddress());
      System.out.println("Destination host port: " + sendPacket.getPort());
      len = sendPacket.getLength();
      System.out.println("Length: " + len);
      System.out.print("Containing: ");
      System.out.println(new String(sendPacket.getData(),0,len));
      // or (as we should be sending back the same thing)
      // System.out.println(received); 
        
      // Send the datagram packet to the client via the send socket. 
      try {
         sendSocket.send(sendPacket);
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(1);
      }

      System.out.println("Server: packet sent");

      // We're finished, so close the sockets.
      sendSocket.close();
      receiveSocket.close();
   }
}

