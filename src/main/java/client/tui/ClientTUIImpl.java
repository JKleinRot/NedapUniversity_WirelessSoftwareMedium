package client.tui;

import java.util.Observable;
import java.util.Scanner;

import client.actors.DataDownloader;
import client.actors.DataUploader;
import client.actors.StatisticsManager;
import client.actors.StorageRequester;
import client.processmanager.ProcessManager;

public class ClientTUIImpl implements ClientTUI {

	/** The process manager */
	private ProcessManager processManager;

	/** The scanner that reads input */
	private Scanner in;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a ClientTUIImpl. Adds the actors as observers.
	 * 
	 * @param processManager
	 *            The process manager
	 */
	public ClientTUIImpl(ProcessManager processManager) {
		this.processManager = processManager;
		in = new Scanner(System.in);
		((Observable) processManager).addObserver(this);
	}

	@Override
	public void run() {
		boolean isRunning = true;
		String input = readInput(
				"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)? "
						+ "Please enter the word between bracket to perform the action");
		while (isRunning) {
			String[] words = input.split(" ");
			if (words.length == 1 && words[0].equals("download")) {
				input = readInput(
						"What file do you want to download? Please enter \"download\" followed by the file name");
			} else if (words.length == 1 && words[0].equals("upload")) {
				input = readInput("What file do you want to upload? Please enter \"upload\" followed by the file name");
			} else if (words.length == 1 && words[0].equals("files")) {

			} else if (words.length == 1 && words[0].equals("statistics")) {

			} else if (words.length == 2 && words[0].equals("download")) {
				processManager.handleRequest(words);
			} else if (words.length == 2 && words[0].equals("upload")) {
				processManager.handleRequest(words);
			}
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
