package fileassembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import packet.Packet;
import packet.header.Types;
import server.downloader.ServerDownloader;

public class ServerFileAssemblerImpl implements ServerFileAssembler {

	/** The file output stream */
	private OutputStream outputStream;

	/** The last sequence number */
	private int lastSequenceNumber;

	/** The downloader */
	private ServerDownloader downloader;
	
	/** The checksum */
	private byte[] checksum;
	
	/** Whether the file is correctly transfered */
	private boolean isFileCorrect;
	
	/** The file */
	private File file;
	
	/** The message digest */
	private MessageDigest messageDigest;

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
	public ServerFileAssemblerImpl(String fileName, String fileDirectory, int downloadNumber,
			ServerDownloader downloader) {
		this.downloader = downloader;
		createFileOutputStream(fileDirectory, fileName);
		file = new File(fileDirectory + fileName);
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
		messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			outputStream = new FileOutputStream(fileDirectory + fileName);
			new DigestOutputStream(outputStream, messageDigest);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No such algorithm");	
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: No such directory");
			downloader.notifyFileNotFound();
		}
	}

	@Override
	public void addPacket(Packet packet) {
		if (packet.getHeader().getTypes() != Types.DATAINTEGRITY) {
			if (packet.getHeader().getSequenceNumber() != lastSequenceNumber) {
				if (packet.getData().length != 0) {
					try {
						outputStream.write(packet.getData());
						messageDigest.update(packet.getData());
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
		checksum = messageDigest.digest();
		System.out.println(Arrays.toString(checksum));
		if (!Arrays.equals(packet.getData(), checksum)) {
			isFileCorrect = false;
			file.delete();
		} else {
			isFileCorrect = true;
		}
	}
	
	@Override
	public boolean isFileCorrect() {
		return isFileCorrect;
	}
}
