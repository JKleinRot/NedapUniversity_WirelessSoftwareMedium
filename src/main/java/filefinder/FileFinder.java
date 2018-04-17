package filefinder;

import java.net.DatagramPacket;

import packet.Packet;

public interface FileFinder {

	/**
	 * Finds the files and directories in the directory
	 * 
	 * @param receivedPacket
	 *            The received packet
	 * @return the packet to send
	 */
	public Packet find(DatagramPacket receivedPacket);

}
