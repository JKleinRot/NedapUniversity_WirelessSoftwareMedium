package packet.header;

/**
 * The header put at the start of the data in the UDP datagram to be able to
 * reliably transfer data over the UDP connection. It contains a sequence
 * number, an acknowledgement number, a data integrity measure, one or several
 * flags and a download ID.
 * 
 * @author janine.kleinrot
 */
public interface Header {

	/**
	 * Returns the header in a byte array.
	 * 
	 * @return the header in byte array format
	 */
	public byte[] getBytes();

	/**
	 * Returns the sequence number of the header
	 * 
	 * @return the sequence number
	 */
	public int getSequenceNumber();

	/**
	 * Returns the acknowledgement number of the header
	 * 
	 * @return the acknowledgement number
	 */
	public int getAcknowledgementNumber();

	/**
	 * Returns the flags of the header
	 * 
	 * @return the flags
	 */
	public Flags getFlags();

	/**
	 * Returns the types of the header
	 * 
	 * @return the types
	 */
	public Types getTypes();

	/**
	 * Returns the download number of the header
	 * 
	 * @return the download number
	 */
	public int getDownloadNumber();

	/**
	 * Returns the length of the header
	 * 
	 * @return the length
	 */
	public int getLength();

}
