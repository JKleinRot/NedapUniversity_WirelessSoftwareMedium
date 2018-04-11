package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import client.processmanager.ProcessManager;
import client.processmanager.ProcessManagerImpl;
import client.tui.ClientTUI;
import client.tui.ClientTUIImpl;
import downloader.DataDownloader;
import downloader.DataDownloaderImpl;
import statistics.StatisticsManager;
import statistics.StatisticsManagerImpl;
import storage.StorageRequester;
import storage.StorageRequesterImpl;
import uploader.DataUploader;
import uploader.DataUploaderImpl;

public class ClientImpl implements Client {

	/** The datagram socket */
	private DatagramSocket socket;

	/** A byte buffer to hold the data to send */
	private byte[] dataToSend;

	/** A byte buffer to hold the received data */
	private byte[] receivedData;

	/** The address of the server */
	private InetAddress address;

	/** The port number of the datagram socket */
	private int portNumber;

	/** The process manager */
	private ProcessManager processManager;

	/** The TUI */
	private ClientTUI clientTUI;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a ClientImpl. Initializes a datagram socket. Creates instances of the
	 * client actors and passes those as arguments in the ClientTUI.
	 */
	public ClientImpl() {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("ERROR: Could not setup datagram socket");
		}
		receivedData = new byte[256];
		dataToSend = new byte[256];
		processManager = new ProcessManagerImpl(this);
		clientTUI = new ClientTUIImpl(processManager);
		Thread clientTUIThread = new Thread(clientTUI);
		clientTUIThread.start();
	}

	@Override
	public DatagramPacket send(DatagramPacket packetToSend) {
		DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
		try {
			socket.setBroadcast(true);
			socket.send(packetToSend);
			System.out.println("Send: " + new String(packetToSend.getData(), 0, packetToSend.getLength()) + " to "
					+ packetToSend.getAddress());
			socket.setBroadcast(false);
			socket.receive(receivedPacket);
			address = receivedPacket.getAddress();
			portNumber = receivedPacket.getPort();
			System.out.println("Received: " + new String(receivedPacket.getData(), 0, receivedPacket.getLength())
					+ " from " + receivedPacket.getAddress());
		} catch (IOException e) {
			System.out.println("ERROR: Connection lost");
		}
		return receivedPacket;
	}

	public static void main(String args[]) {
		System.out.println("Client active");
		Client client = new ClientImpl();
		String message = new String("Hello");
		InetAddress address = null;
		try {
			address = InetAddress.getByName("192.168.1.255");
		} catch (UnknownHostException e) {
			System.out.println("ERROR: Unknown IP address");
		}
		DatagramPacket packetToSend = new DatagramPacket(message.getBytes(), message.length(), address, 9876);
		client.send(packetToSend);
	}
}
