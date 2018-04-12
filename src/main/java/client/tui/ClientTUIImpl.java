package client.tui;

import java.util.Observable;
import java.util.Scanner;

import client.processmanager.ProcessManager;
import downloader.DataDownloader;
import statistics.StatisticsManager;
import storage.StorageRequester;
import uploader.DataUploader;

public class ClientTUIImpl implements ClientTUI {

	/** The process manager */
	private ProcessManager processManager;

	/** The scanner that reads input */
	private Scanner in;
	
	/** The file name */
	private String fileName;
	
	/** The new directory */
	private String newDirectory;
	
	/** The new file name */
	private String newFileName;

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
				
			} else if (words.length == 2 && words[0].equals("upload")) {
				fileName = words[1];
				input = readInput("To what directory do you want to upload your file? Please enter \"upload to\" followed by the directory");
			} else if (words.length == 3 && words[0].equals("upload") && words[1].equals("to")) {
				input = readInput("What would you like the file to be named? Please enter \"upload as\" followed by the file name");
				newDirectory = words[2];
			} else if (words.length == 3 && words[0].equals("upload") && words[1].equals("as")) {
				newFileName = words[2];
				processManager.handleRequest(words[0], fileName, newDirectory, newFileName);
				input = readInput("New question");
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg.equals("File not found")) {
			System.out.println("This file is not found. Please enter another file name");
		}
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
