package client.processmanager;

import java.util.Observable;

import client.Client;
import client.actors.DataDownloader;
import client.actors.DataUploader;
import client.actors.DataUploaderImpl;
import client.actors.StatisticsManager;
import client.actors.StorageRequester;

public class ProcessManagerImpl extends Observable implements ProcessManager {

	/** The client */
	private Client client;

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
	}
	
	@Override
	public void handleRequest(String[] words) {
		if (words.length == 2 && words[0].equals("download")) {
			
		} else if (words.length == 2 && words[0].equals("upload")) {
			DataUploader dataUploader = new DataUploaderImpl(client);
			dataUploader.upload(words[1]);
		}
	}

}
