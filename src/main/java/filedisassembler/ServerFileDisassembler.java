package filedisassembler;

import packet.Packet;

/**
 * Disassembles file into packets with headers ready to send.
 * 
 * @author janine.kleinrot
 */
public interface ServerFileDisassembler {

	/**
	 * Returns the next packet to send.
	 * 
	 * @return the next packet
	 */
	public Packet getNextPacket();

	/**
	 * Returns the current packet to retransmit since a double acknowledgement was
	 * received.
	 * 
	 * @return the next packet
	 */
	public Packet getCurrentPacket();

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
	 */
	public void decreasePacketSize(Packet packet);

	/**
	 * Increases the packet size by doubling it if the packet size is below the
	 * default size. Above the default size the packet size is multiplied by 1.5.
	 */
	public void increasePacketSize();

	/**
	 * Returns the next packet to send from the list created after decreasing the
	 * packet size.
	 * 
	 * @return the next packet
	 */
	public Packet getNextPacketDecreasedSize();

	/**
	 * Returns the data size of the file before sending the file by using
	 * File.length.
	 * 
	 * @return the data size in bytes
	 */
	public int getDataSizeBeforeSending();

	/**
	 * Returns the checksum of the data.
	 * 
	 * @return the checksum
	 */
	public byte[] getChecksum();
}
