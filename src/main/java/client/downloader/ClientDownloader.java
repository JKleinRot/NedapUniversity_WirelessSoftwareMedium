package client.downloader;

/**
 * Downloads a file from the server to the client.
 * 
 * @author janine.kleinrot
 */
public interface ClientDownloader {

	/**
	 * Downloads the provided file to the provided directory with the provided name.
	 * 
	 * @param fileName
	 *            The file name
	 * @param fileDirectory
	 *            The file directory
	 * @param newDirectory
	 *            The new file directory
	 * @param newFileName
	 *            The new file name
	 */
	public void download(String fileName, String fileDirectory, String newDirectory, String newFileName);
	
	/**
	 * Updates the retransmission count in the statistics.
	 * 
	 * @param retransmissionCount
	 *            The retransmission count of this packet
	 */
	public void updateStatistics(int retransmissionCount);
	
	/**
	 * Returns the current statistics of the download.
	 * 
	 * @return the statistics
	 */
	public String getStatistics();
}
