package client.uploader;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import client.Client;
import client.processmanager.ProcessManager;
import filedisassembler.ClientFileDisassembler;
import filedisassembler.ClientFileDisassemblerImpl;
import packet.Packet;
import packet.PacketImpl;
import packet.header.Flags;
import packet.header.Header;
import packet.header.HeaderImpl;
import packet.header.Types;
import statistics.Statistics;
import statistics.StatisticsImpl;

public class ClientUploaderImpl extends Observable implements ClientUploader {

	/** The client */
	private Client client;

	/** The file disassembler */
	private ClientFileDisassembler fileDisassembler;

	/** The process manager */
	private ProcessManager processManager;

	/** The upload number */
	private int uploadNumber;

	/** The request sequence number */
	private static final int requestSequenceNumber = 10;

	/** The final message number */
	private static final int finalNumber = 20;

	/** The previous sent packet */
	private Packet previousPacket;

	/** The client statistics */
	private Statistics clientStatistics;

	/** The string representation of the uploader */
	private String characteristics;

	/** Whether the file is found */
	private boolean isFileFound;

	/** Whether the file is correct */
	private boolean isFileCorrect;

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

	/** Whether the upload is running */
	private volatile boolean isRunning;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a data uploader to upload files to the server.
	 * 
	 * @param client
	 *            The client
	 * @param processManager
	 *            The process manager
	 * @param uploadNumber
	 *            The upload number
	 */
	public ClientUploaderImpl(Client client, ProcessManager processManager, int uploadNumber) {
		this.client = client;
		this.uploadNumber = uploadNumber;
		this.processManager = processManager;
		isFileFound = true;
		isRunning = true;
	}

	@Override
	public void upload(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		characteristics = "Upload " + fileName + " from " + fileDirectory + " to " + newDirectory + " as " + newFileName
				+ "\n";
		createFileDisassembler(fileDirectory + fileName);
		if (!isFileFound) {
			return;
		}
		clientStatistics = new StatisticsImpl(fileDirectory + fileName);
		sendUploadCharacteristicsPacket(newDirectory, newFileName);
		if (!isFileFound) {
			notifyProcessManagerFileNotFound();
			return;
		}
		sendData();
		sendDataIntegrityPacket();
		if (isFileCorrect) {
			notifyProcessManagerUploadComplete(fileName, fileDirectory, newDirectory, newFileName);
		} else {
			notifyProcessManagerUploadIncorrect(fileName, fileDirectory, newDirectory, newFileName);
		}
	}

	/**
	 * Creates a file disassembler
	 * 
	 * @param fileName
	 */
	private void createFileDisassembler(String fileName) {
		fileDisassembler = new ClientFileDisassemblerImpl(fileName, this, uploadNumber);
	}

	/**
	 * Sends the upload characteristics packet to the server.
	 * 
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	private void sendUploadCharacteristicsPacket(String newDirectory, String newFileName) {
		Header header = new HeaderImpl(requestSequenceNumber, 0, Flags.UPLOAD, Types.UPLOADCHARACTERISTICS,
				uploadNumber);
		byte[] data = ("Directory " + newDirectory + " FileName " + newFileName + " DownloadNumber " + uploadNumber)
				.getBytes();
		Packet packet = new PacketImpl(header, data);
		DatagramPacket receivedDatagramPacket = client.sendOnePacket(packet, this);
		Packet receivedPacket = recreatePacket(receivedDatagramPacket.getData());
		if (receivedPacket.getHeader().getTypes() == Types.FILENOTFOUND) {
			isFileFound = false;
		}
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
		} else if (value == 3) {
			types = Types.FILENOTFOUND;
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
	 * Sends the packets to the server.
	 */
	private void sendData() {
		clientStatistics.setStartTime(LocalDateTime.now());
		while (previousPacket == null || !previousPacket.getHeader().getFlags().equals(Flags.UPLOAD_LAST)) {
			if (isRunning) {
				Packet packet = fileDisassembler.getNextPacket();
				client.sendOnePacket(packet, this);
				previousPacket = packet;
				clientStatistics.updatePartSent(packet.getData().length);
			}
		}
		clientStatistics.setEndTime(LocalDateTime.now());
	}

	/**
	 * Sends the data integrity packet to the server.
	 */
	private void sendDataIntegrityPacket() {
		Header header = new HeaderImpl(finalNumber, 0, Flags.UPLOAD_DATAINTEGRITY, Types.DATAINTEGRITY, uploadNumber);
		byte[] data = fileDisassembler.getChecksum();
		Packet packet = new PacketImpl(header, data);
		DatagramPacket receivedDatagramPacket = client.sendOnePacket(packet, this);
		Packet receivedPacket = recreatePacket(
				Arrays.copyOfRange(receivedDatagramPacket.getData(), 0, receivedDatagramPacket.getLength()));
		if (new String(receivedPacket.getData()).equals("Correct")) {
			isFileCorrect = true;
		} else if (new String(receivedPacket.getData()).equals("Incorrect")) {
			isFileCorrect = false;
		} else {
			sendDataIntegrityPacket();
		}
	}

	@Override
	public void notifyProcessManagerFileNotFound() {
		processManager.fileNotFound(this);
		isFileFound = false;
	}

	/**
	 * Notifies the process manager that the current upload is complete.
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
	private void notifyProcessManagerUploadComplete(String fileName, String fileDirectory, String newDirectory,
			String newFileName) {
		processManager.uploadComplete(fileName, fileDirectory, newDirectory, newFileName, this);
	}

	/**
	 * Notifies the process manager that the upload is incorrect.
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
	private void notifyProcessManagerUploadIncorrect(String fileName, String fileDirectory, String newDirectory,
			String newFileName) {
		processManager.uploadIncorrect(fileName, fileDirectory, newDirectory, newFileName, this);
	}

	@Override
	public void decreasePacketSize(Packet packet) {
		List<Packet> packets = fileDisassembler.decreasePacketSize(packet);
		for (Packet packetToSend : packets) {
			client.sendOnePacket(packetToSend);
		}
	}

	@Override
	public void increasePacketSize() {
		fileDisassembler.increasePacketSize();
	}

	@Override
	public void updateStatistics(int retransmissionCount) {
		clientStatistics.updateRetransmissionCount(retransmissionCount);
	}

	@Override
	public String getStatistics() {
		StringBuilder builder = new StringBuilder();
		builder.append(characteristics);
		builder.append(clientStatistics.getStatistics());
		return builder.toString();
	}

	@Override
	public Object getUploadNumber() {
		return uploadNumber;
	}

	@Override
	public void pause() {
		isRunning = false;
	}

	@Override
	public void resume() {
		isRunning = true;
	}

}
