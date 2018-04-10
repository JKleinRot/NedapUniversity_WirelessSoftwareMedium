package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerImpl implements Server {

	/** The port number of the datagram socket */
	private int portNumber;

	/** The datagram socket */
	private DatagramSocket socket;

	/** Whether the server is running or not */
	private boolean isRunning;

	/** A byte buffer to hold the received data */
	private byte[] receivedData;

	/** A byte buffer to hold the data to send */
	private byte[] dataToSend;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a ServerImpl. Initializes a datagram socket.
	 * 
	 * @param portNumber
	 *            The port number of the UDP connection
	 */
	public ServerImpl(int portNumber) {
		this.portNumber = 9876;
		try {
			socket = new DatagramSocket(portNumber);
		} catch (SocketException e) {
			System.out.println("ERROR: Could not setup datagram socket on port " + portNumber);
		}
		isRunning = true;
		receivedData = new byte[256];
		dataToSend = new byte[256];
	}

	@Override
	public void run() {
		while (isRunning) {
			DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
			try {
				socket.receive(receivedPacket);
				System.out.println("Received: " + new String(receivedPacket.getData(), 0, receivedPacket.getLength()) + " from " + receivedPacket.getAddress());
				String message = "Received: " + new String(receivedPacket.getData(), 0, receivedPacket.getLength());
				dataToSend = message.getBytes();
				DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length,
						receivedPacket.getAddress(), receivedPacket.getPort());
				socket.send(packetToSend);
				System.out.println("Send: " + new String(packetToSend.getData(), 0, packetToSend.getLength()) + " to " + packetToSend.getAddress());
			} catch (IOException e) {
				System.out.println("ERROR: Connection lost");
			}
		}
	}

	public static void main(String args[]) {
		System.out.println("Server active");
		int portNumber = 9876;
		Server server = new ServerImpl(portNumber);
		server.run();
	}
}
