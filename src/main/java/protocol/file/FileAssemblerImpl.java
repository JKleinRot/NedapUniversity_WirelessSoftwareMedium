package protocol.file;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import protocol.file.packet.Packet;
import protocol.file.packet.header.parts.Flags;

public class FileAssemblerImpl implements FileAssembler {

	/** The file name */
	private String fileName;

	/** The file directory */
	private String fileDirectory;

	/** The download number */
	private int downloadNumber;

	/** The file output stream */
	private OutputStream outputStream;

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
		createFileOutputStream(fileDirectory, fileName);
	}

	/**
	 * Creates a file output stream to write the data to the file.
	 * 
	 * @param fileDirectory
	 *            The file directory
	 * @param fileName
	 *            The file name
	 */
	private void createFileOutputStream(String fileDirectory, String fileName) {
		try {
			outputStream = new FileOutputStream(fileDirectory + fileName);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: No such directory");
		}
	}

	@Override
	public int getDownloadNumber() {
		return downloadNumber;
	}

	@Override
	public void addPacket(Packet packet) {
		if (packet.getHeader().getFlags() != Flags.UPLOAD_DATAINTEGRITY) {
			try {
				outputStream.write(packet.getData());
			} catch (IOException e) {
				System.out.println("ERROR: File could not be written");
			}
		} else {
			checkForDataIntegrity(packet);
		}
	}

	/**
	 * Checks for data integrity of the file.
	 * 
	 * @param packet
	 *            The data integrity packet
	 */
	private void checkForDataIntegrity(Packet packet) {
		
	}
}
