package protocol.header;

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
	public byte[] getHeader();
}
