package client.processmanager;

import java.util.Observable;

import client.Client;
import client.downloader.ClientDownloader;
import client.downloader.ClientDownloaderImpl;
import client.uploader.ClientUploader;
import client.uploader.ClientUploaderImpl;

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
	public void handleUploadRequest(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		ClientUploader dataUploader = new ClientUploaderImpl(client, this, downloadNumber);
		downloadNumber++;
		dataUploader.upload(fileName, fileDirectory, newDirectory, newFileName);
	}
	
	@Override
	public void handleDownloadRequest(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		ClientDownloader dataDownloader = new ClientDownloaderImpl(client, this, downloadNumber);
		downloadNumber++;
		dataDownloader.download(fileName, fileDirectory, newDirectory, newFileName);
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
