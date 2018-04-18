package client.tui;

import java.util.Observable;
import java.util.Scanner;

import client.processmanager.ProcessManager;

public class ClientTUIImpl implements ClientTUI {

	/** The process manager */
	private ProcessManager processManager;

	/** The scanner that reads input */
	private Scanner in;

	/** The file name */
	private String fileName;

	/** The file directory */
	private String fileDirectory;

	/** The new directory */
	private String newDirectory;

	/** The new file name */
	private String newFileName;

	/** Whether the user is requesting an upload */
	private boolean isUploadRequest;

	/** Whether the user has set the file to upload */
	private boolean isUploadFileSet;

	/** Whether the user has set the location of the file to upload */
	private boolean isUploadDirectorySet;

	/** Whether the user has set the upload location */
	private boolean isUploadLocationSet;

	/** Whether the user is requesting an upload */
	private boolean isDownloadRequest;

	/** Whether the user has set the file to upload */
	private boolean isDownloadFileSet;

	/** Whether the user has set the location of the file to download */
	private boolean isDownloadDirectorySet;

	/** Whether the user has set the upload location */
	private boolean isDownloadLocationSet;

	/** Whether the server is uploading or downloading */
	private boolean isWorking;

	/** Whether the user has requested files */
	private boolean isFileRequest;

	/** Whether the current upload or download is paused */
	private boolean isPaused;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a client TUI and adds the process manager as observer.
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
				"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
						+ "To pause and resume a download or upload use (pause) and (resume).\n"
						+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
						+ "Please enter the word between bracket to perform the action");
		while (isRunning) {
			String[] words = input.split(" ");
			if (words.length == 1 && words[0].equals("download")) {
				if (!isDownloadRequest && !isUploadRequest && !isWorking) {
					input = readInput(
							"What file do you want to download? Please enter \"download\" followed by the file name");
					isDownloadRequest = true;
				} else if (isDownloadRequest) {
					input = readInput(
							"Already requesting a download. Please enter the desired parameters or enter \"abort\" to stop the current action");
				} else if (isWorking) {
					System.out.println("Already uploading or downloading a file");
					input = readInput(
							"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
									+ "To pause and resume a download or upload use (pause) and (resume).\n"
									+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
									+ "Please enter the word between bracket to perform the action");

				} else {
					input = readInput(
							"Please enter the desired parameters or enter \"abort\" to stop the current action");
				}
			} else if (words.length == 2 && words[0].equals("download")) {
				if (isDownloadRequest && !isDownloadFileSet) {
					fileName = words[1];
					input = readInput(
							"In what directory is this file located? Please enter \"upload from\" followed by the complete directory of the file followed by a /");
					isDownloadFileSet = true;
				} else {
					input = readInput(
							"Please enter the desired parameters or enter \"abort\" to stop the current action");
				}
			} else if (words.length == 3 && words[0].equals("download") && words[1].equals("from")) {
				if (isDownloadFileSet && !isDownloadDirectorySet) {
					fileDirectory = words[2];
					input = readInput(
							"To what directory do you want to download your file? Please enter \"upload to\" followed by the directory");
					isDownloadDirectorySet = true;
				}
			} else if (words.length == 3 && words[0].equals("download") && words[1].equals("to")) {
				if (isDownloadDirectorySet && !isDownloadLocationSet) {
					newDirectory = words[2];
					input = readInput(
							"What would you like the file to be named? Please enter \"upload as\" followed by the file name");
					isDownloadLocationSet = true;
				} else {
					input = readInput(
							"Please enter the desired parameters or enter \"abort\" to stop the current action");
				}
			} else if (words.length == 3 && words[0].equals("download") && words[1].equals("as")) {
				if (isDownloadLocationSet) {
					newFileName = words[2];
					processManager.handleDownloadRequest(fileName, fileDirectory, newDirectory, newFileName);
					setAllBooleansFalse();
					isWorking = true;
					input = readInput(
							"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
									+ "To pause and resume a download or upload use (pause) and (resume).\n"
									+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
									+ "Please enter the word between bracket to perform the action");
				} else {
					input = readInput(
							"Please enter the desired parameters or enter \"abort\" to stop the current action");
				}
			} else if (words.length == 1 && words[0].equals("upload")) {
				if (!isUploadRequest && !isDownloadRequest && !isWorking) {
					input = readInput(
							"What file do you want to upload? Please enter \"upload\" followed by the file name");
					isUploadRequest = true;
				} else if (isUploadRequest) {
					input = readInput(
							"Already requesting an upload. Please enter the desired parameters or enter \"abort\" to stop the current action");
				} else if (isWorking) {
					System.out.println("Already uploading or downloading a file");
					input = readInput(
							"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
									+ "To pause and resume a download or upload use (pause) and (resume).\n"
									+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
									+ "Please enter the word between bracket to perform the action");
				} else {
					input = readInput(
							"Please enter the desired parameters or enter \"abort\" to stop the current action");
				}
			} else if (words.length == 2 && words[0].equals("upload")) {
				if (isUploadRequest && !isUploadFileSet) {
					fileName = words[1];
					input = readInput(
							"In what directory is this file located? Please enter \"upload from\" followed by the complete directory of the file followed by a /");
					isUploadFileSet = true;
				} else {
					input = readInput(
							"Please enter the desired parameters or enter \"abort\" to stop the current action");
				}
			} else if (words.length == 3 && words[0].equals("upload") && words[1].equals("from")) {
				if (isUploadFileSet && !isUploadDirectorySet) {
					fileDirectory = words[2];
					input = readInput(
							"To what directory do you want to upload your file? Please enter \"upload to\" followed by the directory");
					isUploadDirectorySet = true;
				}
			} else if (words.length == 3 && words[0].equals("upload") && words[1].equals("to")) {
				if (isUploadDirectorySet && !isUploadLocationSet) {
					newDirectory = words[2];
					input = readInput(
							"What would you like the file to be named? Please enter \"upload as\" followed by the file name");
					isUploadLocationSet = true;
				} else {
					input = readInput(
							"Please enter the desired parameters or enter \"abort\" to stop the current action");
				}
			} else if (words.length == 3 && words[0].equals("upload") && words[1].equals("as")) {
				if (isUploadLocationSet) {
					newFileName = words[2];
					processManager.handleUploadRequest(fileName, fileDirectory, newDirectory, newFileName);
					setAllBooleansFalse();
					isWorking = true;
					input = readInput(
							"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
									+ "To pause and resume a download or upload use (pause) and (resume).\n"
									+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
									+ "Please enter the word between bracket to perform the action");
				} else {
					input = readInput(
							"Please enter the desired parameters or enter \"abort\" to stop the current action");
				}
			} else if (words.length == 1 && words[0].equals("abort")) {
				setAllBooleansFalse();
				input = readInput(
						"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
								+ "To pause and resume a download or upload use (pause) and (resume).\n"
								+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
								+ "Please enter the word between bracket to perform the action");
			} else if (words.length == 1 && words[0].equals("statistics")) {
				System.out.println(processManager.getStatistics());
				input = readInput(
						"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
								+ "To pause and resume a download or upload use (pause) and (resume).\n"
								+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
								+ "Please enter the word between bracket to perform the action");
			} else if (words.length == 1 && words[0].equals("files")) {
				if (!isWorking && !isFileRequest && !isDownloadRequest && !isUploadRequest) {
					input = readInput(
							"Of what directory do you want to see the subdirectories and files? Please enter \"files\" followed by the desired directory (for the top directory enter \" / \")");
					isFileRequest = true;
				} else if (isWorking) {
					System.out.println(
							"Cannot request files while uploading or downloading a file. Please wait until the download or upload is finished");
					input = readInput(
							"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
									+ "To pause and resume a download or upload use (pause) and (resume).\n"
									+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
									+ "Please enter the word between bracket to perform the action");
				} else {
					input = readInput(
							"Please enter the desired parameters or enter \"abort\" to stop the current action");
				}
			} else if (words.length == 2 && words[0].equals("files")) {
				if (isFileRequest) {
					String filesAndDirectories = processManager.handleFilesRequest(words[1]);
					System.out.println(filesAndDirectories);
					input = readInput(
							"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
									+ "To pause and resume a download or upload use (pause) and (resume).\n"
									+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
									+ "Please enter the word between bracket to perform the action");
					setAllBooleansFalse();
				} else {
					input = readInput(
							"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
									+ "To pause and resume a download or upload use (pause) and (resume).\n"
									+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
									+ "Please enter the word between bracket to perform the action");
				}
			} else if (words.length == 1 && words[0].equals("pause")) {
				if (isWorking && !isPaused) {
					processManager.pause();
					System.out.println("The current upload or download is paused");
					input = readInput(
							"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
									+ "To pause and resume a download or upload use (pause) and (resume).\n"
									+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
									+ "Please enter the word between bracket to perform the action");
					isPaused = true;
				} else if (isWorking && isPaused) {
					System.out.println("The current upload or download is already paused");
					input = readInput(
							"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
									+ "To pause and resume a download or upload use (pause) and (resume).\n"
									+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
									+ "Please enter the word between bracket to perform the action");
				} else if (!isWorking) {
					System.out.println("There is no upload or download to pause");
					input = readInput(
							"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
									+ "To pause and resume a download or upload use (pause) and (resume).\n"
									+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
									+ "Please enter the word between bracket to perform the action");
				}
			} else if (words.length == 1 && words[0].equals("resume")) {
				if (isWorking && isPaused) {
					processManager.resume();
					System.out.println("The pause upload or download is resumed");
					input = readInput(
							"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
									+ "To pause and resume a download or upload use (pause) and (resume).\n"
									+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
									+ "Please enter the word between bracket to perform the action");
					isPaused = false;
				} else if (!isPaused || !isWorking) {
					System.out.println("There is no paused upload or download to resume");
					input = readInput(
							"Do you want to upload (upload), download (download), request files (files) or request statistics (statistics)?\n"
									+ "To pause and resume a download or upload use (pause) and (resume).\n"
									+ "To stop the client at any time use (exit), the current transfer will be aborted and incomplete files may be saved.\n"
									+ "Please enter the word between bracket to perform the action");
				}
			} else if (words.length == 1 && words[0].equals("exit")) {
				System.out.println("Goodbye");
				isRunning = false;
			} else {
				input = readInput("Please enter the desired parameters or enter \"abort\" to stop the current action");
			}
		}
	}

	/**
	 * Sets all booleans to false to let the user request a new action.
	 */
	private void setAllBooleansFalse() {
		isUploadRequest = false;
		isUploadFileSet = false;
		isUploadDirectorySet = false;
		isUploadLocationSet = false;
		isDownloadRequest = false;
		isDownloadFileSet = false;
		isDownloadDirectorySet = false;
		isDownloadLocationSet = false;
		isFileRequest = false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg.equals("File not found")) {
			System.out.println("This file is not found. Please try to download or upload another file");
		} else if (((String) arg).contains("uploaded to the server")) {
			isWorking = false;
			System.out.println(arg);
		} else if (((String) arg).contains("downloaded from the server")) {
			isWorking = false;
			System.out.println(arg);
		} else if (((String) arg).contains("incorrectly uploaded")) {
			isWorking = false;
			System.out.println(arg);
		} else if (((String) arg).contains("incorrectly downloaded")) {
			isWorking = false;
			System.out.println(arg);
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
