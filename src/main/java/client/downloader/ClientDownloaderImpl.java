package client.downloader;

import client.Client;
import client.processmanager.ProcessManager;
import file.FileAssembler;
import file.FileAssemblerImpl;
import packet.Packet;
import packet.PacketImpl;
import packet.header.Flags;
import packet.header.Header;
import packet.header.HeaderImpl;
import packet.header.Types;

public class ClientDownloaderImpl implements ClientDownloader {

	/** The client */
	private Client client;

	/** The process manager */
	private ProcessManager processManager;

	/** The download number */
	private int downloadNumber;

	/** The file assembler */
	private FileAssembler fileAssembler;
	
	/** The request sequence number */
	private static final int requestSequenceNumber = 10;

	/** The final message number */
	private static final int finalNumber = 20;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a new client downloader to download a file from the server.
	 * 
	 * @param client
	 *            The client
	 * @param processManager
	 *            The process manager
	 * @param downloadNumber
	 *            The download number
	 */
	public ClientDownloaderImpl(Client client, ProcessManager processManager, int downloadNumber) {
		this.client = client;
		this.processManager = processManager;
		this.downloadNumber = downloadNumber;
	}

	@Override
	public void download(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		createFileAssembler(fileName, fileDirectory);
		sendDownloadCharacteristicsPacket(newDirectory, newFileName);
	}

	/**
	 * Creates a file assembler.
	 * 
	 * @param fileName
	 *            The file name
	 * @param fileDirectory
	 *            The file directory
	 */
	private void createFileAssembler(String fileName, String fileDirectory) {
		fileAssembler = new FileAssemblerImpl(fileName, fileDirectory, downloadNumber);
	}

	/**
	 * Sends the download characteristics packet to the server.
	 * 
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	private void sendDownloadCharacteristicsPacket(String newDirectory, String newFileName) {
		Header header = new HeaderImpl(requestSequenceNumber, 0, Flags.DOWNLOAD, Types.DOWNLOADCHARACTERISTICS, downloadNumber);
		byte[] data = ("Directory " + newDirectory + " FileName " + newFileName + " DownloadNumber " + downloadNumber).getBytes();
		Packet packet = new PacketImpl(header, data);
		client.sendOnePacket(packet);
	}
}
