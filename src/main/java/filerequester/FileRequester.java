package filerequester;

public interface FileRequester {

	/**
	 * Requests the files and directories in the directory
	 * 
	 * @param directory
	 *            The directory
	 * @return the files and directories
	 */
	public String handleFilesRequest(String directory);

}
