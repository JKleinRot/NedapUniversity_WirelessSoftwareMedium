package fileassembler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import packet.Packet;
import packet.header.Types;

public class FileAssemblerImpl implements FileAssembler {
	
	/** The file output stream */
	private OutputStream outputStream;
	
	/** The last sequence number */
	private int lastSequenceNumber;

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
		createFileOutputStream(fileDirectory, fileName);
		lastSequenceNumber = 0;
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
	public void addPacket(Packet packet) {
		if (packet.getHeader().getTypes() != Types.DATAINTEGRITY) {
			if (packet.getHeader().getSequenceNumber() != lastSequenceNumber) {
				if (packet.getData().length != 0) {
					try {
						outputStream.write(packet.getData());
					} catch (IOException e) {
						System.out.println("ERROR: File could not be written");
					}
					lastSequenceNumber = packet.getHeader().getSequenceNumber();
				}
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
