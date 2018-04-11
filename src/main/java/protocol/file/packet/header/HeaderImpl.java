package protocol.file.packet.header;

import java.nio.ByteBuffer;

import protocol.file.packet.header.parts.Flags;
import protocol.file.packet.header.parts.Types;

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
	 * Creates a HeaderImpl with the provided arguments.
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
	public byte[] getHeader() {
		ByteBuffer buffer = ByteBuffer.allocate(20);
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
}
