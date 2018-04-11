package protocol.file;

import protocol.file.packet.Packet;

/**
 * A file contains packets that contain the data and a header to reconstruct the
 * original file using the data in the packets.
 * 
 * @author janine.kleinrot
 */
public interface File {

	/**
	 * Adds a packet to the file.
	 * 
	 * @param packet
	 *            The packet to add
	 */
	public void addPacket(Packet packet);
}
