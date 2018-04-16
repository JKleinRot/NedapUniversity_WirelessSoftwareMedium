package client.processmanager;

import java.util.ArrayList;
import java.util.List;
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
	
	/** The upload number */
	private int uploadNumber;
	
	/** The uploaders */
	private List<ClientUploader> uploaders;
	
	/** The downloaders */
	private List<ClientDownloader> downloaders;

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
		uploadNumber = 1;
		uploaders = new ArrayList<>();
		downloaders = new ArrayList<>();
	}

	@Override
	public void handleUploadRequest(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		ClientUploader dataUploader = new ClientUploaderImpl(client, this, downloadNumber);
		uploadNumber++;
		uploaders.add(dataUploader);
		
		final Thread uploadThread = new Thread(new Runnable() {
			@Override
			public void run() {
				dataUploader.upload(fileName, fileDirectory, newDirectory, newFileName);
			}
		});
		uploadThread.start();
	}
	
	@Override
	public void handleDownloadRequest(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		ClientDownloader dataDownloader = new ClientDownloaderImpl(client, this, downloadNumber);
		downloadNumber++;
		downloaders.add(dataDownloader);
		
		final Thread downloadThread = new Thread(new Runnable() {
			@Override
			public void run() {
				dataDownloader.download(fileName, fileDirectory, newDirectory, newFileName);
			}
		});
		downloadThread.start();
	}
 
	@Override
	public void fileNotFound() {
		setChanged();
		notifyObservers("File not found");
	}

	@Override
	public void uploadComplete(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		setChanged();
		notifyObservers("The file " + fileName + " from " + fileDirectory + " is uploaded to the server into " + newDirectory + " as " + newFileName);
	}

	@Override
	public void downloadComplete(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		setChanged();
		notifyObservers("The file " + fileName + " from " + fileDirectory + " is downloaded from the server into " + newDirectory + " as " + newFileName);
	}

	@Override
	public String getStatistics() {
		StringBuilder builder = new StringBuilder();
		builder.append("Statistics of uploaders: \n");
		for (ClientUploader uploader : uploaders) {
			builder.append(uploader.getStatistics());
		}
		builder.append("Statistics of downloaders: \n");
		for (ClientDownloader downloader : downloaders) {
			builder.append(downloader.getStatistics());
		}
		return builder.toString();
	}

}
