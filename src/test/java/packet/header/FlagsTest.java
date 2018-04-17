package packet.header;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

/**
 * Test program for Flags
 * 
 * @author janine.kleinrot
 */
public class FlagsTest {
	
	/** 
	 * Tests that all the values are a Flag. 
	 */
	@Test
	public void testPossibleValues() {
		assertNotNull(Flags.valueOf("UNDEFINED"));
		assertNotNull(Flags.valueOf("UPLOAD"));
		assertNotNull(Flags.valueOf("DOWNLOAD"));
		assertNotNull(Flags.valueOf("STATISTICS"));
		assertNotNull(Flags.valueOf("FILEREQUEST"));
		assertNotNull(Flags.valueOf("UPLOAD_MORETOCOME"));
		assertNotNull(Flags.valueOf("UPLOAD_LAST"));
		assertNotNull(Flags.valueOf("UPLOAD_DATAINTEGRITY"));
		assertNotNull(Flags.valueOf("DOWNLOAD_MORETOCOME"));
		assertNotNull(Flags.valueOf("DOWNLOAD_LAST"));
		assertNotNull(Flags.valueOf("DOWNLOAD_DATAINTEGRITY"));
	}
	
	/**
	 * Tests the byte values of the flags.
	 */
	@Test
	public void testByteValueOfFlags() {
		assertArrayEquals(new byte[] {0, 0, 0, 0}, Flags.UNDEFINED.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, 1}, Flags.UPLOAD.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, 2}, Flags.DOWNLOAD.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, 4}, Flags.STATISTICS.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, 8}, Flags.FILEREQUEST.getBytes());
		assertArrayEquals(new byte[] {0, 0, 1, 1}, Flags.UPLOAD_MORETOCOME.getBytes());
		assertArrayEquals(new byte[] {0, 0, 2, 1}, Flags.UPLOAD_LAST.getBytes());
		assertArrayEquals(new byte[] {0, 0, 4, 1}, Flags.UPLOAD_DATAINTEGRITY.getBytes());
		assertArrayEquals(new byte[] {0, 0, 1, 2}, Flags.DOWNLOAD_MORETOCOME.getBytes());
		assertArrayEquals(new byte[] {0, 0, 2, 2}, Flags.DOWNLOAD_LAST.getBytes());
		assertArrayEquals(new byte[] {0, 0, 4, 2}, Flags.DOWNLOAD_DATAINTEGRITY.getBytes());
	}
	
	/**
	 * Tests the int values of the flags.
	 */
	@Test
	public void testIntValueOfFlags() {
		assertEquals(0, ByteBuffer.wrap(Flags.UNDEFINED.getBytes()).getInt());
		assertEquals(1, ByteBuffer.wrap(Flags.UPLOAD.getBytes()).getInt());
		assertEquals(2, ByteBuffer.wrap(Flags.DOWNLOAD.getBytes()).getInt());
		assertEquals(4, ByteBuffer.wrap(Flags.STATISTICS.getBytes()).getInt());
		assertEquals(8, ByteBuffer.wrap(Flags.FILEREQUEST.getBytes()).getInt());
		assertEquals(257, ByteBuffer.wrap(Flags.UPLOAD_MORETOCOME.getBytes()).getInt());
		assertEquals(513, ByteBuffer.wrap(Flags.UPLOAD_LAST.getBytes()).getInt());
		assertEquals(1025, ByteBuffer.wrap(Flags.UPLOAD_DATAINTEGRITY.getBytes()).getInt());
		assertEquals(258, ByteBuffer.wrap(Flags.DOWNLOAD_MORETOCOME.getBytes()).getInt());
		assertEquals(514, ByteBuffer.wrap(Flags.DOWNLOAD_LAST.getBytes()).getInt());
		assertEquals(1026, ByteBuffer.wrap(Flags.DOWNLOAD_DATAINTEGRITY.getBytes()).getInt());
	}

}
