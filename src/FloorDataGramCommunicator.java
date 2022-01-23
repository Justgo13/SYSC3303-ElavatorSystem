import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * 
 */

/**
 * @author jgao2
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

	public void send(Map<String, String> message) {


		// DatagramPackets store their messages as byte arrays.
		// Convert the Map into bytes according to the platform's
		// default character encoding, storing the result into a new
		// byte array.
		byte[] floorData = map2Bytes(message);

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
			sendPacket = new DatagramPacket(floorData, floorData.length, InetAddress.getLocalHost(), 5000);
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
		System.out.println(message);

		// Send the datagram packet to the scheduler via the send socket.

		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Client: Packet sent.\n");
	}

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

	/**
	 * Convert generic Map to byte array
	 * 
	 * @param mapToConvert The Map to convert to a byte array
	 * @return A byte array representation of the map
	 */
	private byte[] map2Bytes(Map<?, ?> mapToConvert) {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		byte[] convertedBytes = null;

		try {
			ObjectOutputStream out = new ObjectOutputStream(byteOutputStream);
			out.writeObject(mapToConvert);
			out.flush();
			convertedBytes = byteOutputStream.toByteArray();
		} catch (IOException ex) {
			// ignore close exception
			System.out.println(ex);
		}

		return convertedBytes;
	}
}
