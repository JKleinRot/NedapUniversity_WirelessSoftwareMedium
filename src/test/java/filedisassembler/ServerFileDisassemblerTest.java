package filedisassembler;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import filedisassembler.ServerFileDisassembler;
import filedisassembler.ServerFileDisassemblerImpl;
import packet.Packet;
import packet.PacketImpl;
import packet.header.Flags;
import packet.header.Header;
import packet.header.HeaderImpl;
import packet.header.Types;
import server.uploader.ServerUploader;

/**
 * Test program for FileDisassembler.
 * 
 * @author janine.kleinrot
 */
public class ServerFileDisassemblerTest {

	/** The file disassembler */
	private ServerFileDisassembler fileDisassembler;

	/** The file disassembler for a longer file */
	private ServerFileDisassembler fileDisassemblerLong;

	/** The file disassembler for a really long file */
	private ServerFileDisassembler fileDisassemblerReallyLong;

	/** The mocks */
	private EasyMockSupport mocks;

	/** The data uploader */
	private ServerUploader dataUploader;

	/** The download number */
	private int downloadNumber;

	@BeforeEach
	public void setup() {
		mocks = new EasyMockSupport();
		dataUploader = mocks.createMock(ServerUploader.class);
		downloadNumber = 1;
		fileDisassembler = new ServerFileDisassemblerImpl("Test.txt", dataUploader, downloadNumber);
		fileDisassemblerLong = new ServerFileDisassemblerImpl("TestLong.txt", dataUploader, downloadNumber);
		fileDisassemblerReallyLong = new ServerFileDisassemblerImpl("trailer.mp4", dataUploader, downloadNumber);
	}

	/**
	 * Tests that a file is constructed with packets that contain headers and data.
	 * The file can fit in one packet.
	 */
	@Test
	public void testFileDisassemblerFromFileOnePacket() {
		int expectedSequenceNumber = 100;
		int expectedAcknowledgementNumber = 0;
		Flags expectedFlags = Flags.DOWNLOAD_LAST;
		Types expectedTypes = Types.DATA;
		int expectedDownloadNumber = downloadNumber;
		Header expectedHeader = new HeaderImpl(expectedSequenceNumber, expectedAcknowledgementNumber, expectedFlags,
				expectedTypes, expectedDownloadNumber);
		byte[] expectedData = "Hello!".getBytes();
		Packet expectedPacket = new PacketImpl(expectedHeader, expectedData);
		int expectedTotalDataSize = 6;

		Packet packet = fileDisassembler.getNextPacket();

		assertEquals(expectedPacket.getLength(), packet.getLength());
		assertEquals(expectedTotalDataSize, fileDisassembler.getTotalDataSize());
		assertArrayEquals(expectedData, packet.getData());
		assertEquals(expectedSequenceNumber, packet.getHeader().getSequenceNumber());
		assertEquals(expectedAcknowledgementNumber, packet.getHeader().getAcknowledgementNumber());
		assertEquals(expectedFlags, packet.getHeader().getFlags());
		assertEquals(expectedTypes, packet.getHeader().getTypes());
		assertEquals(expectedDownloadNumber, packet.getHeader().getDownloadNumber());
	}

	/**
	 * Tests that a file is constructed with packets that contain headers and data.
	 * The file is distributed over multiple packets.
	 */
	@Test
	public void testCreateFileWithPacketsFromFileMultiplePackets() {
		int expectedSequenceNumberFirstPacket = 100;
		int expectedSequenceNumberSecondPacket = 101;
		int expectedSequenceNumberThirdPacket = 102;
		int expectedAcknowledgementNumber = 0;
		Flags expectedFlagsFirstPacket = Flags.DOWNLOAD_MORETOCOME;
		Flags expectedFlagsSecondPacket = Flags.DOWNLOAD_MORETOCOME;
		Flags expectedFlagsThirdPacket = Flags.DOWNLOAD_LAST;
		Types expectedTypes = Types.DATA;
		int expectedDownloadNumber = downloadNumber;
		Header expectedHeaderFirstPacket = new HeaderImpl(expectedSequenceNumberFirstPacket,
				expectedAcknowledgementNumber, expectedFlagsFirstPacket, expectedTypes, expectedDownloadNumber);
		Header expectedHeaderSecondPacket = new HeaderImpl(expectedSequenceNumberSecondPacket,
				expectedAcknowledgementNumber, expectedFlagsSecondPacket, expectedTypes, expectedDownloadNumber);
		Header expectedHeaderThirdPacket = new HeaderImpl(expectedSequenceNumberThirdPacket,
				expectedAcknowledgementNumber, expectedFlagsThirdPacket, expectedTypes, expectedDownloadNumber);
		byte[] expectedDataFirstPacket = ("During the module you learned about networks, layers and different protocols. Where TCP guarantees reliable transfer of information, UDP does not. For this assignment you will create a wireless storage medium (think of it like a simple NAS), where you should achieve reliable file transfer using the UDP protocol.\r"
				+ "The assignment should be performed alone. Discussion about the assignment is allowed, but you should always be able to defend your programming and design choices. You've got 8 days to finish the assignment and demonstrate your final results.\r"
				+ "To be able to demonstrate your results, you will be provided with a Raspberry Pi 3 Model B starter kit. The Raspberry Pi will be pre-installed with the Raspbian Stretch Lite image (minimal Linux distribution without graphical interface), Java 8 and the Java Cryptography Extension (JCE). When the device boots an ad-hoc WiFi network will be setup. The Raspberry Pi setup guide for configuring your Mac / Windows system and communicate with the devi")
						.getBytes();
		byte[] expectedDataSecondPacket = ("ce will be provided seperately. The ad-hoc network should be used to communicate between your laptop and the Raspberry Pi’s.\n"
				+ "The application you will be making consists of two parts: a storage application (server) on the Raspberry Pi, and a desktop / laptop client which connects to the Pi.\r"
				+ "Your application will provide the following features:\r"
				+ "You should be able to upload and download files from the client to the Raspberry Pi Server.\r"
				+ "The application supports file sizes 100M or more.\r"
				+ "To keep it interesting, use UDP combined with an ARQ protocol. You are not allowed to use TCP/IP.\r"
				+ "The client should be able to ask for and list all available files on the Raspberry Pi.\r"
				+ "You should be able to pause and later resume a paused download.\r"
				+ "The server should be able to transfer several files at the same time.\r"
				+ "You should be able to prove that the file you download from the server is exactly the same as the one on the server, and the other way around (data integrity).\r"
				+ "Your laptop client should be able to").getBytes();
		byte[] expectedDataThirdPacket = (" find the Raspbery Pi on a local network without knowing its IP address.\r"
				+ "You client should be able to show statistics about download speeds, packet loss, retransmissions, etc.\r"
				+ "Bonus: Mesh network support. Download a file from a Raspberry Pi out of range of the WiFi from your laptop. Connect with an intermediate Raspberry Pi, which can see both the other Pi and your laptop. (Hint: It is possible to simulate a Raspberry Pi out of range by blacklisting a Pi from your computer)\r"
				+ "Bonus: Encrypted file transfer. Prove this by transferring a text file and creating a Wireshark dump.\n")
						.getBytes();
		Packet expectedFirstPacket = new PacketImpl(expectedHeaderFirstPacket, expectedDataFirstPacket);
		Packet expectedSecondPacket = new PacketImpl(expectedHeaderSecondPacket, expectedDataSecondPacket);
		Packet expectedThirdPacket = new PacketImpl(expectedHeaderThirdPacket, expectedDataThirdPacket);
		int expectedTotalDataSize = 2589;

		Packet firstPacket = fileDisassemblerLong.getNextPacket();

		assertEquals(expectedFirstPacket.getLength(), firstPacket.getLength());
		assertArrayEquals(expectedDataFirstPacket, firstPacket.getData());
		assertEquals(expectedSequenceNumberFirstPacket, firstPacket.getHeader().getSequenceNumber());
		assertEquals(expectedAcknowledgementNumber, firstPacket.getHeader().getAcknowledgementNumber());
		assertEquals(expectedFlagsFirstPacket, firstPacket.getHeader().getFlags());
		assertEquals(expectedTypes, firstPacket.getHeader().getTypes());
		assertEquals(expectedDownloadNumber, firstPacket.getHeader().getDownloadNumber());

		Packet secondPacket = fileDisassemblerLong.getNextPacket();

		assertEquals(expectedSecondPacket.getLength(), secondPacket.getLength());
		assertArrayEquals(expectedDataSecondPacket, secondPacket.getData());
		assertEquals(expectedSequenceNumberSecondPacket, secondPacket.getHeader().getSequenceNumber());
		assertEquals(expectedAcknowledgementNumber, secondPacket.getHeader().getAcknowledgementNumber());
		assertEquals(expectedFlagsSecondPacket, secondPacket.getHeader().getFlags());
		assertEquals(expectedTypes, secondPacket.getHeader().getTypes());
		assertEquals(expectedDownloadNumber, secondPacket.getHeader().getDownloadNumber());

		Packet thirdPacket = fileDisassemblerLong.getNextPacket();

		assertEquals(expectedThirdPacket.getLength(), thirdPacket.getLength());
		assertArrayEquals(expectedDataThirdPacket, thirdPacket.getData());
		assertEquals(expectedSequenceNumberThirdPacket, thirdPacket.getHeader().getSequenceNumber());
		assertEquals(expectedAcknowledgementNumber, thirdPacket.getHeader().getAcknowledgementNumber());
		assertEquals(expectedFlagsThirdPacket, thirdPacket.getHeader().getFlags());
		assertEquals(expectedTypes, thirdPacket.getHeader().getTypes());
		assertEquals(expectedDownloadNumber, thirdPacket.getHeader().getDownloadNumber());

		assertEquals(expectedTotalDataSize, fileDisassemblerLong.getTotalDataSize());
	}

	/**
	 * Tests that the current packet is returned.
	 */
	@Test
	public void testGetCurrentPacket() {
		Packet packet = fileDisassembler.getNextPacket();
		Packet currentPacket = fileDisassembler.getCurrentPacket();

		assertEquals(packet.getLength(), currentPacket.getLength());
		assertArrayEquals(packet.getData(), currentPacket.getData());
		assertEquals(packet.getHeader().getSequenceNumber(), currentPacket.getHeader().getSequenceNumber());
		assertEquals(packet.getHeader().getAcknowledgementNumber(),
				currentPacket.getHeader().getAcknowledgementNumber());
		assertEquals(packet.getHeader().getFlags(), currentPacket.getHeader().getFlags());
		assertEquals(packet.getHeader().getTypes(), currentPacket.getHeader().getTypes());
		assertEquals(packet.getHeader().getDownloadNumber(), currentPacket.getHeader().getDownloadNumber());
	}

	/**
	 * Tests that the correct file length is known before sending.
	 */
	@Test
	public void testGetDataSizeBeforeSending() {
		int expectedTotalDataSize = 6;

		assertEquals(expectedTotalDataSize, fileDisassembler.getDataSizeBeforeSending());
	}

	/**
	 * Tests that the checksum is correct.
	 */
	@Test
	public void testGetChecksum() {
		byte[] expectedChecksum = new byte[] { -107, 45, 44, 86, -48, 72, 89, 88, 51, 103, 71, -68, -35, -104, 89, 13 };

		fileDisassembler.getNextPacket();

		assertArrayEquals(expectedChecksum, fileDisassembler.getChecksum());
	}

	/**
	 * Tests that between the default and maximum packet size the packet size is
	 * increased with a factor 1.5.
	 */
	@Test
	public void testIncreasePacketSize() {
		int packetLength = 1024;
		int longPacketLength = 1536;
		Packet packet = fileDisassemblerLong.getNextPacket();
		fileDisassemblerLong.increasePacketSize();
		Packet longPacket = fileDisassemblerLong.getNextPacket();

		assertEquals(packetLength, packet.getLength());
		assertEquals(longPacketLength, longPacket.getLength());
	}

	/**
	 * Tests that between the default and maximum packet size the packet size is
	 * increased with a factor 1.5.
	 */
	@Test
	public void testIncreasePacketSizeOnMaximum() {
		int packetLength = 60000;
		int longPacketLength = 60000;
		fileDisassemblerReallyLong.increasePacketSize();
		fileDisassemblerReallyLong.increasePacketSize();
		fileDisassemblerReallyLong.increasePacketSize();
		fileDisassemblerReallyLong.increasePacketSize();
		fileDisassemblerReallyLong.increasePacketSize();
		fileDisassemblerReallyLong.increasePacketSize();
		fileDisassemblerReallyLong.increasePacketSize();
		fileDisassemblerReallyLong.increasePacketSize();
		fileDisassemblerReallyLong.increasePacketSize();
		fileDisassemblerReallyLong.increasePacketSize();
		fileDisassemblerReallyLong.increasePacketSize();
		fileDisassemblerReallyLong.increasePacketSize();
		Packet packet = fileDisassemblerReallyLong.getNextPacket();
		fileDisassemblerReallyLong.increasePacketSize();
		Packet longPacket = fileDisassemblerReallyLong.getNextPacket();

		assertEquals(packetLength, packet.getLength());
		assertEquals(longPacketLength, longPacket.getLength());
	}

	/**
	 * Tests that the packet is split in more packets of 64 bytes.
	 */
	@Test
	public void testDecreasePacketSize() {
		int shortPacketLength = 64;
		int expectedSequenceNumberAShortPacket = 106;

		Packet packet = fileDisassemblerLong.getNextPacket();
		fileDisassemblerLong.decreasePacketSize(packet);
		Packet shortPacket = fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		Packet aShortPacket = fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		fileDisassemblerLong.getNextPacketDecreasedSize();
		Packet normalPacket = fileDisassemblerLong.getNextPacket();
		fileDisassemblerLong.getNextPacket();
		fileDisassemblerLong.getNextPacket();
		fileDisassemblerLong.getNextPacket();
		fileDisassemblerLong.getNextPacket();
		fileDisassemblerLong.increasePacketSize();
		Packet normalPacketIncreasedLength = fileDisassemblerLong.getNextPacket();

		assertEquals(shortPacketLength, shortPacket.getLength());
		assertEquals(packet.getHeader().getSequenceNumber(), shortPacket.getHeader().getSequenceNumber());
		assertEquals(expectedSequenceNumberAShortPacket, aShortPacket.getHeader().getSequenceNumber());
		assertEquals(shortPacketLength, normalPacket.getLength());
		assertEquals(shortPacketLength * 2, normalPacketIncreasedLength.getLength());
	}
}
