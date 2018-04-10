package protocol;

import java.nio.ByteBuffer;

public class HeaderImpl implements Header {

	/** The sequence number */
	private int sequenceNumber;

	/** The acknowledgement number */
	private int acknowledgementNumber;

	/** The data integrity measure */
	private DataIntegrityMeasure dataIntegrityMeasure;

	/** The flags */
	private Flags flags;

	/** The download number */
	private short downloadNumber;
	
	/** The sequence number offset in the header */
	private static final int sequenceNumberOffset = 0;
	
	/** The acknowledgement number offset in the header */
	private static final int acknowledgementNumberOffset = 4;
	
	/** The data integrity measure offset in the header */
	private static final int dataIntegrityMeasureOffset = 8;
	
	/** The flags offset in the header */
	private static final int flagsOffset = 12 ;
	
	/** The download number offset in the header */
	private static final int downloadNumberOffset = 14;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a HeaderImpl with the provided arguments.
	 * 
	 * @param sequenceNumber
	 *            The sequence number
	 * @param acknowledgementNumber
	 *            The acknowledgement number
	 * @param dataIntegrityMeasure
	 *            The data integrity measure
	 * @param flags
	 *            The flags
	 * @param downloadNumber
	 *            The download number
	 */
	public HeaderImpl(int sequenceNumber, int acknowledgementNumber, DataIntegrityMeasure dataIntegrityMeasure,
			Flags flags, short downloadNumber) {
		this.sequenceNumber = sequenceNumber;
		this.acknowledgementNumber = acknowledgementNumber;
		this.dataIntegrityMeasure = dataIntegrityMeasure;
		this.flags = flags;
		this.downloadNumber = downloadNumber;
	}
	
	@Override
	public byte[] getHeader() {
		ByteBuffer buffer = ByteBuffer.allocate(16);
		ByteBuffer seqNum = ByteBuffer.allocate(4).putInt(sequenceNumber);
		ByteBuffer ackNum = ByteBuffer.allocate(4).putInt(acknowledgementNumber);
		ByteBuffer dataIntegrity = ByteBuffer.allocate(4).putInt(1);
		ByteBuffer flags = ByteBuffer.allocate(2).putShort((short) 1);
		ByteBuffer dowNum = ByteBuffer.allocate(2).putShort(downloadNumber);
		buffer.position(sequenceNumberOffset);
		buffer.put(seqNum.array());
		buffer.position(acknowledgementNumberOffset);
		buffer.put(ackNum.array());
		buffer.position(dataIntegrityMeasureOffset);
		buffer.put(dataIntegrity.array());
		buffer.position(flagsOffset);
		buffer.put(flags.array());
		buffer.position(downloadNumberOffset);
		buffer.put(dowNum.array());
		return buffer.array();
	}		
}
