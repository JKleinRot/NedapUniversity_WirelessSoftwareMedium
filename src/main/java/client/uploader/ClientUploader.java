package client.uploader;

/**
 * Uploads the data onto the file system.
 * 
 * @author janine.kleinrot
 */
public interface ClientUploader {

	/**
	 * Uploads a file.
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
	public void upload(String fileName, String fileDirectory, String newDirectory, String newFileName);

	/**
	 * Notifies the process manager that the file is not found.
	 */
	public void notifyProcessManagerFileNotFound();
}
