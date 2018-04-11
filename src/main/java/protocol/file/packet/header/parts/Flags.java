package protocol.file.packet.header.parts;

public enum Flags {

	UPLOAD(new byte[] {0b00000000, 0b000000000, 0b00000000, 0b00000001}),
	DOWNLOAD(new byte[] {0b00000000, 0b00000000, 0b00000000, 0b00000010}),
	STATISTICS(new byte[] {0b00000000, 0b00000000, 0b00000000, 0b00000100}),
	FILEREQUEST(new byte[] {0b00000000, 0b00000000, 0b00000000, 0b00001000}),
	UPLOAD_MORETOCOME(new byte[] {0b00000000, 0b00000000, 0b00000001, 0b00000001}),
	UPLOAD_LAST(new byte[] {0b00000000, 0b00000000, 0b00000010, 0b00000001}),
	DOWNLOAD_MORETOCOME(new byte[] {0b00000000, 0b00000000, 0b00000001, 0b00000010}),
	DOWNLOAD_LAST(new byte[] {0b00000000, 0b00000000, 0b00000010, 0b00000010});
	
	/** The bytes */
	private byte[] bytes;
	
	/** 
	 * Creates a new Flags. 
	 * @param bytes
	 * The bytes of the flags
	 */
	private Flags(byte[] bytes) {
		this.bytes = bytes;
	}
	
	/** 
	 * Returns the bytes of the flags.
	 * @return the bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}
	
	
}
