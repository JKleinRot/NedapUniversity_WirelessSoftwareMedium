package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

import client.downloader.ClientDownloader;
import client.processmanager.ProcessManager;
import client.processmanager.ProcessManagerImpl;
import client.tui.ClientTUI;
import client.tui.ClientTUIImpl;
import client.uploader.ClientUploader;
import packet.Packet;

public class ClientImpl implements Client {

	/** The datagram socket */
	private DatagramSocket socket;

	/** The address of the server */
	private InetAddress address;

	/** The port number of the datagram socket */
	private int portNumber;

	/** The process manager */
	private ProcessManager processManager;

	/** The TUI */
	private ClientTUI clientTUI;

	/** The timeout duration in milliseconds */
	private static final int timeoutDuration = 2000;

	/** The retransmission count */
	private int retransmissionCount;

	/** The successful transmission in one try count */
	private int successfulTransmissionCount;

	/** The more than enough byte buffer space number */
	private static final int enoughSpace = 65000;

	/**
	 * The amount of retransmissions needed before the packet size is reset to the
	 * minimum packet size
	 */
	private static final int decreasePacketSizeThreshold = 5;

	/**
	 * The amount of adjacent successful retransmissions needed before the packet
	 * size is increased
	 */
	private static final int increasePacketSizeThreshold = 5;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a client that can send and received UDP packets. It starts a TUI for
	 * inputs of the user.
	 */
	public ClientImpl() {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
		}
		retransmissionCount = 0;
		successfulTransmissionCount = 0;
		processManager = new ProcessManagerImpl(this);
		clientTUI = new ClientTUIImpl(processManager);
		Thread clientTUIThread = new Thread(clientTUI);
		clientTUIThread.start();
	}

	@Override
	public DatagramPacket connect(DatagramPacket packetToSend) {
		byte[] receivedData = new byte[enoughSpace];
		DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
		try {
			socket.setBroadcast(true);
			socket.send(packetToSend);
			socket.setBroadcast(false);
			socket.setSoTimeout(timeoutDuration);
			socket.receive(receivedPacket);
			address = receivedPacket.getAddress();
			portNumber = receivedPacket.getPort();
		} catch (SocketTimeoutException e) {
			connect(packetToSend);
		} catch (IOException e) {
		}
		return receivedPacket;
	}

	@Override
	public DatagramPacket sendOnePacket(Packet thePacketToSend) {
		byte[] receivedData = new byte[enoughSpace];
		DatagramPacket packetToSend = new DatagramPacket(thePacketToSend.getBytes(), thePacketToSend.getLength(),
				address, portNumber);
		DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
		try {
			socket.send(packetToSend);
			socket.setSoTimeout(timeoutDuration);
			socket.receive(receivedPacket);
		} catch (SocketTimeoutException e) {
			receivedPacket = sendOnePacket(thePacketToSend);
		} catch (IOException e) {
		}
		return receivedPacket;
	}

	@Override
	public DatagramPacket sendOnePacket(Packet thePacketToSend, ClientDownloader downloader) {
		byte[] receivedData = new byte[enoughSpace];
		DatagramPacket packetToSend = new DatagramPacket(thePacketToSend.getBytes(), thePacketToSend.getLength(),
				address, portNumber);
		DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
		int retransmissionCount = 0;
		try {
			socket.send(packetToSend);
			socket.setSoTimeout(timeoutDuration);
			socket.receive(receivedPacket);
		} catch (SocketTimeoutException e) {
			retransmissionCount++;
			receivedPacket = sendOnePacket(thePacketToSend);
		} catch (IOException e) {
		}
		downloader.updateStatistics(retransmissionCount);
		return receivedPacket;
	}

	@Override
	public DatagramPacket sendOnePacket(Packet thePacketToSend, ClientUploader uploader) {
		byte[] receivedData = new byte[enoughSpace];
		DatagramPacket packetToSend = new DatagramPacket(thePacketToSend.getBytes(), thePacketToSend.getLength(),
				address, portNumber);
		DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
		try {
			socket.send(packetToSend);
			socket.setSoTimeout(timeoutDuration);
			socket.receive(receivedPacket);
			if (retransmissionCount == 0) {
				successfulTransmissionCount++;
			}
		} catch (SocketTimeoutException e) {
			if (retransmissionCount < decreasePacketSizeThreshold) {
				retransmissionCount++;
				successfulTransmissionCount = 0;
				receivedPacket = sendOnePacket(thePacketToSend, uploader);
			} else {
				uploader.decreasePacketSize(thePacketToSend);
			}
		} catch (IOException e) {
		}
		uploader.updateStatistics(retransmissionCount);
		retransmissionCount = 0;
		if (successfulTransmissionCount >= increasePacketSizeThreshold) {
			uploader.increasePacketSize();
			successfulTransmissionCount = 0;
		}
		return receivedPacket;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            Not used
	 */
	public static void main(String args[]) {
		Client client = new ClientImpl();
		String message = new String("Hello, I want to connect to a wireless storage medium");
		InetAddress address = null;
		try {
			address = InetAddress.getByName("192.168.1.255");
		} catch (UnknownHostException e) {
		}
		DatagramPacket packetToSend = new DatagramPacket(message.getBytes(), message.length(), address, 9876);
		client.connect(packetToSend);
	}
}
