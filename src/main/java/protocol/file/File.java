package protocol.file;

import java.util.List;

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

	/**
	 * Returns the list of packets in the file.
	 * 
	 * @return the list of packets
	 */
	public List<Packet> getPackets();
}
