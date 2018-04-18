package packet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import packet.header.Flags;
import packet.header.Header;
import packet.header.HeaderImpl;
import packet.header.Types;

/**
 * Test program for Packet
 * 
 * @author janine.kleinrot
 */
public class PacketTest {

	/** The header */
	private Header header;

	/** The sequence number */
	private static final int sequenceNumber = 100;

	/** The acknowledgement number */
	private static final int acknowledgementNumber = 0;

	/** The flags */
	private static final Flags flags = Flags.UPLOAD_LAST;

	/** The types */
	private static final Types types = Types.DATA;

	/** The download number */
	private static final int downloadNumber = 1;

	/** The data */
	private static final byte[] data = new byte[] { 1, 2, 3 };

	/** The packet */
	private Packet packet;

	@BeforeEach
	public void setup() {
		header = new HeaderImpl(sequenceNumber, acknowledgementNumber, flags, types, downloadNumber);
		packet = new PacketImpl(header, data);
	}

	/**
	 * Tests that the correct header is returned.
	 */
	@Test
	public void testGetHeader() {
		assertEquals(header, packet.getHeader());
	}

	/**
	 * Tests that the correct data is returned.
	 */
	@Test
	public void testGetData() {
		assertEquals(data, packet.getData());
	}

	@Test
	public void testGetBytes() {
		assertArrayEquals(
				ByteBuffer.allocate(header.getLength() + data.length).put(header.getBytes()).put(data).array(),
				packet.getBytes());
	}
}
