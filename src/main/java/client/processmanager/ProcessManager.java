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
	 * @param uploader
	 *            The uploader
	 */
	public void uploadComplete(String fileName, String fileDirectory, String newDirectory, String newFileName,
			ClientUploader uploader);

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
	 * @param downloader
	 *            The downloader
	 */
	public void downloadComplete(String fileName, String fileDirectory, String newDirectory, String newFileName,
			ClientDownloader downloader);

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
	 * @param uploader
	 *            The uploader
	 */
	public void uploadIncorrect(String fileName, String fileDirectory, String newDirectory, String newFileName,
			ClientUploader uploader);

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
	 * @param downloader
	 *            The downloader
	 */
	public void downloadIncorrect(String fileName, String fileDirectory, String newDirectory, String newFileName,
			ClientDownloader downloader);

	/**
	 * Handles the request for the files in a directory on the server
	 * 
	 * @param directory
	 *            The directory
	 * @return the files and directories found
	 */
	public String handleFilesRequest(String directory);

	/**
	 * Pauses the current transfer.
	 */
	public void pause();

	/**
	 * Resumes the paused transfer.
	 */
	public void resume();
}
