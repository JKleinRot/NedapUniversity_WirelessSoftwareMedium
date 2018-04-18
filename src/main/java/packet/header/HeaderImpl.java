package packet.header;

import java.nio.ByteBuffer;

public class HeaderImpl implements Header {

	/** The sequence number */
	private int sequenceNumber;

	/** The acknowledgement number */
	private int acknowledgementNumber;

	/** The flags */
	private Flags flags;

	/** The types */
	private Types types;

	/** The download number */
	private int downloadNumber;

	/** The length of the header */
	private static final int headerLength = 20;

	/** The sequence number offset in the header */
	private static final int sequenceNumberOffset = 0;

	/** The acknowledgement number offset in the header */
	private static final int acknowledgementNumberOffset = 4;

	/** The flags offset in the header */
	private static final int flagsOffset = 8;

	/** The types offset in the header */
	private static final int typesOffset = 12;

	/** The download number offset in the header */
	private static final int downloadNumberOffset = 16;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a header.
	 * 
	 * @param sequenceNumber
	 *            The sequence number
	 * @param acknowledgementNumber
	 *            The acknowledgement number
	 * @param flags
	 *            The flags
	 * @param types
	 *            The types
	 * @param downloadNumber
	 *            The download number
	 */
	public HeaderImpl(int sequenceNumber, int acknowledgementNumber, Flags flags, Types types, int downloadNumber) {
		this.sequenceNumber = sequenceNumber;
		this.acknowledgementNumber = acknowledgementNumber;
		this.flags = flags;
		this.types = types;
		this.downloadNumber = downloadNumber;
	}

	@Override
	public byte[] getBytes() {
		ByteBuffer buffer = ByteBuffer.allocate(headerLength);
		ByteBuffer seqNum = ByteBuffer.allocate(4).putInt(sequenceNumber);
		ByteBuffer ackNum = ByteBuffer.allocate(4).putInt(acknowledgementNumber);
		ByteBuffer flag = ByteBuffer.allocate(4).put(flags.getBytes());
		ByteBuffer type = ByteBuffer.allocate(4).put(types.getBytes());
		ByteBuffer dowNum = ByteBuffer.allocate(4).putInt(downloadNumber);
		buffer.position(sequenceNumberOffset);
		buffer.put(seqNum.array());
		buffer.position(acknowledgementNumberOffset);
		buffer.put(ackNum.array());
		buffer.position(flagsOffset);
		buffer.put(flag.array());
		buffer.position(typesOffset);
		buffer.put(type.array());
		buffer.position(downloadNumberOffset);
		buffer.put(dowNum.array());
		return buffer.array();
	}

	@Override
	public int getSequenceNumber() {
		return sequenceNumber;
	}

	@Override
	public int getAcknowledgementNumber() {
		return acknowledgementNumber;
	}

	@Override
	public Flags getFlags() {
		return flags;
	}

	@Override
	public Types getTypes() {
		return types;
	}

	@Override
	public int getDownloadNumber() {
		return downloadNumber;
	}

	@Override
	public int getLength() {
		return headerLength;
	}
}
