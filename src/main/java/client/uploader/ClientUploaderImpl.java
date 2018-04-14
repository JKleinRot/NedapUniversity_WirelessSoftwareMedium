package client.uploader;

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

public class ClientUploaderImpl extends Observable implements ClientUploader {

	/** The client */
	private Client client;

	/** The file disassembler */
	private ClientFileDisassembler fileDisassembler;

	/** The process manager */
	private ProcessManager processManager;

	/** The download number */
	private int downloadNumber;

	/** The request sequence number */
	private static final int requestSequenceNumber = 10;

	/** The final message number */
	private static final int finalNumber = 20;

	/** The previous send packet */
	private Packet previousPacket;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a DataUploaderImpl.
	 * 
	 * @param client
	 *            The client
	 * @param processManager
	 *            The process manager
	 */
	public ClientUploaderImpl(Client client, ProcessManager processManager, int downloadNumber) {
		this.client = client;
		this.downloadNumber = downloadNumber;
		this.processManager = processManager;
	}

	@Override
	public void upload(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		createFileDisassembler(fileDirectory + fileName);
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
		fileDisassembler = new ClientFileDisassemblerImpl(fileName, this, downloadNumber);
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
				downloadNumber);
		byte[] data = ("Directory " + newDirectory + " FileName " + newFileName + " DownloadNumber " + downloadNumber)
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
		while (previousPacket == null || !previousPacket.getHeader().getFlags().equals(Flags.UPLOAD_LAST)) {
			Packet packet = fileDisassembler.getNextPacket();
			client.sendOnePacket(packet, this);
			previousPacket = packet;
		}
	}

	/**
	 * Sends the data integrity packet to the server.
	 */
	private void sendDataIntegrityPacket() {
		Header header = new HeaderImpl(finalNumber, 0, Flags.UPLOAD_DATAINTEGRITY, Types.DATAINTEGRITY, downloadNumber);
		byte[] data = ("DataSize " + fileDisassembler.getTotalDataSize()).getBytes();
		Packet packet = new PacketImpl(header, data);
		client.sendOnePacket(packet, this);
	}

	@Override
	public void notifyProcessManagerFileNotFound() {
		processManager.fileNotFound();
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
		List<Packet> packets = fileDisassembler.splitPacket(packet);
		for (Packet packetToSend : packets) {
			client.sendOnePacket(packetToSend);
		}
	}
	
	@Override
	public void increasePacketSize() {
		fileDisassembler.increasePacketSize();
	}

}
