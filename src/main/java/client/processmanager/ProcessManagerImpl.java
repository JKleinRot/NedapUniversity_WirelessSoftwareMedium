package client.processmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import client.Client;
import client.downloader.ClientDownloader;
import client.downloader.ClientDownloaderImpl;
import client.uploader.ClientUploader;
import client.uploader.ClientUploaderImpl;
import filerequester.FileRequester;
import filerequester.FileRequesterImpl;

public class ProcessManagerImpl extends Observable implements ProcessManager {

	/** The client */
	private Client client;

	/** The download number */
	private int numberOfDownloaders;

	/** The upload number */
	private int numberOfUploaders;

	/** The uploaders */
	private List<ClientUploader> uploaders;

	/** The upload threads */
	private Map<Integer, Thread> uploadThreads;

	/** The downloaders */
	private List<ClientDownloader> downloaders;

	/** The download threads */
	private Map<Integer, Thread> downloadThreads;

	/** The file requester */
	private FileRequester fileRequester;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a process manager that handles the request from the user.
	 * 
	 * @param client
	 *            The client
	 */
	public ProcessManagerImpl(Client client) {
		this.client = client;
		numberOfDownloaders = 1;
		numberOfUploaders = 1;
		uploaders = new ArrayList<>();
		uploadThreads = new HashMap<>();
		downloaders = new ArrayList<>();
		downloadThreads = new HashMap<>();
		fileRequester = new FileRequesterImpl(client);
	}

	@Override
	public void handleUploadRequest(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		ClientUploader dataUploader = new ClientUploaderImpl(client, this, numberOfUploaders);
		uploaders.add(dataUploader);
		final Thread uploadThread = new Thread(new Runnable() {
			@Override
			public void run() {
				dataUploader.upload(fileName, fileDirectory, newDirectory, newFileName);
			}
		});
		uploadThreads.put(numberOfUploaders, uploadThread);
		numberOfUploaders++;
		uploadThread.start();
	}

	@Override
	public void handleDownloadRequest(String fileName, String fileDirectory, String newDirectory, String newFileName) {
		ClientDownloader dataDownloader = new ClientDownloaderImpl(client, this, numberOfDownloaders);
		downloaders.add(dataDownloader);
		final Thread downloadThread = new Thread(new Runnable() {
			@Override
			public void run() {
				dataDownloader.download(fileName, fileDirectory, newDirectory, newFileName);
			}
		});
		downloadThreads.put(numberOfDownloaders, downloadThread);
		numberOfDownloaders++;
		downloadThread.start();
	}

	@Override
	public void fileNotFound(ClientDownloader downloader) {
		Thread downloadThread = downloadThreads.get(downloader.getDownloadNumber());
		downloadThread.interrupt();
		downloaders.remove(downloader);
		downloadThreads.remove(downloader.getDownloadNumber());
		setChanged();
		notifyObservers("File not found");
	}

	@Override
	public void fileNotFound(ClientUploader uploader) {
		uploaders.remove(uploader);
		uploadThreads.remove(uploader.getUploadNumber());
		setChanged();
		notifyObservers("File not found");
	}

	@Override
	public void uploadComplete(String fileName, String fileDirectory, String newDirectory, String newFileName,
			ClientUploader uploader) {
		uploadThreads.remove(uploader.getUploadNumber());
		setChanged();
		notifyObservers("The file " + fileName + " from " + fileDirectory + " is uploaded to the server into "
				+ newDirectory + " as " + newFileName);
	}

	@Override
	public void downloadComplete(String fileName, String fileDirectory, String newDirectory, String newFileName,
			ClientDownloader downloader) {
		Thread downloadThread = downloadThreads.get(downloader.getDownloadNumber());
		downloadThread.interrupt();
		downloadThreads.remove(downloader.getDownloadNumber());
		setChanged();
		notifyObservers("The file " + fileName + " from " + fileDirectory + " is downloaded from the server into "
				+ newDirectory + " as " + newFileName);
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

	@Override
	public void uploadIncorrect(String fileName, String fileDirectory, String newDirectory, String newFileName,
			ClientUploader uploader) {
		uploadThreads.remove(uploader.getUploadNumber());
		setChanged();
		notifyObservers("The file " + fileName + " from " + fileDirectory
				+ " was incorrectly uploaded to the server into " + newDirectory + " as " + newFileName);
	}

	@Override
	public void downloadIncorrect(String fileName, String fileDirectory, String newDirectory, String newFileName,
			ClientDownloader downloader) {
		Thread downloadThread = downloadThreads.get(downloader.getDownloadNumber());
		downloadThread.interrupt();
		downloadThreads.remove(downloader.getDownloadNumber());
		setChanged();
		notifyObservers("The file " + fileName + " from " + fileDirectory
				+ " was incorrectly downloaded to the server into " + newDirectory + " as " + newFileName);
	}

	@Override
	public String handleFilesRequest(String directory) {
		String filesAndDirectories = fileRequester.handleFilesRequest(directory);
		return filesAndDirectories;
	}

	@Override
	public void pause() {
		if (uploaders.size() != 0) {
			ClientUploader uploader = uploaders.get(uploaders.size() - 1);
			uploader.pause();
		}
		if (downloaders.size() != 0) {
			ClientDownloader downloader = downloaders.get(downloaders.size() - 1);
			downloader.pause();
		}
	}

	@Override
	public void resume() {
		if (uploaders.size() != 0) {
			ClientUploader uploader = uploaders.get(uploaders.size() - 1);
			uploader.resume();
		} 
		if (downloaders.size() != 0) {
			ClientDownloader downloader = downloaders.get(downloaders.size() - 1);
			downloader.resume();
		}
	}

}
