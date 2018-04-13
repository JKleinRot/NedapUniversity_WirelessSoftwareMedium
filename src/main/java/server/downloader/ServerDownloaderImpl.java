package server.downloader;

import java.nio.ByteBuffer;
import java.util.Arrays;

import packet.Packet;
import packet.PacketImpl;
import packet.header.Flags;
import packet.header.Header;
import packet.header.HeaderImpl;
import packet.header.Types;
import server.file.ServerFileAssembler;
import server.file.ServerFileAssemblerImpl;

public class ServerDownloaderImpl implements ServerDownloader {

	/** The file assembler */
	private ServerFileAssembler fileAssembler;

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
	 * Creates a server downloader.
	 */
	public ServerDownloaderImpl() {

	}

	@Override
	public Packet processPacket(byte[] packet, int length) {
		Packet receivedPacket = recreatePacket(Arrays.copyOfRange(packet, 0, length));
		if (receivedPacket.getHeader().getTypes().equals(Types.UPLOADCHARACTERISTICS)) {
			createFileAssembler(receivedPacket);
		} else {
			fileAssembler.addPacket(receivedPacket);
		}
		Packet ack = createAck(receivedPacket);
		return ack;
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
		Flags flags = reconstructFlags(
				ByteBuffer.wrap(Arrays.copyOfRange(packet, flagsOffset, typesOffset)).getInt());
		Types types = reconstructTypes(
				ByteBuffer.wrap(Arrays.copyOfRange(packet, typesOffset, downloadNumberOffset)).getInt());
		int downloadNumber = ByteBuffer.wrap(Arrays.copyOfRange(packet, downloadNumberOffset, headerLength))
				.getInt();
		Header header = new HeaderImpl(sequenceNumber, acknowledgementNumber, flags, types, downloadNumber);
		byte[] data = ByteBuffer.wrap(Arrays.copyOfRange(packet, headerLength, packet.length))
				.array();
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
	 * Creates a new file assembler.
	 * 
	 * @param packet
	 *            The packet
	 */
	private void createFileAssembler(Packet packet) {
		String data = new String(packet.getData());
		String[] words = data.split(" ");
		String fileDirectory = words[1];
		String fileName = words[3];
		int downloadNumber = Integer.parseInt(words[5]);
		fileAssembler = new ServerFileAssemblerImpl(fileName, fileDirectory, downloadNumber);
	}

	/**
	 * Creates the ack packet to send back to the server.
	 * 
	 * @param packet
	 *            The packet
	 * @return the ack packet
	 */
	private Packet createAck(Packet packet) {
		Header header = new HeaderImpl(0, packet.getHeader().getSequenceNumber(), Flags.UPLOAD, Types.ACK,
				packet.getHeader().getDownloadNumber());
		Packet ack = new PacketImpl(header, new byte[0]);
		return ack;
	}

}
