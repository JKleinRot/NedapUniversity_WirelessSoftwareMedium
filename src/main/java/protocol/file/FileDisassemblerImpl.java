package protocol.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import protocol.file.packet.Packet;
import protocol.file.packet.PacketImpl;
import protocol.file.packet.header.Header;
import protocol.file.packet.header.HeaderImpl;
import protocol.file.packet.header.parts.Flags;
import protocol.file.packet.header.parts.Types;
import uploader.DataUploader;

public class FileDisassemblerImpl implements FileDisassembler {

	/** The file name */
	private String fileName;

	/** The data uplader */
	private DataUploader dataUploader;

	/** The packet size */
	private int packetSize;
	
	/** The download number */
	private int downloadNumber;

	/** The header size */
	private static final int headerSize = 20;

	/** The data size */
	private int dataSize;

	/** The default packet size */
	private static final int defaultPacketSize = 1024;

	/**
	 * -----Constructor-----
	 * 
	 * @param filename
	 *            The file name
	 */
	public FileDisassemblerImpl(String fileName, DataUploader dataUploader, int downloadNumber) {
		this.fileName = fileName;
		this.dataUploader = dataUploader;
		this.downloadNumber = downloadNumber;
		this.packetSize = defaultPacketSize;
		setDataSize();
	}

	/**
	 * Sets the data size.
	 */
	private void setDataSize() {
		dataSize = packetSize - headerSize;
	}

	@Override
	public File createFileWithPacketsFromFile() {
		byte[] content = fromFileToByteArray();
		File file = fromByteArrayToFileWithPackets(content);
		return file;
	}

	/**
	 * Notifies the data uploader that the file is not found.
	 */
	private void notifyDataUploaderFileNotFound() {
		dataUploader.notifyProcessManagerFileNotFound();
	}

	/**
	 * Reads the file and converts it to a byte array.
	 */
	private byte[] fromFileToByteArray() {
		byte[] content = null;
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuilder stringBuilder = new StringBuilder();
			String line = bufferedReader.readLine();
			while (line != null) {
				stringBuilder.append(line);
				line = bufferedReader.readLine();
			}
			content = stringBuilder.toString().getBytes();
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			notifyDataUploaderFileNotFound();
		} catch (IOException e) {
			// ?
		}
		return content;
	}

	/**
	 * Converts the byte array to a file consisting of packets ready to send.
	 */
	private File fromByteArrayToFileWithPackets(byte[] content) {
		int numberOfPackets = content.length / dataSize + 1;
		File file = new FileImpl();
		for (int i = 0; i < numberOfPackets; i++) {
			byte[] data;
			Header header;
			if (i == numberOfPackets - 1) {
				data = Arrays.copyOfRange(content, i * dataSize, content.length);
				header = new HeaderImpl((i * 10) + 100, 0, Flags.UPLOAD_LAST, Types.DATA, downloadNumber);
			} else {
				data = Arrays.copyOfRange(content, i * dataSize, (i + 1) * dataSize);
				header = new HeaderImpl((i * 10) + 100, 0, Flags.UPLOAD_MORETOCOME, Types.DATA, downloadNumber);
			}
			Packet packet = new PacketImpl(header, data);
			file.addPacket(packet);
		}
		return file;
	}
}
