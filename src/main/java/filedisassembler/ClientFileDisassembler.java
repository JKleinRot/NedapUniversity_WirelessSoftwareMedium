package filedisassembler;

import java.util.List;

import packet.Packet;

/**
 * Disassembles file into packets with headers ready to send.
 * 
 * @author janine.kleinrot
 */
public interface ClientFileDisassembler {

	/**
	 * Returns the next packet to send.
	 * 
	 * @return the next packet
	 */
	public Packet getNextPacket();

	/**
	 * Returns the total data size of the file.
	 * 
	 * @return the total data size
	 */
	public int getTotalDataSize();

	/**
	 * Decreases the packet that is not transmitted after five tries into packets of
	 * the minimum size.
	 * 
	 * @param packet
	 *            The packet not transmitted
	 * @return the resulting packets
	 */
	public List<Packet> decreasePacketSize(Packet packet);

	/**
	 * Increases the packet size by doubling it if the packet size is below the
	 * default size. Above the default size the packet size is multiplied by 1.5
	 */
	public void increasePacketSize();
}
