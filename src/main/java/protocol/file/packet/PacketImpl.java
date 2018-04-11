package protocol.file.packet;

import java.nio.ByteBuffer;

import protocol.file.packet.header.Header;

public class PacketImpl implements Packet {

	/** The header */
	private Header header;

	/** The data */
	private byte[] data;
	
	/** The header position */
	private static final int headerPosition = 0;
	
	/** The data position */
	private static final int dataPosition = 20;

	/**
	 * Creates a PacketImpl with a header and data.
	 * 
	 * @param header
	 *            The header
	 * @param data
	 *            The data
	 */
	public PacketImpl(Header header, byte[] data) {
		this.header = header;
		this.data = data;
	}

	@Override
	public Header getHeader() {
		return header;
	}

	@Override
	public byte[] getData() {
		return data;
	}
	
	@Override
	public int getLength() {
		return header.getLength() + data.length;
	}
	
	@Override
	public byte[] getBytes() {
		ByteBuffer buffer = ByteBuffer.allocate(getLength());
		buffer.position(headerPosition);
		buffer.put(header.getBytes());
		buffer.position(dataPosition);
		buffer.put(data);
		return buffer.array();
	}

}
