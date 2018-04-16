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
	
	/** The amount of retransmissions needed before the packet size is reset to the minimum packet size */
	private static final int decreasePacketSizeThreshold = 5;
	
	/** The amount of adjacent successful retransmissions needed before the packet size is increased */
	private static final int increasePacketSizeThreshold = 5;
	
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
			System.out.println("Send: " + new String(packetToSend.getData(), 0, packetToSend.getLength()) + " to "
					+ packetToSend.getAddress());
			System.out.println(Arrays.toString(packetToSend.getData()));
			socket.setBroadcast(false);
			socket.setSoTimeout(timeoutDuration);
			socket.receive(receivedPacket);
			address = receivedPacket.getAddress();
			portNumber = receivedPacket.getPort();
			System.out.println("Received: " + new String(receivedPacket.getData(), 0, receivedPacket.getLength())
					+ " from " + receivedPacket.getAddress());
		} catch (SocketTimeoutException e) {
			System.out.println("ERROR: No response within time");
			connect(packetToSend);
		} catch (IOException e) {
			System.out.println("ERROR: Connection lost");
		}
		return receivedPacket;
	}
	
	@Override 
	public DatagramPacket sendOnePacket(Packet thePacketToSend) {
		byte[] receivedData = new byte[enoughSpace];
		DatagramPacket packetToSend = new DatagramPacket(thePacketToSend.getBytes(), thePacketToSend.getLength(), address, portNumber);
		DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
		try {
			socket.send(packetToSend);
//			System.out.println("ClientImpl packet send: " + Arrays.toString(Arrays.copyOfRange(packetToSend.getData(), 0, 20)));
//			System.out.println("PacketSize = " + thePacketToSend.getLength() + " SequenceNumber = " + thePacketToSend.getHeader().getSequenceNumber());
//			System.out.println("Send: " + new String(packetToSend.getData(), 0, packetToSend.getLength()) + " to "
//					+ packetToSend.getAddress());
//			System.out.println(Arrays.toString(packetToSend.getData()));
			socket.setSoTimeout(timeoutDuration);
			socket.receive(receivedPacket);
//			System.out.println("ClientImpl packet received: " + Arrays.toString(Arrays.copyOfRange(receivedPacket.getData(), 0, 20)));
//			System.out.println("Length: " + receivedPacket.getLength());
//			System.out.println("Received: " + new String(receivedPacket.getData(), 0, receivedPacket.getLength())
//					+ " from " + receivedPacket.getAddress());
//			System.out.println(Arrays.toString(receivedPacket.getData()));
		} catch (SocketTimeoutException e) {
			System.out.println("ERROR: No response within time");
			receivedPacket = sendOnePacket(thePacketToSend);
		} catch (IOException e) {
			System.out.println("ERROR: Connection lost");
		}
		return receivedPacket;
	}
	
	@Override 
	public DatagramPacket sendOnePacket(Packet thePacketToSend, ClientDownloader downloader) {
		byte[] receivedData = new byte[enoughSpace];
		DatagramPacket packetToSend = new DatagramPacket(thePacketToSend.getBytes(), thePacketToSend.getLength(), address, portNumber);
		DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
		int retransmissionCount = 0;
		try {
			socket.send(packetToSend);
//			System.out.println("ClientImpl packet send: " + Arrays.toString(Arrays.copyOfRange(packetToSend.getData(), 0, 20)));
//			System.out.println("PacketSize = " + thePacketToSend.getLength() + " SequenceNumber = " + thePacketToSend.getHeader().getSequenceNumber());
//			System.out.println("Send: " + new String(packetToSend.getData(), 0, packetToSend.getLength()) + " to "
//					+ packetToSend.getAddress());
//			System.out.println(Arrays.toString(packetToSend.getData()));
			socket.setSoTimeout(timeoutDuration);
			socket.receive(receivedPacket);
//			System.out.println("ClientImpl packet received: " + Arrays.toString(Arrays.copyOfRange(receivedPacket.getData(), 0, 20)));
//			System.out.println("Length: " + receivedPacket.getLength());
//			System.out.println("Received: " + new String(receivedPacket.getData(), 0, receivedPacket.getLength())
//					+ " from " + receivedPacket.getAddress());
//			System.out.println(Arrays.toString(receivedPacket.getData()));
		} catch (SocketTimeoutException e) {
			System.out.println("ERROR: No response within time");
			retransmissionCount++;
			receivedPacket = sendOnePacket(thePacketToSend);
		} catch (IOException e) {
			System.out.println("ERROR: Connection lost");
		}
		downloader.updateStatistics(retransmissionCount);
		return receivedPacket;
	}
	
	@Override
	public DatagramPacket sendOnePacket(Packet thePacketToSend, ClientUploader uploader ) {
		byte[] receivedData = new byte[enoughSpace];
		DatagramPacket packetToSend = new DatagramPacket(thePacketToSend.getBytes(), thePacketToSend.getLength(), address, portNumber);
		DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
		try {
			socket.send(packetToSend);
//			System.out.println("PacketSize = " + thePacketToSend.getLength() + " SequenceNumber = " + thePacketToSend.getHeader().getSequenceNumber());
//			System.out.println("Send: " + new String(packetToSend.getData(), 0, packetToSend.getLength()) + " to "
//					+ packetToSend.getAddress());
//			System.out.println(Arrays.toString(packetToSend.getData()));
			socket.setSoTimeout(timeoutDuration);
			socket.receive(receivedPacket);
			if (retransmissionCount == 0) {
				successfulTransmissionCount++;
//				System.out.println("Successful transmission count = " + successfulTransmissionCount);
			}
//			System.out.println("Length: " + receivedPacket.getLength());
//			System.out.println("Received: " + new String(receivedPacket.getData(), 0, receivedPacket.getLength())
//					+ " from " + receivedPacket.getAddress());
//			System.out.println(Arrays.toString(receivedPacket.getData()));
		} catch (SocketTimeoutException e) {
			System.out.println("ERROR: No response within time");
			if (retransmissionCount < decreasePacketSizeThreshold) {
				retransmissionCount++;
				successfulTransmissionCount = 0;
//				System.out.println("Retransmission count = " + retransmissionCount);
				receivedPacket = sendOnePacket(thePacketToSend, uploader);
			} else {
//				System.out.println("Decrease packet size");
				uploader.decreasePacketSize(thePacketToSend);
			}
		} catch (IOException e) {
			System.out.println("ERROR: Connection lost");
		}
		uploader.updateStatistics(retransmissionCount);
		retransmissionCount = 0;
		if (successfulTransmissionCount >= increasePacketSizeThreshold) {
			uploader.increasePacketSize();
//			System.out.println("Increase packet size. Successful transmission count = " + successfulTransmissionCount);
			successfulTransmissionCount = 0;
		}
		return receivedPacket;
	}

	public static void main(String args[]) {
		System.out.println("Client active");
		Client client = new ClientImpl();
		String message = new String("Hello, I want to connect to a wireless storage medium");
		InetAddress address = null;
		try {
			address = InetAddress.getByName("192.168.1.255");
		} catch (UnknownHostException e) {
			System.out.println("ERROR: Unknown IP address");
		}
		DatagramPacket packetToSend = new DatagramPacket(message.getBytes(), message.length(), address, 9876);
		client.connect(packetToSend);
	}
}
