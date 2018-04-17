package filefinder;

import java.io.File;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import packet.Packet;
import packet.PacketImpl;
import packet.header.Flags;
import packet.header.Header;
import packet.header.HeaderImpl;
import packet.header.Types;

public class FileFinderImpl implements FileFinder {

	/** The request sequence number */
	private static final int requestSequenceNumber = 10;
	
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
	 * Creates a file finder
	 */
	public FileFinderImpl() {

	}

	@Override
	public Packet find(DatagramPacket receivedDatagramPacket) {
		Packet receivedPacket = recreatePacket(
				Arrays.copyOfRange(receivedDatagramPacket.getData(), 0, receivedDatagramPacket.getLength()));
		String filesAndDirectories = findFilesAndDirectories(receivedPacket.getData());
		Packet packetToSend = createFilesAndDirectoriesPacket(filesAndDirectories);
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
	 * Returns the files and directories in the directory.
	 * 
	 * @param directoryBytes
	 *            The directory
	 * @return the files and directories
	 */
	private String findFilesAndDirectories(byte[] directoryBytes) {
		File directory = new File(new String(directoryBytes));
		StringBuilder builder = new StringBuilder();
		StringBuilder files = new StringBuilder();
		StringBuilder directories = new StringBuilder();
		File[] filesAndDirectories = directory.listFiles();
		for (File file : filesAndDirectories) {
			if (file.isFile()) {
				files.append("File: " + file.getName() + "\n");
			} else if (file.isDirectory()) {
				directories.append("Directory: " + file.getName() + "\n");
			}
		}
		builder.append(files.toString());
		builder.append(directories.toString());
		return builder.toString();
	}
	

	private Packet createFilesAndDirectoriesPacket(String filesAndDirectories) {
		Header header = new HeaderImpl(0, requestSequenceNumber, Flags.FILEREQUEST, Types.UNDEFINED, 0);
		byte[] data = filesAndDirectories.getBytes();
		Packet packet = new PacketImpl(header, data);
		return packet;
	}
}
