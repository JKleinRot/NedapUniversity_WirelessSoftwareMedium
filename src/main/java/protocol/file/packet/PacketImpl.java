package protocol.file.packet;

import protocol.file.packet.header.Header;

public class PacketImpl implements Packet {

	/** The header */
	private Header header;

	/** The data */
	private byte[] data;

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

}
