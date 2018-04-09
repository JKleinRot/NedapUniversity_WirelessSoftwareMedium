package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientImpl implements Client {

	/** The datagram socket */
	private DatagramSocket socket;

	/** A byte buffer to hold the data to send */
	private byte[] dataToSend;

	/** A byte buffer to hold the received data */
	private byte[] receivedData;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a ClientImpl. Initializes a datagram socket.
	 */
	public ClientImpl() {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("ERROR: Could not setup datagram socket");
		}
		receivedData = new byte[256];
		dataToSend = new byte[256];
	}

	@Override
	public DatagramPacket send(DatagramPacket packetToSend) {
		DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
		try {
			socket.send(packetToSend);
			socket.receive(receivedPacket);
			System.out.println(new String(receivedPacket.getData(), 0, receivedPacket.getLength()));
		} catch (IOException e) {
			System.out.println("ERROR: Connection lost");
		}
		return receivedPacket;
	}

	public static void main(String args[]) {
		Client client = new ClientImpl();
		String message = new String("Hello");
		InetAddress address = null;
		try {
			address = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			System.out.println("ERROR: Unknown IP address");
		}
		DatagramPacket packetToSend = new DatagramPacket(message.getBytes(), message.length(), address, 9876);
		client.send(packetToSend);
	}
}
