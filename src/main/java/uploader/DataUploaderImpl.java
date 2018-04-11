package uploader;

import java.util.Observable;

import client.Client;
import client.processmanager.ProcessManager;
import protocol.file.File;
import protocol.file.FileDisassembler;
import protocol.file.FileDisassemblerImpl;

public class DataUploaderImpl extends Observable implements DataUploader {

	/** The client */
	private Client client;

	/** The file disassembler */
	private FileDisassembler fileDisassembler;

	/** The process manager */
	private ProcessManager processManager;

	/** The download number */
	private int downloadNumber;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a DataUploaderImpl.
	 * 
	 * @param client
	 *            The client
	 * @param processManager
	 *            The process manager
	 */
	public DataUploaderImpl(Client client, ProcessManager processManager, int downloadNumber) {
		this.client = client;
		this.downloadNumber = downloadNumber;
	}

	@Override
	public void upload(String fileName) {
		File file = getFileWithPacketsFromFile(fileName);
	}

	/**
	 * Returns a file with packets from the file with the provided file name.
	 * 
	 * @param fileName
	 *            The file name
	 * @return the file with packets
	 */
	private File getFileWithPacketsFromFile(String fileName) {
		fileDisassembler = new FileDisassemblerImpl(fileName, this, downloadNumber);
		File file = fileDisassembler.createFileWithPacketsFromFile();
		return file;
	}

	@Override
	public void notifyProcessManagerFileNotFound() {
		processManager.fileNotFound();
	}
}
