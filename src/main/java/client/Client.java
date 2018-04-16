package client;

import java.net.DatagramPacket;

import client.downloader.ClientDownloader;
import client.uploader.ClientUploader;
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

	/**
	 * Sends one packet and returns the response received. Is able to tell the
	 * downloader the amount of retransmissions needed.
	 * 
	 * @param packetToSend
	 *            The packet to send
	 * @param downloader
	 *            The client downloader
	 * @return the packet received
	 */
	public DatagramPacket sendOnePacket(Packet packetToSend, ClientDownloader downloader);

	/**
	 * Sends one packet and returns the response received. Is able to tell the
	 * uploader to change the size of the packets according to the amount of
	 * retransmissions.
	 * 
	 * @param packetToSend
	 *            The packet to send
	 * @param uploader
	 *            The client uploader
	 * @return the packet received
	 */
	public DatagramPacket sendOnePacket(Packet packetToSend, ClientUploader uploader);
}
