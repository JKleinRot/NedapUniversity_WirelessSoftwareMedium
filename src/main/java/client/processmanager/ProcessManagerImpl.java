package client.processmanager;

import java.util.Observable;

import client.Client;
import client.uploader.ClientDataUploader;
import client.uploader.ClientDataUploaderImpl;
import server.downloader.ServerDataDownloader;
import statistics.StatisticsManager;
import storage.StorageRequester;

public class ProcessManagerImpl extends Observable implements ProcessManager {

	/** The client */
	private Client client;

	/** The download number */
	private int downloadNumber;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a ProcessManagerImpl that handles the request from the user.
	 * 
	 * @param dataDownloader
	 *            The data downloader
	 * @param dataUploader
	 *            The data uploader
	 * @param statisticsManager
	 *            The statistics manager
	 * @param storageRequester
	 *            The storage requester
	 */
	public ProcessManagerImpl(Client client) {
		this.client = client;
		downloadNumber = 1;
	}

	@Override
	public void handleUploadRequest(String fileName, String newDirectory, String newFileName) {
		ClientDataUploader dataUploader = new ClientDataUploaderImpl(client, this, downloadNumber);
		downloadNumber++;
		dataUploader.upload(fileName, newDirectory, newFileName);
	}
	
	@Override
	public void handleDownloadRequest(String fileName, String newDirectory, String newFileName) {
		
	}
 
	@Override
	public void fileNotFound() {
		setChanged();
		notifyObservers("File not found");
	}

	@Override
	public void uploadComplete(String fileName, String newDirectory, String newFileName) {
		setChanged();
		notifyObservers("The file " + fileName + " is uploaded to the server into " + newDirectory + " as " + newFileName);
	}

}
