package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import packet.Packet;
import server.downloader.ServerDownloader;
import server.downloader.ServerDownloaderImpl;
import server.uploader.ServerUploader;
import server.uploader.ServerUploaderImpl;

public class ServerImpl implements Server {

	/** The datagram socket */
	private DatagramSocket socket;

	/** Whether the server is running or not */
	private boolean isRunning;

	/** A byte buffer to hold the data to send */
	private byte[] dataToSend;

	/** The data downloaders */
	private Map<Integer, ServerDownloader> dataDownloaders;

	/** The data uploaders */
	private Map<Integer, ServerUploader> dataUploaders;

	/** The more than enough byte buffer space number */
	private static final int enoughSpace = 65000;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a ServerImpl. Initializes a datagram socket.
	 * 
	 * @param portNumber
	 *            The port number of the UDP connection
	 */
	public ServerImpl(int portNumber) {
		try {
			socket = new DatagramSocket(portNumber);
		} catch (SocketException e) {
			System.out.println("ERROR: Could not setup datagram socket on port " + portNumber);
		}
		isRunning = true;
		dataDownloaders = new HashMap<>();
		dataUploaders = new HashMap<>();
	}

	@Override
	public void run() {
		while (isRunning) {
			final byte[] receivedData = new byte[enoughSpace];
			final DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
			try {
				socket.receive(receivedPacket);
				System.out.println("ServerImpl packet received: " + Arrays.toString(Arrays.copyOfRange(receivedPacket.getData(), 0, 20)));
				if (new String(receivedPacket.getData(), 0, 5).equals("Hello")) {
					handleConnectionMessage(receivedPacket);
				} else if (receivedPacket.getData()[11] == 1) {
					handleUploadMessage(receivedPacket);
				} else if (receivedPacket.getData()[11] == 2) {
					handleDownloadMessage(receivedPacket);
				} else {
					dataToSend = new byte[enoughSpace];
					dataToSend = ("Received: " + new String(receivedPacket.getData(), 0, receivedPacket.getLength())
							+ " from " + receivedPacket.getAddress()).getBytes();
					DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length,
							receivedPacket.getAddress(), receivedPacket.getPort());
					socket.send(packetToSend);
					System.out.println("Send: " + new String(packetToSend.getData(), 0, packetToSend.getLength())
							+ " to " + packetToSend.getAddress());
				}
			} catch (IOException e) {
				System.out.println("ERROR: Connection lost");
			}
		}
	}

	/**
	 * Handles the connection message received from the client.
	 * 
	 * @param receivedPacket
	 *            The packet received
	 * @throws IOException
	 *             If the connection is lost
	 */
	private void handleConnectionMessage(DatagramPacket receivedPacket) throws IOException {
		String message = "You have been connected to a wireless storage medium";
		dataToSend = new byte[message.length()];
		dataToSend = message.getBytes();
		DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length, receivedPacket.getAddress(),
				receivedPacket.getPort());
		socket.send(packetToSend);
	}

	/**
	 * Handles the upload messages received from the client.
	 * 
	 * @param receivedPacket
	 *            The packet received
	 * @throws IOException
	 *             If the connection is lost
	 */
	private void handleUploadMessage(DatagramPacket receivedPacket) throws IOException {
		if (ByteBuffer.wrap(Arrays.copyOfRange(receivedPacket.getData(), 12, 16)).getInt() == 4) {
			dataDownloaders.put(ByteBuffer.wrap(Arrays.copyOfRange(receivedPacket.getData(), 16, 20)).getInt(),
					new ServerDownloaderImpl());
		}
		Packet thePacketToSend = dataDownloaders
				.get(ByteBuffer.wrap(Arrays.copyOfRange(receivedPacket.getData(), 16, 20)).getInt())
				.processPacket(receivedPacket.getData(), receivedPacket.getLength());
//		System.out.println("Upload");
		dataToSend = new byte[thePacketToSend.getLength()];
		dataToSend = thePacketToSend.getBytes();
		DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length, receivedPacket.getAddress(),
				receivedPacket.getPort());
		socket.send(packetToSend);
	}

	/**
	 * Handles the download messages received from the client.
	 * 
	 * @param receivedPacket
	 *            The packet received
	 * @throws IOException
	 *             If the connection is lost
	 */
	private void handleDownloadMessage(DatagramPacket receivedPacket) throws IOException {
		if (ByteBuffer.wrap(Arrays.copyOfRange(receivedPacket.getData(), 12, 16)).getInt() == 64) {
			dataUploaders.put(ByteBuffer.wrap(Arrays.copyOfRange(receivedPacket.getData(), 16, 20)).getInt(),
					new ServerUploaderImpl());
		}
		Packet thePacketToSend = dataUploaders
				.get(ByteBuffer.wrap(Arrays.copyOfRange(receivedPacket.getData(), 16, 20)).getInt())
				.processPacket(receivedPacket.getData(), receivedPacket.getLength());
		byte[] dataToSend = new byte[thePacketToSend.getLength()];
		dataToSend = thePacketToSend.getBytes();
		DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length, receivedPacket.getAddress(),
				receivedPacket.getPort());
//		System.out.println("ServerImpl packet send: " + Arrays.toString(Arrays.copyOfRange(packetToSend.getData(), 0, 20)));
		socket.send(packetToSend);
	}

	public static void main(String args[]) {
		System.out.println("Server active");
		int portNumber = 9876;
		Server server = new ServerImpl(portNumber);
		server.run();
	}
}
