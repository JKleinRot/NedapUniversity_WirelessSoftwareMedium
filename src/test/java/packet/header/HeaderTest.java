package packet.header;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import packet.header.Flags;
import packet.header.Header;
import packet.header.HeaderImpl;
import packet.header.Types;

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
	
	/** The length of the header */
	private int length;

	@BeforeEach
	public void setup() {
		sequenceNumber = 10;
		acknowledgementNumber = 2;
		flags = Flags.UPLOAD_MORETOCOME;
		types = Types.DATA;
		downloadNumber = 1;
		length = 20;
		header = new HeaderImpl(sequenceNumber, acknowledgementNumber, flags, types, downloadNumber);
	}

	/**
	 * Tests that the correct byte array is returned.
	 */
	@Test
	public void testGetHeader() {
		byte[] expectedHeaderBytes = new byte[] { 0, 0, 0, 10, 0, 0, 0, 2, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1 };
		byte[] headerBytes = header.getBytes();
		assertArrayEquals(expectedHeaderBytes, headerBytes);
	}
	
	/**
	 * Tests getting the sequence number.
	 */
	@Test
	public void testGetSequenceNumber() {
		assertEquals(sequenceNumber, header.getSequenceNumber());
	}
	
	/**
	 * Tests getting the acknowledgement number.
	 */
	@Test
	public void testGetAcknowledgementNumber() {
		assertEquals(acknowledgementNumber, header.getAcknowledgementNumber());
	}
	
	/**
	 * Tests getting the flags.
	 */
	@Test
	public void testGetFlags() {
		assertEquals(flags, header.getFlags());
	}
	
	/**
	 * Tests getting the types.
	 */
	@Test
	public void testGetTypes() {
		assertEquals(types, header.getTypes());
	}
	
	/**
	 * Tests getting the acknowledgement number.
	 */
	@Test
	public void testGetDownloadNumber() {
		assertEquals(downloadNumber, header.getDownloadNumber());
	}
	
	/**
	 * Tests getting the length of the header.
	 */
	@Test
	public void testGetLength() {
		assertEquals(length, header.getLength());
	}
}
