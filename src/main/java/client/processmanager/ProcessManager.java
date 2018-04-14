package client.processmanager;

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
	 * Notifies the client that the file is not found.
	 */
	public void fileNotFound();

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
}
