package protocol;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

	/** The data integrity measure */
	private DataIntegrityMeasure dataIntegrityMeasure;

	/** The flags */
	private Flags flags;

	/** The download number */
	private short downloadNumber;
	
	@BeforeEach
	public void setup() {
		sequenceNumber = 10;
		acknowledgementNumber = 2;
		dataIntegrityMeasure = null;
		flags = null;
		downloadNumber = 1;
		header = new HeaderImpl(sequenceNumber, acknowledgementNumber, dataIntegrityMeasure, flags, downloadNumber);
	}
	
	/** 
	 * Checks that the correct byte array is returned.
	 */
	@Test
	public void testGetHeader() {
		byte[] expectedHeaderBytes = new byte[] {0, 0, 0, 10, 0, 0, 0, 2, 0, 0, 0, 1, 0, 1, 0, 1};
		byte[] headerBytes = header.getHeader();
		assertArrayEquals(expectedHeaderBytes, headerBytes);
	}
}
