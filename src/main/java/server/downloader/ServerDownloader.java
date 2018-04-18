package server.downloader;

import packet.Packet;

/**
 * Downloads data from the client to the server.
 * 
 * @author janine.kleinrot
 */
public interface ServerDownloader {

	/**
	 * Forwards the received packet from the server to the data downloader and
	 * returns the packet to send.
	 * 
	 * @param packet
	 *            The packet
	 * @param length
	 *            The actual length of the data
	 * @returns the packet to send
	 */
	public Packet processPacket(byte[] packet, int length);

	/**
	 * Notifies the client that the file is not found.
	 */
	public void notifyFileNotFound();
}
