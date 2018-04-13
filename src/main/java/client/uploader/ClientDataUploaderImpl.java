package client.uploader;

import java.util.Observable;

import client.Client;
import client.processmanager.ProcessManager;
import protocol.file.File;
import protocol.file.FileDisassembler;
import protocol.file.FileDisassemblerImpl;
import protocol.file.packet.Packet;
import protocol.file.packet.PacketImpl;
import protocol.file.packet.header.Header;
import protocol.file.packet.header.HeaderImpl;
import protocol.file.packet.header.parts.Flags;
import protocol.file.packet.header.parts.Types;

public class ClientDataUploaderImpl extends Observable implements ClientDataUploader {

	/** The client */
	private Client client;

	/** The file disassembler */
	private FileDisassembler fileDisassembler;

	/** The process manager */
	private ProcessManager processManager;

	/** The download number */
	private int downloadNumber;

	/** The request sequence number */
	private static final int requestSequenceNumber = 10;

	/** The final message number */
	private static final int finalNumber = 20;

	/** The data size */
	private int dataSize;

	/** The previous send packet */
	private Packet previousPacket;

	/** The current packet */
	private Packet currentPacket;

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
	public ClientDataUploaderImpl(Client client, ProcessManager processManager, int downloadNumber) {
		this.client = client;
		this.downloadNumber = downloadNumber;
		this.processManager = processManager;
	}

	@Override
	public void upload(String fileName, String newDirectory, String newFileName) {
		createFileDisassembler(fileName);
		sendUploadCharacteristicsPacket(newDirectory, newFileName);
		sendData();
		sendDataIntegrityPacket();
		notifyProcessManagerDownloadComplete(fileName, newDirectory, newFileName);
	}

	/**
	 * Creates a file disassembler
	 * 
	 * @param fileName
	 */
	private void createFileDisassembler(String fileName) {
		fileDisassembler = new FileDisassemblerImpl(fileName, this, downloadNumber);
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
		byte[] data = ("Directory " + newDirectory + " FileName " + newFileName + " DownloadNumber " + downloadNumber
				+ " DataSize " + dataSize).getBytes();
		Packet packet = new PacketImpl(header, data);
		client.sendOnePacket(packet);
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
			client.sendOnePacket(packet);
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
		client.sendOnePacket(packet);
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
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	private void notifyProcessManagerDownloadComplete(String fileName, String newDirectory, String newFileName) {
		processManager.uploadComplete(fileName, newDirectory, newFileName);
	}

}
