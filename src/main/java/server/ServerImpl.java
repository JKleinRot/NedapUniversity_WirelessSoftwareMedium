package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import protocol.file.packet.Packet;
import server.downloader.ServerDataDownloader;
import server.downloader.ServerDataDownloaderImpl;

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
	
	/** The data downloaders */
	private Map<Integer, ServerDataDownloader> dataDownloaders;

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
		receivedData = new byte[2048];
		dataDownloaders = new HashMap<>();
	}

	@Override
	public void run() {
		while (isRunning) {
			receivedData = new byte[2048];
			final DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
			try {
				socket.receive(receivedPacket);
				System.out.println("Received: " + new String(receivedPacket.getData(), 0, receivedPacket.getLength())
						+ " from " + receivedPacket.getAddress());
				System.out.flush();
				System.out.println(Arrays.toString(receivedPacket.getData()));
				System.out.println("DataSize: " + receivedPacket.getLength());
				System.out.println("Flag: " + ByteBuffer.allocate(4).wrap(Arrays.copyOfRange(receivedPacket.getData(), 8, 12)).getInt());
				if (new String (receivedPacket.getData(), 0, 5).equals("Hello")) {
					String message = "You have been connected to a wireless storage medium";
					dataToSend = new byte[message.length()];
					dataToSend = message.getBytes();
					DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length,
							receivedPacket.getAddress(), receivedPacket.getPort());
					socket.send(packetToSend);
					System.out.println("Send: " + new String(packetToSend.getData(), 0, packetToSend.getLength()) + " to "
							+ packetToSend.getAddress());
				} else if (receivedPacket.getData()[11] == 1) {
					if (ByteBuffer.allocate(4).wrap(Arrays.copyOfRange(receivedPacket.getData(), 12, 16)).getInt() == 4) {
						dataDownloaders.put(ByteBuffer.allocate(4).wrap(Arrays.copyOfRange(receivedPacket.getData(), 16, 20)).getInt(), new ServerDataDownloaderImpl(this, receivedPacket.getData()));
					}
					Packet thePacketToSend = dataDownloaders.get(ByteBuffer.allocate(4).wrap(Arrays.copyOfRange(receivedPacket.getData(), 16, 20)).getInt()).processPacket(receivedPacket.getData(), receivedPacket.getLength());
					System.out.println("Upload");
					dataToSend = new byte[thePacketToSend.getLength()];
					dataToSend = thePacketToSend.getBytes();
					DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length,
							receivedPacket.getAddress(), receivedPacket.getPort());
					socket.send(packetToSend);
					System.out.println("Send: " + new String(packetToSend.getData(), 0, packetToSend.getLength()) + " to "
							+ packetToSend.getAddress());
				} else {
					dataToSend = new byte[2048];
					dataToSend = ("Received: " + new String(receivedPacket.getData(), 0, receivedPacket.getLength())
							+ " from " + receivedPacket.getAddress()).getBytes();
					DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length,
							receivedPacket.getAddress(), receivedPacket.getPort());
					socket.send(packetToSend);
					System.out.println("Send: " + new String(packetToSend.getData(), 0, packetToSend.getLength()) + " to "
							+ packetToSend.getAddress());
				}
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
