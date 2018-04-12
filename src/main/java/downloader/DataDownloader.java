package downloader;

import protocol.file.packet.Packet;

public interface DataDownloader {

	/**
	 * Forwards the received packet from the server to the data downloader and
	 * returns the packet to send.
	 * 
	 * @param packet
	 *            The packet
	 * @returns the packet to send
	 */
	public Packet processPacket(byte[] packet);
}
