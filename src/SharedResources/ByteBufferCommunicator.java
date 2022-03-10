/**
 * 
 */
package SharedResources;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * A shared class for storing and retrieving information from message buffers
 * 
 * @author Jason Gao
 */
public class ByteBufferCommunicator implements Runnable {
	private int sendPort;
	private int receivePort;

	private DatagramSocket receiveSocket;
	private List<byte[]> receivedMessages;

	public ByteBufferCommunicator(int sendPort, int receivePort) {
		try {
			this.sendPort = sendPort;
			this.receivePort = receivePort;
			this.receiveSocket = new DatagramSocket(this.receivePort);
			this.receivedMessages = new ArrayList<>();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Send a UDP message
	 * 
	 * @param msg The message to send
	 */
	public void sendUDPMessage(byte[] msg) {
		try {
			DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), this.sendPort);
			DatagramSocket socket = new DatagramSocket();

			socket.send(sendPacket);

			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return A byte array with the UDP message
	 */
	public synchronized byte[] getUDPMessage() {
		while (this.receivedMessages.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		notifyAll();
		return this.receivedMessages.remove(0);
	}

	/**
	 * 
	 * @return Boolean to indicate if received message list is empty
	 */
	public synchronized boolean isMessageListEmpty() {
		notifyAll();
		return this.receivedMessages.isEmpty();
	}

	/**
	 * Add a UDP message to the received message list
	 * 
	 * @param message
	 */
	private synchronized void addUDPMessage(byte[] message) {
		this.receivedMessages.add(message);
		notifyAll();
	}

	/**
	 * Receive a UDP packet
	 */
	private void receiveUDPMessage() {

		byte[] receiveBytes = new byte[512];
		DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);

		try {
			this.receiveSocket.receive(receivePacket);
		} catch (BindException e) {
			// TODO: handle exception
		} catch (SocketException e) {
			// TODO: handle exception
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.addUDPMessage(receiveBytes);

	}

	public void closeSocket() {
		this.receiveSocket.close();
	}

	@Override
	public void run() {
		while (true) {

			if (Thread.currentThread().isInterrupted()) {
				break;
			}
			this.receiveUDPMessage();

		}

	}
}
