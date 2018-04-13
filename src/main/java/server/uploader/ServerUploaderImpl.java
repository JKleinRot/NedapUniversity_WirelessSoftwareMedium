package server.uploader;

import java.nio.ByteBuffer;
import java.util.Arrays;

import packet.Packet;
import packet.PacketImpl;
import packet.header.Flags;
import packet.header.Header;
import packet.header.HeaderImpl;
import packet.header.Types;
import server.file.ServerFileDisassembler;
import server.file.ServerFileDisassemblerImpl;

public class ServerUploaderImpl implements ServerUploader {

	/** The file disassembler */
	private ServerFileDisassembler fileDisassembler;
	
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
	
	/** The final message number */
	private static final int finalNumber = 20;
	
	/** The download number */
	private int downloadNumber;
	
	/**
	 * -----Constructor-----
	 * 
	 * Creates a server uploader.
	 */
	public ServerUploaderImpl() {

	}

	@Override
	public Packet processPacket(byte[] packet, int length) {
		Packet receivedPacket = recreatePacket(Arrays.copyOfRange(packet, 0, length));
		Packet packetToSend;
		if (receivedPacket.getHeader().getTypes().equals(Types.DOWNLOADCHARACTERISTICS)) {
			createFileDisassembler(receivedPacket);
			packetToSend = fileDisassembler.getNextPacket();
			System.out.println("DownloadCharacteristics");
		} else if (receivedPacket.getHeader().getTypes().equals(Types.ACK)){
			packetToSend = fileDisassembler.getNextPacket();
			System.out.println("Ack");
		} else {
			packetToSend = createDataIntegrityPacket();
		}
		return packetToSend;
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
	
	private void createFileDisassembler(Packet packet) {
		String data = new String(packet.getData());
		String[] words = data.split(" ");
		String fileDirectory = words[1];
		String fileName = words[3];
		downloadNumber = Integer.parseInt(words[5]);
		fileDisassembler = new ServerFileDisassemblerImpl(fileDirectory + fileName, this, downloadNumber);
	}
	
	private Packet createDataIntegrityPacket() {
		Header header = new HeaderImpl(finalNumber, 0, Flags.DOWNLOAD_DATAINTEGRITY, Types.DATAINTEGRITY, downloadNumber);
		byte[] data = ("DataSize " + fileDisassembler.getTotalDataSize()).getBytes();
		Packet packet = new PacketImpl(header, data);
		return packet;
	}
	
	@Override
	public void notifyServerFileNotFound() {
		System.out.println("ERROR: File not found");
	}
}
