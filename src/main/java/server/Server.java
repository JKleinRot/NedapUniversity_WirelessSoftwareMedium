package server;

/**
 * The wireless storage medium server. A client is able to upload files to and
 * download files from the server.
 * 
 * @author janine.kleinrot
 */
public interface Server {

	/**
	 * Reads and processes incoming messages and responds with appropriate messages.
	 */
	public void run();
}
