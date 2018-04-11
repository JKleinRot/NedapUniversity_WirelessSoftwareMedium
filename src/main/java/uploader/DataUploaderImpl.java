package uploader;

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

public class DataUploaderImpl extends Observable implements DataUploader {

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
	public DataUploaderImpl(Client client, ProcessManager processManager, int downloadNumber) {
		this.client = client;
		this.downloadNumber = downloadNumber;
	}

	@Override
	public void upload(String fileName, String newDirectory, String newFileName) {
		File file = getFileWithPacketsFromFile(fileName);
		sendUploadCharacteristicsPacket(newDirectory, newFileName);
		sendData(file);
		sendDataIntegrityPacket();
	}

	/**
	 * Returns a file with packets from the file with the provided file name.
	 * 
	 * @param fileName
	 *            The file name
	 * @return the file with packets
	 */
	private File getFileWithPacketsFromFile(String fileName) {
		fileDisassembler = new FileDisassemblerImpl(fileName, this, downloadNumber);
		File file = fileDisassembler.createFileWithPacketsFromFile();
		dataSize = file.getDataSize();
		return file;
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
	 * Sends the packets to the server.
	 * 
	 * @param file
	 *            The file to send
	 */
	private void sendData(File file) {
		for (Packet packet : file.getPackets()) {
			client.sendOnePacket(packet);
		}
	}

	/**
	 * Sends the data integrity packet to the server.
	 */
	private void sendDataIntegrityPacket() {
		Header header = new HeaderImpl(finalNumber, 0, Flags.UPLOAD_DATAINTEGRITY, Types.DATAINTEGRITY, downloadNumber);
		byte[] data = ("DataSize " + dataSize).getBytes();
		Packet packet = new PacketImpl(header, data);
		client.sendOnePacket(packet);
	}

	@Override
	public void notifyProcessManagerFileNotFound() {
		processManager.fileNotFound();
	}
}
