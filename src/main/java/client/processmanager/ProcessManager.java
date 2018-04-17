package client.processmanager;

import client.downloader.ClientDownloader;
import client.uploader.ClientUploader;

/**
 * Manages the processes executed in response to inputs by the user.
 * 
 * @author janine.kleinrot
 */
public interface ProcessManager {

	/**
	 * Handles the upload request of the user.
	 * 
	 * @param fileName
	 *            The file name
	 * @param fileDirectory
	 *            The file directory
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	public void handleUploadRequest(String fileName, String fileDirectory, String newDirectory, String newFileName);

	/**
	 * Handles the download request of the user.
	 * 
	 * @param fileName
	 *            The file name
	 * @param fileDirectory
	 *            The file directory
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	public void handleDownloadRequest(String fileName, String fileDirectory, String newDirectory, String newFileName);

	/**
	 * Notifies the client that the file is not found for the uploader.
	 */
	public void fileNotFound(ClientUploader uploader);

	/**
	 * Notifies the client that the file is not found for the downloader.
	 */
	public void fileNotFound(ClientDownloader downloader);

	/**
	 * Notifies the client that the upload is complete.
	 * 
	 * @param fileName
	 *            The file name
	 * @param fileDirectory
	 *            The file directory
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	public void uploadComplete(String fileName, String fileDirectory, String newDirectory, String newFileName);

	/**
	 * Notifies the client that the download is complete.
	 * 
	 * @param fileName
	 *            The file name
	 * @param fileDirectory
	 *            The file directory
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	public void downloadComplete(String fileName, String fileDirectory, String newDirectory, String newFileName);

	/**
	 * Returns the statistics of all downloads and uploads performed.
	 * 
	 * @return the statistics
	 */
	public String getStatistics();

	/**
	 * Notifies the client that the upload is incorrect.
	 * 
	 * @param fileName
	 *            The file name
	 * @param fileDirectory
	 *            The file directory
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	public void uploadIncorrect(String fileName, String fileDirectory, String newDirectory, String newFileName);

	/**
	 * Notifies the client that the download is incorrect.
	 * 
	 * @param fileName
	 *            The file name
	 * @param fileDirectory
	 *            The file directory
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	public void downloadIncorrect(String fileName, String fileDirectory, String newDirectory, String newFileName);
}
