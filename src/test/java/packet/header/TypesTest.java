package packet.header;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

/**
 * Test program for Types
 * 
 * @author janine.kleinrot
 */
public class TypesTest {

	/** 
	 * Tests that all the values are a Flag. 
	 */
	@Test
	public void testPossibleValues() {
		assertNotNull(Types.valueOf("UNDEFINED"));
		assertNotNull(Types.valueOf("DATA"));
		assertNotNull(Types.valueOf("FILENAME"));
		assertNotNull(Types.valueOf("UPLOADCHARACTERISTICS"));
		assertNotNull(Types.valueOf("STATISTICS"));
		assertNotNull(Types.valueOf("DATAINTEGRITY"));
		assertNotNull(Types.valueOf("ACK"));
		assertNotNull(Types.valueOf("DOWNLOADCHARACTERISTICS"));
		assertNotNull(Types.valueOf("LASTACK"));
		assertNotNull(Types.valueOf("FILENOTFOUND"));
	}
	
	/**
	 * Tests the byte values of the types.
	 */
	@Test
	public void testByteValueOfTypes() {
		assertArrayEquals(new byte[] {0, 0, 0, 0}, Types.UNDEFINED.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, 1}, Types.DATA.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, 2}, Types.FILENAME.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, 4}, Types.UPLOADCHARACTERISTICS.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, 8}, Types.STATISTICS.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, 16}, Types.DATAINTEGRITY.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, 32}, Types.ACK.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, 64}, Types.DOWNLOADCHARACTERISTICS.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, -128}, Types.LASTACK.getBytes());
		assertArrayEquals(new byte[] {0, 0, 0, 3}, Types.FILENOTFOUND.getBytes());
	}
	
	/**
	 * Tests the int values of the types.
	 */
	@Test
	public void testIntValueOfTypes() {
		assertEquals(0, ByteBuffer.wrap(Types.UNDEFINED.getBytes()).getInt());
		assertEquals(1, ByteBuffer.wrap(Types.DATA.getBytes()).getInt());
		assertEquals(2, ByteBuffer.wrap(Types.FILENAME.getBytes()).getInt());
		assertEquals(4, ByteBuffer.wrap(Types.UPLOADCHARACTERISTICS.getBytes()).getInt());
		assertEquals(8, ByteBuffer.wrap(Types.STATISTICS.getBytes()).getInt());
		assertEquals(16, ByteBuffer.wrap(Types.DATAINTEGRITY.getBytes()).getInt());
		assertEquals(32, ByteBuffer.wrap(Types.ACK.getBytes()).getInt());
		assertEquals(64, ByteBuffer.wrap(Types.DOWNLOADCHARACTERISTICS.getBytes()).getInt());
		assertEquals(128, ByteBuffer.wrap(Types.LASTACK.getBytes()).getInt());
		assertEquals(3, ByteBuffer.wrap(Types.FILENOTFOUND.getBytes()).getInt());
	}
}
