package client.uploader;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Observable;

import client.Client;
import client.processmanager.ProcessManager;
import client.statistics.ClientStatistics;
import client.statistics.ClientStatisticsImpl;
import filedisassembler.ClientFileDisassembler;
import filedisassembler.ClientFileDisassemblerImpl;
import packet.Packet;
import packet.PacketImpl;
import packet.header.Flags;
import packet.header.Header;
import packet.header.HeaderImpl;
import packet.header.Types;

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

	/** The previous send packet */
	private Packet previousPacket;
	
	/** The client statistics */
	private ClientStatistics clientStatistics;
	
	/** The string representation of the uploader */
	private String characteristics;

	/** Whether the file is found */
	private boolean isFileFound;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a DataUploaderImpl.
	 * 
	 * @param client
	 *            The client
	 * @param processManager
	 *            The process manager
	 *            @param uploadNumber
	 *            The upload number
	 */
	public ClientUploaderImpl(Client client, ProcessManager processManager, int uploadNumber) {
		this.client = client;
		this.uploadNumber = uploadNumber;
		this.processManager = processManager;
	}

	@Override
	public void upload(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		characteristics = "Upload " + fileName + " from " + fileDirectory + " to " + newDirectory + " as " + newFileName + "\n";
		createFileDisassembler(fileDirectory + fileName);
		if (!isFileFound) {
			return;
		}
		clientStatistics = new ClientStatisticsImpl(fileDirectory + fileName);
		sendUploadCharacteristicsPacket(newDirectory, newFileName);
		sendData();
		sendDataIntegrityPacket();
		notifyProcessManagerUploadComplete(fileName, fileDirectory, newDirectory, newFileName);
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
		client.sendOnePacket(packet, this);
	}

	/**
	 * Sends the packets to the server via the client.
	 * 
	 * @param file
	 *            The file to send
	 */
	private void sendData() {
		clientStatistics.setStartTime(LocalDateTime.now());
		while (previousPacket == null || !previousPacket.getHeader().getFlags().equals(Flags.UPLOAD_LAST)) {
			Packet packet = fileDisassembler.getNextPacket();
			client.sendOnePacket(packet, this);
			previousPacket = packet;
			clientStatistics.updatePartSend(packet.getData().length);
		}
		clientStatistics.setEndTime(LocalDateTime.now());
	}

	/**
	 * Sends the data integrity packet to the server.
	 */
	private void sendDataIntegrityPacket() {
		Header header = new HeaderImpl(finalNumber, 0, Flags.UPLOAD_DATAINTEGRITY, Types.DATAINTEGRITY, uploadNumber);
		byte[] data = ("DataSize " + fileDisassembler.getTotalDataSize()).getBytes();
		Packet packet = new PacketImpl(header, data);
		client.sendOnePacket(packet, this);
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
	private void notifyProcessManagerUploadComplete(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		processManager.uploadComplete(fileName, fileDirectory, newDirectory, newFileName);
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

}
