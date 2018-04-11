package client.processmanager;

import java.util.Observable;

import client.actors.DataDownloader;
import client.actors.DataUploader;
import client.actors.StatisticsManager;
import client.actors.StorageRequester;

public class ProcessManagerImpl extends Observable implements ProcessManager {

	/** The data downloader */
	private DataDownloader dataDownloader;

	/** The data uploader */
	private DataUploader dataUploader;

	/** The statistics manager */
	private StatisticsManager statisticsManager;

	/** The storage requester */
	private StorageRequester storageRequester;

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
	public ProcessManagerImpl(DataDownloader dataDownloader, DataUploader dataUploader,
			StatisticsManager statisticsManager, StorageRequester storageRequester) {
		this.dataDownloader = dataDownloader;
		this.dataUploader = dataUploader;
		this.statisticsManager = statisticsManager;
		this.storageRequester = storageRequester;
	}
	
	@Override
	public void handleRequest(String[] words) {
		if (words.length == 2 && words[0].equals("download")) {
//			dataDownloader.download(words[1]);
		} else if (words.length == 2 && words[0].equals("upload")) {
			dataUploader.upload(words[1]);
		}
	}

}
