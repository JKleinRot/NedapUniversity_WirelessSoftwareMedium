package statistics;

import java.time.LocalDateTime;

/**
 * Calculates the statistics of the client upload.
 * 
 * @author janine.kleinrot
 */

public interface Statistics {

	/**
	 * Updates the retransmission count.
	 * 
	 * @param retransmissionCount
	 *            The retransmission count for the packet sent
	 */
	public void updateRetransmissionCount(int retransmissionCount);

	/**
	 * Sets the start time of the upload.
	 * 
	 * @param startTime
	 *            The start time
	 */
	public void setStartTime(LocalDateTime startTime);

	/**
	 * Sets the end time of the upload.
	 * 
	 * @param endTime
	 *            The end time
	 */
	public void setEndTime(LocalDateTime endTime);

	/**
	 * Updates the part of the data that is already sent.
	 * 
	 * @param lastPacketSize
	 *            The size of the packet sent
	 */
	public void updatePartSent(int lastPacketSize);

	/**
	 * Returns the statistics in a formatted string ready to print on the console.
	 * 
	 * @return the statistics
	 */
	public String getStatistics();

	/**
	 * Sets the total data size for the client downloader.
	 * 
	 * @param packet
	 *            The received packet
	 */
	public void setTotalDataSize(byte[] packet);
}
