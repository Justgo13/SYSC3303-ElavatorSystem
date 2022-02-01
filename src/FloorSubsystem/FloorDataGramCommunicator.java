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

	private DatagramPacket sendFloorDataPacket, receiveFloorPacket;
	private DatagramSocket floorSocket;

	public FloorDataGramCommunicator() {

		try {
			// Construct a datagram socket and bind it to any available
			// port on the local host machine. This socket will be used to
			// send and receive UDP Datagram packets.
			floorSocket = new DatagramSocket();

		} catch (SocketException se) { // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Sends a message contained in a byte array using a datagram packet and
	 * datagram socket to the scheduler.
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
			sendFloorDataPacket = new DatagramPacket(message, message.length, InetAddress.getLocalHost(), 5000);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Client: Sending packet:");
		System.out.println("To host: " + sendFloorDataPacket.getAddress());
		System.out.println("Destination host port: " + sendFloorDataPacket.getPort());
		int len = sendFloorDataPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: ");

		// de-serialize message for printing to console
		this.deserializeMessageAndPrint(message);

		// Send the datagram packet to the scheduler via the send socket.

		try {
			floorSocket.send(sendFloorDataPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Client: Packet sent.\n");
	}

	/**
	 * Receives messages from the scheduler
	 */
	public void receive() {
		// Construct a DatagramPacket for receiving packets up
		// to 300 bytes long (the length of the byte array).

		byte receivedFloorData[] = new byte[300];
		receiveFloorPacket = new DatagramPacket(receivedFloorData, receivedFloorData.length);

		try {
			// Block until a datagram is received via receive socket.
			floorSocket.receive(receiveFloorPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		System.out.println("Client: Packet received:");
		System.out.println("From host: " + receiveFloorPacket.getAddress());
		System.out.println("Host port: " + receiveFloorPacket.getPort());
		int len = receiveFloorPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: ");

		// de-serialize message for printing to console
		this.deserializeMessageAndPrint(receiveFloorPacket.getData());
	}

	/**
	 * De-serialize the message and print the message
	 * 
	 * @param message The serialized message
	 */
	private void deserializeMessageAndPrint(byte[] message) {
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
	}

	/**
	 * Closes the send/receive socket
	 */
	public void closeSendReceiveSocket() {
		floorSocket.close();
	}
}
