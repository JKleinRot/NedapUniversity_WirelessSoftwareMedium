package protocol.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import client.uploader.ClientDataUploader;
import protocol.file.packet.Packet;
import protocol.file.packet.PacketImpl;
import protocol.file.packet.header.Header;
import protocol.file.packet.header.HeaderImpl;
import protocol.file.packet.header.parts.Flags;
import protocol.file.packet.header.parts.Types;

public class FileDisassemblerImpl implements FileDisassembler {

	/** The file name */
	private String fileName;

	/** The data uplader */
	private ClientDataUploader dataUploader;

	/** The packet size */
	private int packetSize;

	/** The download number */
	private int downloadNumber;

	/** The file input stream */
	private InputStream inputStream;

	/** The header size */
	private static final int headerSize = 20;

	/** The data size */
	private int dataSize;

	/** The default packet size */
	private static final int defaultPacketSize = 1024;

	/** The previous send packet */
	private Packet previousPacket;

	/** The current packet */
	private Packet currentPacket;

	/** The first sequence number */
	private int firstSequenceNumber;
	
	/** The total data size */
	private int totalDataSize;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a file disassembler with a buffered reader for the file.
	 * 
	 * @param filename
	 *            The file name
	 */
	public FileDisassemblerImpl(String fileName, ClientDataUploader dataUploader, int downloadNumber) {
		this.fileName = fileName;
		this.dataUploader = dataUploader;
		this.downloadNumber = downloadNumber;
		this.packetSize = defaultPacketSize;
		firstSequenceNumber = 100;
		totalDataSize = 0;
		setDataSize();
		createFileInputStream(fileName);
	}

	/**
	 * Sets the data size.
	 */
	private void setDataSize() {
		dataSize = packetSize - headerSize;
	}
	
	/**
	 * Creates the buffered reader for the file.
	 * 
	 * @param fileName
	 *            The file name
	 */
	private void createFileInputStream(String fileName) {
		try {
			inputStream = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			notifyDataUploaderFileNotFound();
		}
	}
	
	/**
	 * Notifies the data uploader that the file is not found.
	 */
	private void notifyDataUploaderFileNotFound() {
		dataUploader.notifyProcessManagerFileNotFound();
	}

	@Override
	public Packet getNextPacket() {
		previousPacket = currentPacket;
		byte[] data = getNextData();
		Header header = getNextHeader(data);
		currentPacket = new PacketImpl(header, data);
		totalDataSize = totalDataSize + data.length;
		return currentPacket;
	}

	/**
	 * Reads the next data from the file.
	 * 
	 * @return the data
	 */
	private byte[] getNextData() {
		byte[] dataBuffer = new byte[dataSize];
		int readDataSize = 0;
		try {
			readDataSize = inputStream.read(dataBuffer);
		} catch (IOException e) {
			System.out.println("ERROR: File could not be read");
		}
		byte[] data;
		if (readDataSize != -1) {
			data = Arrays.copyOfRange(dataBuffer, 0, readDataSize);
		} else {
			data = new byte[0];
		}
		return data;
	}

	/**
	 * Creates the header for the next packet.
	 * 
	 * @param data
	 *            The data
	 * @return the header
	 */
	private Header getNextHeader(byte[] data) {
		Header header;
		if (previousPacket == null && data.length == dataSize) {
			header = new HeaderImpl(firstSequenceNumber, 0, Flags.UPLOAD_MORETOCOME, Types.DATA, downloadNumber);
		} else if (previousPacket == null && data.length != dataSize) {
			header = new HeaderImpl(firstSequenceNumber, 0, Flags.UPLOAD_LAST, Types.DATA, downloadNumber);
		} else if (previousPacket != null && data.length == dataSize) {
			header = new HeaderImpl(previousPacket.getHeader().getSequenceNumber() + 1, 0, Flags.UPLOAD_MORETOCOME, Types.DATA, downloadNumber);
		} else if (previousPacket != null && data.length != dataSize) {
			header = new HeaderImpl(previousPacket.getHeader().getSequenceNumber() + 1, 0, Flags.UPLOAD_LAST, Types.DATA, downloadNumber);
		} else {
			header = new HeaderImpl(0, 0, Flags.UNDEFINED, Types.UNDEFINED, 0);
		}
		return header;
	}
	
	@Override
	public int getTotalDataSize() {
		return totalDataSize;
	}
}
