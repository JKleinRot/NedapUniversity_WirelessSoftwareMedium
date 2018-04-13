package server.file;

import packet.Packet;

/**
 * Assembles the received packets into a file with the provided name saved in
 * the provided directory.
 * 
 * @author janine.kleinrot
 */
public interface ServerFileAssembler {

	/**
	 * Returns the download number.
	 * 
	 * @return the download number
	 */
	public int getDownloadNumber();

	/**
	 * Adds packet to file.
	 * 
	 * @param packet
	 *            The packet received
	 */
	public void addPacket(Packet packet);
}
