package server.uploader;

import packet.Packet;

/**
 * Uploads data from the server to the client.
 * 
 * @author janine.kleinrot
 */
public interface ServerUploader {

	/**
	 * Forwards the received packet from the server to the data uploader and returns
	 * the packet to send.
	 * 
	 * @param packet
	 *            The packet
	 * @param length
	 *            The actual length of the data
	 * @return the packet to send
	 */
	public Packet processPacket(byte[] packet, int length);

	/**
	 * Notifies the server that the file is not found.
	 */
	public void notifyServerFileNotFound();

	/**
	 * Sets the boolean to indicate if the server is sending from the list of
	 * decreased size packets.
	 * 
	 * @param value
	 *            The value to set the boolean to
	 */
	public void setIsSendingPacketsAfterDecreasingSize(boolean value);

}
