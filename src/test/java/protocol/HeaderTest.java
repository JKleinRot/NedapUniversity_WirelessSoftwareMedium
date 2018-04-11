package protocol;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import protocol.header.Header;
import protocol.header.HeaderImpl;
import protocol.header.parts.Flags;
import protocol.header.parts.Types;

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
		flags = null;
		types = null;
		downloadNumber = 1;
		header = new HeaderImpl(sequenceNumber, acknowledgementNumber, flags, types, downloadNumber);
	}
	
	/** 
	 * Checks that the correct byte array is returned.
	 */
	@Test
	public void testGetHeader() {
		byte[] expectedHeaderBytes = new byte[] {0, 0, 0, 10, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1};
		byte[] headerBytes = header.getHeader();
		assertArrayEquals(expectedHeaderBytes, headerBytes);
	}
}
