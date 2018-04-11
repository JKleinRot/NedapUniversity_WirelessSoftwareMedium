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
	 * @param words
	 *            The input given by the user
	 */
	public void handleRequest(String[] words);

	/**
	 * Notifies the client that the file is not found.
	 */
	public void fileNotFound();
}
