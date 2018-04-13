package client;

import java.net.DatagramPacket;

import packet.Packet;

/**
 * The client application able to communicate with the wireless storage medium.
 * It can upload and download files from and to the server.
 * 
 * @author janine.kleinrot
 */
public interface Client {

	/**
	 * Sends first packet to get to know the server.
	 * 
	 * @param packetToSend
	 *            The packet to send
	 * @return the packet received
	 */
	public DatagramPacket connect(DatagramPacket packetToSend);

	/**
	 * Sends one packet and returns the response received
	 * 
	 * @param packetToSend
	 *            The packet to send
	 * @return the packet received
	 */
	public DatagramPacket sendOnePacket(Packet packetToSend);
}
