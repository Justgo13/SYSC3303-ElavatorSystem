package FloorSubsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import SharedResources.FloorDataMessageSerializable;
import SharedResources.SerializeUtils;

/**
 * @author Jason Gao
 * 
 *         A class for sending and receiving UDP Datagram packets from the
 *         scheduler
 *
 */
public class FloorDataGramCommunicator {

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendSocket, receiveSocket;

	public FloorDataGramCommunicator() {

		try {
			// Construct a datagram socket and bind it to any available
			// port on the local host machine. This socket will be used to
			// send UDP Datagram packets.
			sendSocket = new DatagramSocket();

			// Construct a datagram socket and bind it to any available
			// port on the local host machine. This socket will be used to
			// receive UDP Datagram packets.
			receiveSocket = new DatagramSocket();
		} catch (SocketException se) { // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Sends a message contained in a byte array using a datagram packet and
	 * datagram socket to the scheduler
	 * 
	 * @param message
	 */
	public void send(byte[] message) {

		// DatagramPackets store their messages as byte arrays.

		// Construct a datagram packet that is to be sent to a specified port
		// on a specified host.
		// The arguments are:
		// msg - the message contained in the packet (the byte array)
		// msg.length - the length of the byte array
		// InetAddress.getLocalHost() - the Internet address of the
		// destination host.
		// In this example, we want the destination to be the same as
		// the source (i.e., we want to run the client and server on the
		// same computer). InetAddress.getLocalHost() returns the Internet
		// address of the local host.
		// 5000 - the destination port number on the destination host.
		try {
			sendPacket = new DatagramPacket(message, message.length, InetAddress.getLocalHost(), 5000);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Client: Sending packet:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		int len = sendPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: ");
		
		// de-serialize message for printing to console
		try {
			FloorDataMessageSerializable messageDeserialize = SerializeUtils.deserialize(message);
			System.out.println(messageDeserialize);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
  	  

		// Send the datagram packet to the scheduler via the send socket.

		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Client: Packet sent.\n");
	}

	/**
	 * Receives a datagram packet from the scheduler as a byte array
	 */
	public void receive() {
		// Construct a DatagramPacket for receiving packets up
		// to 100 bytes long (the length of the byte array).

		byte data[] = new byte[300];
		receivePacket = new DatagramPacket(data, data.length);

		try {
			// Block until a datagram is received via recieve socket.
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		System.out.println("Client: Packet received:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		int len = receivePacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: ");

		// Form a String from the byte array.
		String received = new String(data, 0, len);
		System.out.println(received);
	}

	public void closeSendSocket() {
		sendSocket.close();
	}

	public void closeReceiveSocket() {
		receiveSocket.close();
	}
}
