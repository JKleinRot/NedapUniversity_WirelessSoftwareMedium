package server.file;

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
	 * Returns the total data size of the file.
	 * 
	 * @return the total data size
	 */
	public int getTotalDataSize();
}
