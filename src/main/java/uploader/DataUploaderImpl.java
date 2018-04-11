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
	private static final int requestSequenceNumber = 50;

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
		sendFileDestinationPacket(newDirectory, newFileName);
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
		return file;
	}

	/**
	 * Sends a file destination packet to the server.
	 * 
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	private void sendFileDestinationPacket(String newDirectory, String newFileName) {
		Header header = new HeaderImpl(requestSequenceNumber, 0, Flags.UPLOAD, Types.DIRECTORYANDFILENAME, downloadNumber);
		byte[] data = ("Directory " + newDirectory + " Filename " + newFileName).getBytes();
		Packet packet = new PacketImpl(header, data);
		client.sendOnePacket(packet);
	}

	@Override
	public void notifyProcessManagerFileNotFound() {
		processManager.fileNotFound();
	}
}
