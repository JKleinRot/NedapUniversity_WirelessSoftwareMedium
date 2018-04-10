package client.tui;

import java.util.Observable;
import java.util.Scanner;

import client.actors.DataDownloader;
import client.actors.DataUploader;
import client.actors.StatisticsManager;
import client.actors.StorageRequester;

public class ClientTUIImpl implements ClientTUI {

	/** The data downloader */
	private DataDownloader dataDownloader;

	/** The data uploader */
	private DataUploader dataUploader;

	/** The statistics manager */
	private StatisticsManager statisticsManager;

	/** The storage requester */
	private StorageRequester storageRequester;

	/** The scanner that reads input */
	private Scanner in;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a ClientTUIImpl. Adds the actors as observers.
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
	public ClientTUIImpl(DataDownloader dataDownloader, DataUploader dataUploader, StatisticsManager statisticsManager,
			StorageRequester storageRequester) {
		this.dataDownloader = dataDownloader;
		this.dataUploader = dataUploader;
		this.statisticsManager = statisticsManager;
		this.storageRequester = storageRequester;
		in = new Scanner(System.in);
		((Observable) dataDownloader).addObserver(this);
		((Observable) dataUploader).addObserver(this);
		((Observable) statisticsManager).addObserver(this);
		((Observable) storageRequester).addObserver(this);
	}

	@Override
	public void run() {
		boolean isRunning = true;
		while (isRunning) {
			String input = readInput("Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)? " + 
					"Please enter the word between bracket to perform the action");
		}
	}

	@Override
	public void update(Observable o, Object arg) {

	}

	/**
	 * Reads standard input after sending prompt to standard output
	 * 
	 * @param prompt
	 *            The message displayed
	 * @return the message received
	 */
	private String readInput(String prompt) {
		String result = null;
		System.out.println(prompt);
		if (in.hasNextLine()) {
			result = in.nextLine();
		}
		return result;
	}
}
