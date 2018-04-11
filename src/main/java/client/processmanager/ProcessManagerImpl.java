package client.processmanager;

import java.util.Observable;

import client.Client;
import downloader.DataDownloader;
import statistics.StatisticsManager;
import storage.StorageRequester;
import uploader.DataUploader;
import uploader.DataUploaderImpl;

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
	public void handleRequest(String action, String fileName, String newDirectory, String newFileName) {
		if (action.equals("download")) {

		} else if (action.equals("upload")) {
			DataUploader dataUploader = new DataUploaderImpl(client, this, downloadNumber);
			downloadNumber++;
			dataUploader.upload(fileName, newDirectory, newFileName);
		}
	}

	@Override
	public void fileNotFound() {
		setChanged();
		notifyObservers("File not found");
	}

}
