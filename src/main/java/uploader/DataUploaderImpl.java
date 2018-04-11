package uploader;

import java.util.Observable;

import client.Client;

public class DataUploaderImpl extends Observable implements DataUploader {

	/** The client */
	private Client client;
	
	/**
	 * -----Constructor-----
	 * 
	 * Creates a DataUploaderImpl.
	 * 
	 * @param client
	 *            The client
	 */
	public DataUploaderImpl(Client client) {
		this.client = client;
		
	}
	
	@Override
	public void upload(String filename) {
		
	}
}
