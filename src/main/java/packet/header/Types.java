package packet.header;

/**
 * The types a packet can have.
 * 
 * @author janine.kleinrot
 */
public enum Types {

	UNDEFINED(new byte[] { 0b00000000, 0b00000000, 0b00000000, 0b00000000 }), DATA(
			new byte[] { 0b00000000, 0b00000000, 0b00000000, 0b00000001 }), FILENAME(
					new byte[] { 0b00000000, 0b00000000, 0b00000000, 0b00000010 }), UPLOADCHARACTERISTICS(
							new byte[] { 0b00000000, 0b00000000, 0b00000000, 0b00000100 }), STATISTICS(
									new byte[] { 0b00000000, 0b00000000, 0b00000000, 0b00001000 }), DATAINTEGRITY(
											new byte[] { 0b00000000, 0b00000000, 0b00000000, 0b00010000 }), ACK(
													new byte[] { 0b00000000, 0b00000000, 0b00000000,
															0b00100000 }), DOWNLOADCHARACTERISTICS(
																	new byte[] { 0b00000000, 0b00000000, 0b00000000,
																			0b01000000 }), LASTACK(
																					new byte[] { 0b00000000, 0b00000000,
																							0b00000000,
																							(byte) 0b10000000 }), FILENOTFOUND(
																									new byte[] {
																											0b00000000,
																											0b00000000,
																											0b00000000,
																											0b00000011 });

	/** The bytes */
	private byte[] bytes;

	/**
	 * Creates a new types.
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
