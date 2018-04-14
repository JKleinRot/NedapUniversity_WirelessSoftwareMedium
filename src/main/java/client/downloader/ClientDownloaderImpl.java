package client.downloader;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import client.Client;
import client.processmanager.ProcessManager;
import fileassembler.FileAssembler;
import fileassembler.FileAssemblerImpl;
import packet.Packet;
import packet.PacketImpl;
import packet.header.Flags;
import packet.header.Header;
import packet.header.HeaderImpl;
import packet.header.Types;

public class ClientDownloaderImpl implements ClientDownloader {

	/** The client */
	private Client client;
	
	/** The process manager */
	private ProcessManager processManager;

	/** The download number */
	private int downloadNumber;

	/** The file assembler */
	private FileAssembler fileAssembler;

	/** The request sequence number */
	private static final int requestSequenceNumber = 10;

	/** The length of the header */
	private static final int headerLength = 20;

	/** The sequence number offset in the header */
	private static final int sequenceNumberOffset = 0;

	/** The acknowledgement number offset in the header */
	private static final int acknowledgementNumberOffset = 4;

	/** The flags offset in the header */
	private static final int flagsOffset = 8;

	/** The types offset in the header */
	private static final int typesOffset = 12;

	/** The download number offset in the header */
	private static final int downloadNumberOffset = 16;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a new client downloader to download a file from the server.
	 * 
	 * @param client
	 *            The client
	 * @param processManager
	 *            The process manager
	 * @param downloadNumber
	 *            The download number
	 */
	public ClientDownloaderImpl(Client client, ProcessManager processManager, int downloadNumber) {
		this.client = client;
		this.processManager = processManager;
		this.downloadNumber = downloadNumber;
	}

	@Override
	public void download(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		createFileAssembler(newFileName, newDirectory);
		Packet packet = sendDownloadCharacteristicsPacket(fileDirectory, fileName);
		System.out.println("ClientDownloader" + Arrays.toString(packet.getBytes()));
		while (!packet.getHeader().getTypes().equals(Types.DATAINTEGRITY)) {
			System.out.println("Send another packet");
			Packet ack = createAck(packet);
			packet = sendAck(ack);
		}
		notifyProcessManagerDownloadComplete(fileName, fileDirectory, newDirectory, newFileName);
		
	}

	/**
	 * Creates a file assembler.
	 * 
	 * @param fileName
	 *            The file name
	 * @param fileDirectory
	 *            The file directory
	 */
	private void createFileAssembler(String fileName, String fileDirectory) {
		fileAssembler = new FileAssemblerImpl(fileName, fileDirectory, downloadNumber);
	}

	/**
	 * Sends the download characteristics packet to the server and returns the
	 * received packet.
	 * 
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 * @return the received packet
	 */
	private Packet sendDownloadCharacteristicsPacket(String newDirectory, String newFileName) {
		Header header = new HeaderImpl(requestSequenceNumber, 0, Flags.DOWNLOAD, Types.DOWNLOADCHARACTERISTICS,
				downloadNumber);
		byte[] data = ("Directory " + newDirectory + " FileName " + newFileName + " DownloadNumber " + downloadNumber)
				.getBytes();
		Packet packet = new PacketImpl(header, data);
		DatagramPacket receivedDatagramPacket = client.sendOnePacket(packet);
		Packet receivedPacket = recreatePacket(
				Arrays.copyOfRange(receivedDatagramPacket.getData(), 0, receivedDatagramPacket.getLength()));
		fileAssembler.addPacket(receivedPacket);
		return receivedPacket;
	}

	/**
	 * Recreates the packet with header and data from the byte array.
	 * 
	 * @param packet
	 *            The received packet
	 * @return the recreated packet
	 */
	private Packet recreatePacket(byte[] packet) {
		int sequenceNumber = ByteBuffer
				.wrap(Arrays.copyOfRange(packet, sequenceNumberOffset, acknowledgementNumberOffset)).getInt();
		int acknowledgementNumber = ByteBuffer
				.wrap(Arrays.copyOfRange(packet, acknowledgementNumberOffset, flagsOffset)).getInt();
		Flags flags = reconstructFlags(ByteBuffer.wrap(Arrays.copyOfRange(packet, flagsOffset, typesOffset)).getInt());
		Types types = reconstructTypes(
				ByteBuffer.wrap(Arrays.copyOfRange(packet, typesOffset, downloadNumberOffset)).getInt());
		int downloadNumber = ByteBuffer.wrap(Arrays.copyOfRange(packet, downloadNumberOffset, headerLength)).getInt();
		Header header = new HeaderImpl(sequenceNumber, acknowledgementNumber, flags, types, downloadNumber);
		byte[] data = ByteBuffer.wrap(Arrays.copyOfRange(packet, headerLength, packet.length)).array();
		Packet thePacket = new PacketImpl(header, data);
		return thePacket;
	}

	/**
	 * Reconstructs the flags from the bytes
	 * 
	 * @param value
	 *            the integer value of the bytes
	 * @return the flags
	 */
	private Flags reconstructFlags(int value) {
		Flags flags = null;
		if (value == 1) {
			flags = Flags.UPLOAD;
		} else if (value == 2) {
			flags = Flags.DOWNLOAD;
		} else if (value == 4) {
			flags = Flags.STATISTICS;
		} else if (value == 8) {
			flags = Flags.FILEREQUEST;
		} else if (value == 257) {
			flags = Flags.UPLOAD_MORETOCOME;
		} else if (value == 513) {
			flags = Flags.UPLOAD_LAST;
		} else if (value == 1025) {
			flags = Flags.UPLOAD_DATAINTEGRITY;
		} else if (value == 258) {
			flags = Flags.DOWNLOAD_MORETOCOME;
		} else if (value == 514) {
			flags = Flags.DOWNLOAD_LAST;
		} else if (value == 1026) {
			flags = Flags.DOWNLOAD_DATAINTEGRITY;
		}
		return flags;
	}

	/**
	 * Reconstructs the types from the bytes
	 * 
	 * @param value
	 *            The integer value of the bytes
	 * @return the types
	 */
	private Types reconstructTypes(int value) {
		Types types = null;
		if (value == 1) {
			types = Types.DATA;
		} else if (value == 2) {
			types = Types.FILENAME;
		} else if (value == 4) {
			types = Types.UPLOADCHARACTERISTICS;
		} else if (value == 8) {
			types = Types.STATISTICS;
		} else if (value == 16) {
			types = Types.DATAINTEGRITY;
		} else if (value == 32) {
			types = Types.ACK;
		} else if (value == 64) {
			types = Types.DOWNLOADCHARACTERISTICS;
		} else if (value == 128) {
			types = Types.LASTACK;
		}
		return types;
	}

	/**
	 * Creates the ack to send to the server.
	 * 
	 * @param packet
	 *            The packet received
	 * @return the ack
	 */
	private Packet createAck(Packet packet) {
		Packet ack;
		if (!packet.getHeader().getFlags().equals(Flags.DOWNLOAD_LAST)) {
			Header header = new HeaderImpl(0, packet.getHeader().getSequenceNumber(), Flags.DOWNLOAD, Types.ACK,
					downloadNumber);
			byte[] data = new byte[0];
			ack = new PacketImpl(header, data);
		} else {
			Header header = new HeaderImpl(0, packet.getHeader().getSequenceNumber(), Flags.DOWNLOAD_DATAINTEGRITY,
					Types.LASTACK, downloadNumber);
			byte[] data = new byte[0];
			ack = new PacketImpl(header, data);
		}
		return ack;
	}

	/**
	 * Sends the ack to the received packet and returns the received packet.
	 * 
	 * @param ack
	 *            The ack
	 * @return the received packet
	 */
	private Packet sendAck(Packet ack) {
		DatagramPacket receivedPacketDatagram = client.sendOnePacket(ack);
		Packet receivedPacket = recreatePacket(
				Arrays.copyOfRange(receivedPacketDatagram.getData(), 0, receivedPacketDatagram.getLength()));
		fileAssembler.addPacket(receivedPacket);
		return receivedPacket;
	}
	
	/**
	 * Notifies the process manager that the current download is complete.
	 * 
	 * @param fileName
	 *            The file name
	 * @param fileDirectory
	 *            The file directory
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	private void notifyProcessManagerDownloadComplete(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		processManager.downloadComplete(fileName, fileDirectory, newDirectory, newFileName);
	}
}
