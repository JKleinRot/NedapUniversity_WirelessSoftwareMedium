package client;

import java.net.DatagramPacket;

/**
 * The client application able to communicate with the wireless storage medium.
 * It can upload and download files from and to the server.
 * 
 * @author janine.kleinrot
 */
public interface Client {

	/**
	 * Send packet to the server.
	 * 
	 * @param packetToSend
	 *            The packet to send
	 * @return the packet received
	 */
	public DatagramPacket send(DatagramPacket packetToSend);
}
