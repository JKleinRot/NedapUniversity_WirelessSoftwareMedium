package protocol.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import protocol.file.packet.Packet;
import protocol.file.packet.PacketImpl;
import protocol.file.packet.header.Header;
import protocol.file.packet.header.HeaderImpl;
import protocol.file.packet.header.parts.Flags;
import protocol.file.packet.header.parts.Types;

public class FileAssemblerImpl implements FileAssembler {

	/** The file name */
	private String fileName;

	/** The file directory */
	private String fileDirectory;

	/** The download number */
	private int downloadNumber;

	/** The packets */
	private Map<Integer, Packet> packets;

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
	 * Creates a new FileAssemblerImpl for the provided file name and directory.
	 * 
	 * @param fileName
	 *            The file name
	 * @param fileDirectory
	 *            The file directory
	 * @param downloadNumber
	 *            The download number
	 */
	public FileAssemblerImpl(String fileName, String fileDirectory, int downloadNumber) {
		this.fileName = fileName;
		this.fileDirectory = fileDirectory;
		this.downloadNumber = downloadNumber;
		packets = new HashMap<>();

	}

	@Override
	public int getDownloadNumber() {
		return downloadNumber;
	}

	@Override
	public void addPacket(byte[] packet) {
		int sequenceNumber = ByteBuffer.allocate(4)
				.put(Arrays.copyOfRange(packet, sequenceNumberOffset, acknowledgementNumberOffset)).getInt();
		int acknowledgementNumber = ByteBuffer.allocate(4)
				.put(Arrays.copyOfRange(packet, acknowledgementNumberOffset, flagsOffset)).getInt();
		Flags flags = reconstructFlags(
				ByteBuffer.allocate(4).put(Arrays.copyOfRange(packet, flagsOffset, typesOffset)).getInt());
		Types types = reconstructTypes(
				ByteBuffer.allocate(4).put(Arrays.copyOfRange(packet, typesOffset, downloadNumberOffset)).getInt());
		int downloadNumber = ByteBuffer.allocate(4).put(Arrays.copyOfRange(packet, downloadNumberOffset, headerLength))
				.getInt();
		Header header = new HeaderImpl(sequenceNumber, acknowledgementNumber, flags, types, downloadNumber);
		int dataSize = packet.length - headerLength;
		byte[] data = ByteBuffer.allocate(dataSize).put(Arrays.copyOfRange(packet, headerLength, packet.length + 1))
				.array();
		Packet thePacket = new PacketImpl(header, data);
		if (header.getFlags() != Flags.UPLOAD_DATAINTEGRITY) {
			packets.put(sequenceNumber, thePacket);
		} else {
			assembleFile(thePacket);
		}
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
		}
		return types;
	}

	/**
	 * Assembles the data from the packets into the file.
	 */
	private void assembleFile(Packet lastPacket) {
		int dataLength = findDataLength(lastPacket);
		ByteBuffer byteBuffer = ByteBuffer.allocate(dataLength);
		int position = 0;
		for (Integer sequenceNumber : packets.keySet()) {
			Packet packet = packets.get(sequenceNumber);
			byte[] data = packet.getData();
			byteBuffer.position(position);
			byteBuffer.put(data);
			position = position + data.length;
		}
		byte[] dataBytes = byteBuffer.array();
		String data = new String(dataBytes, 0, dataBytes.length);
		writeFile(data);
	}

	/**
	 * Finds the data length of the original file.
	 * 
	 * @param packet
	 *            The packet
	 * @return the data length
	 */
	private int findDataLength(Packet packet) {
		String dataIntegrity = new String(packet.getData(), 0, packet.getLength());
		String[] words = dataIntegrity.split(" ");
		int dataLength = Integer.parseInt(words[1]);
		return dataLength;
	}

	/**
	 * Writes the data to the file with the correct name and in the correct directory.
	 * 
	 * @param data
	 *            The data
	 */
	private void writeFile(String data) {
		try {
			FileWriter fileWriter = new FileWriter(fileDirectory + fileName);
			BufferedWriter bufferedReader = new BufferedWriter(fileWriter);
			bufferedReader.write(data);
			bufferedReader.close();
		} catch (IOException e) {
			//?
		}
	}

}
