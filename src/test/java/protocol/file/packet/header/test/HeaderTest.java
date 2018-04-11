package protocol.file.packet.header.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import protocol.file.packet.header.Header;
import protocol.file.packet.header.HeaderImpl;
import protocol.file.packet.header.parts.Flags;
import protocol.file.packet.header.parts.Types;

/**
 * Test program for Header.
 * 
 * @author janine.kleinrot
 */
public class HeaderTest {

	/** The header */
	private Header header;

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

	@BeforeEach
	public void setup() {
		sequenceNumber = 10;
		acknowledgementNumber = 2;
		flags = Flags.UPLOAD_MORETOCOME;
		types = Types.DATA;
		downloadNumber = 1;
		header = new HeaderImpl(sequenceNumber, acknowledgementNumber, flags, types, downloadNumber);
	}

	/**
	 * Checks that the correct byte array is returned.
	 */
	@Test
	public void testGetHeader() {
		byte[] expectedHeaderBytes = new byte[] { 0, 0, 0, 10, 0, 0, 0, 2, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1 };
		byte[] headerBytes = header.getBytes();
		assertArrayEquals(expectedHeaderBytes, headerBytes);
	}
}
