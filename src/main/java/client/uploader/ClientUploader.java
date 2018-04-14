package client.uploader;

import packet.Packet;

/**
 * Uploads the data onto the file system.
 * 
 * @author janine.kleinrot
 */
public interface ClientUploader {

	/**
	 * Uploads a file.
	 * 
	 * @param fileName
	 *            The file name
	 * @param fileDirectory
	 *            The file directory
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	public void upload(String fileName, String fileDirectory, String newDirectory, String newFileName);

	/**
	 * Notifies the process manager that the file is not found.
	 */
	public void notifyProcessManagerFileNotFound();

	/**
	 * Gives the packet not transmitted after five tries back to the file
	 * disassembler to split the packet into packets of minimum size and sends those
	 * packets one at a time the the server.
	 * 
	 * @param packet
	 *            The packet not transmitted
	 */
	public void decreasePacketSize(Packet packet);

	/**
	 * Increases the packet size by doubling it if five packets in a row were
	 * transmitted successfully.
	 */
	public void increasePacketSize();
}
