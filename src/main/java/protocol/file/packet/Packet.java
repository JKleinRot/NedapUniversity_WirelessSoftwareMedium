package protocol.file.packet;

import protocol.file.packet.header.Header;

/**
 * A packet contains data from a file and a header.
 * 
 * @author janine.kleinrot
 */
public interface Packet {

	/**
	 * Returns the header of the packet.
	 * 
	 * @return the header
	 */
	public Header getHeader();

	/**
	 * Returns the data of the packet.
	 * 
	 * @return the data
	 */
	public byte[] getData();
}