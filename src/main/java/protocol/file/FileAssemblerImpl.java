package protocol.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import protocol.file.packet.Packet;
import protocol.file.packet.header.parts.Flags;

public class FileAssemblerImpl implements FileAssembler {

	/** The file name */
	private String fileName;

	/** The file directory */
	private String fileDirectory;

	/** The download number */
	private int downloadNumber;

	/** The packets */
	private Map<Integer, Packet> packets;

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
	public void addPacket(Packet packet) {
		if (packet.getHeader().getFlags() != Flags.UPLOAD_DATAINTEGRITY) {
			packets.put(packet.getHeader().getSequenceNumber(), packet);
		} else {
			assembleFile(packet);
		}
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
		String dataIntegrity = new String(packet.getData());
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
