package client.processmanager;

/**
 * Manages the processes executed in response to inputs by the user.
 * 
 * @author janine.kleinrot
 */
public interface ProcessManager {

	/**
	 * Handles the request of the user.
	 * 
	 * @param action
	 *            The action requested
	 * @param fileName
	 *            The file name
	 * @param newDirectory
	 *            The new directory
	 * @param newFileName
	 *            The new file name
	 */
	public void handleRequest(String action, String fileName, String newDirectory, String newFileName);

	/**
	 * Notifies the client that the file is not found.
	 */
	public void fileNotFound();
}
