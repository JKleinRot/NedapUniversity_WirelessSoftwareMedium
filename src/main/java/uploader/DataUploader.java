package uploader;

/**
 * Uploads the data onto the file system.
 * 
 * @author janine.kleinrot
 */
public interface DataUploader {

	/**
	 * Uploads a file.
	 * 
	 * @param fileName
	 *            The file name
	 */
	public void upload(String fileName);
	
	/** 
	 * Notifies the process manager that the file is not found.
	 */
	public void notifyProcessManagerFileNotFound();
}
