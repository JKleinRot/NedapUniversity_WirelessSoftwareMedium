package filedisassembler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import filedisassembler.ServerFileDisassembler;
import packet.Packet;
import packet.PacketImpl;
import packet.header.Flags;
import packet.header.Header;
import packet.header.HeaderImpl;
import packet.header.Types;
import server.uploader.ServerUploader;

public class ServerFileDisassemblerImpl implements ServerFileDisassembler {

	/** The data uploader */
	private ServerUploader dataUploader;

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

	/** The previous sent packet */
	private Packet previousPacket;

	/** The current packet */
	private Packet currentPacket;

	/** The first sequence number */
	private int firstSequenceNumber;

	/** The total data size */
	private int totalDataSize;

	/** The list of packets after decreasing the packet size */
	private List<Packet> packetsDecreasedSize;

	/** The next packet of decreased size to send */
	private int nextPacketDecreasedSize;

	/** The minimum packet size */
	private static final int minimalPacketSize = 64;

	/** The maximum packet size */
	private static final int maximalPacketSize = 60000;

	/** The data size found by using File.length */
	private int fileLengthKnownBeforeSending;
	
	/** The checksum */
	private byte[] checksum;
	
	/** The message digest */
	private MessageDigest messageDigest;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a file disassembler with a buffered reader for the file.
	 * 
	 * @param filename
	 *            The file name
	 */
	public ServerFileDisassemblerImpl(String fileName, ServerUploader dataUploader, int downloadNumber) {
		this.dataUploader = dataUploader;
		this.downloadNumber = downloadNumber;
		this.packetSize = defaultPacketSize;
		firstSequenceNumber = 100;
		totalDataSize = 0;
		nextPacketDecreasedSize = 0;
		File file = new File(fileName);
		fileLengthKnownBeforeSending = (int) file.length();
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
		messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			inputStream = new FileInputStream(fileName);
			new DigestInputStream(inputStream, messageDigest);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No such algorithm");	
		} catch (FileNotFoundException e) {
			notifyDataUploaderFileNotFound();
		}
	}

	/**
	 * Notifies the data uploader that the file is not found.
	 */
	private void notifyDataUploaderFileNotFound() {
		dataUploader.notifyServerFileNotFound();
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
		messageDigest.update(data);
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
		Header header = null;
		if (previousPacket == null && data.length == dataSize) {
			header = new HeaderImpl(firstSequenceNumber, 0, Flags.DOWNLOAD_MORETOCOME, Types.DATA, downloadNumber);
		} else if (previousPacket == null && data.length != dataSize) {
			header = new HeaderImpl(firstSequenceNumber, 0, Flags.DOWNLOAD_LAST, Types.DATA, downloadNumber);
		} else if (previousPacket != null && data.length == dataSize) {
			header = new HeaderImpl(previousPacket.getHeader().getSequenceNumber() + 1, 0, Flags.DOWNLOAD_MORETOCOME,
					Types.DATA, downloadNumber);
		} else if (previousPacket != null && data.length != dataSize) {
			header = new HeaderImpl(previousPacket.getHeader().getSequenceNumber() + 1, 0, Flags.DOWNLOAD_LAST,
					Types.DATA, downloadNumber);
		} 
		return header;
	}

	@Override
	public Packet getCurrentPacket() {
		return currentPacket;
	}

	@Override
	public int getTotalDataSize() {
		return totalDataSize;
	}

	@Override
	public void decreasePacketSize(Packet packet) {
		nextPacketDecreasedSize = 0;
		packetsDecreasedSize = new ArrayList<>();
		packetSize = minimalPacketSize;
		setDataSize();
		System.out.println("Data size = " + dataSize);
		System.out.println("Data length = " + packet.getData().length);
		byte[] data = packet.getData();
		for (int i = 0; i < data.length / dataSize; i++) {
			byte[] dataPart = Arrays.copyOfRange(data, i * dataSize, (i + 1) * dataSize);
			Header header = getNextHeader(dataPart);
			Packet packetPart = new PacketImpl(header, dataPart);
			packetsDecreasedSize.add(packetPart);
			previousPacket = packetPart;
		}
		System.out.println("Amount of packets = " + packetsDecreasedSize.size());
	}

	@Override
	public void increasePacketSize() {
		if (packetSize < defaultPacketSize) {
			packetSize = packetSize * 2;
		} else if ((packetSize * 1.5) >= maximalPacketSize) {
			packetSize = maximalPacketSize;
		} else {
			packetSize = (int) (packetSize * 1.5);
		}
		setDataSize();
	}

	@Override
	public Packet getNextPacketDecreasedSize() {
		Packet packet = packetsDecreasedSize.get(nextPacketDecreasedSize);
		nextPacketDecreasedSize++;
		System.out.println("next small packet = " + nextPacketDecreasedSize);
		if (nextPacketDecreasedSize == packetsDecreasedSize.size()) {
			dataUploader.setIsSendingPacketsAfterDecreasingSize(false);
			System.out.println("Back to false");
		}
		return packet;
	}

	@Override
	public int getDataSizeBeforeSending() {
		return fileLengthKnownBeforeSending;
	}

	@Override
	public byte[] getChecksum() {
		checksum = messageDigest.digest();
		System.out.println(Arrays.toString(checksum));
		return checksum;
	}

	
}
