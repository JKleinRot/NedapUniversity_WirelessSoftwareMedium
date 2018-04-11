package protocol.file.packet.header.parts;

public enum Types {

	DATA(new byte[] { 0b00000000, 0b00000000, 0b00000000, 0b00000001 }), FILENAME(
			new byte[] { 0b00000000, 0b00000000, 0b00000000, 0b00000010 }), DIRECTORY(
					new byte[] { 0b00000000, 0b00000000, 0b00000000, 0b00000100 }), STATISTICS(
							new byte[] { 0b00000000, 0b00000000, 0b00000000, 0b00001000 });

	/** The bytes */
	private byte[] bytes;

	/**
	 * Creates a new Types.
	 * 
	 * @param bytes
	 *            The bytes of the types
	 */
	private Types(byte[] bytes) {
		this.bytes = bytes;
	}

	/**
	 * Returns the bytes of the types.
	 * 
	 * @return the bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}
}
