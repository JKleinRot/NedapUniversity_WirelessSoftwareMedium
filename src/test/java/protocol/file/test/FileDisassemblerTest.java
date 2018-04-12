package protocol.file.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import protocol.file.File;
import protocol.file.FileDisassembler;
import protocol.file.FileDisassemblerImpl;
import protocol.file.FileImpl;
import protocol.file.packet.Packet;
import protocol.file.packet.PacketImpl;
import protocol.file.packet.header.Header;
import protocol.file.packet.header.HeaderImpl;
import protocol.file.packet.header.parts.Flags;
import protocol.file.packet.header.parts.Types;
import uploader.DataUploader;

/**
 * Test program for FileDisassembler.
 * 
 * @author janine.kleinrot
 */
public class FileDisassemblerTest {

	/** The file disassembler */
	private FileDisassembler fileDisassembler;

	/** The file disassembler for a longer file */
	private FileDisassemblerImpl fileDisassemblerLong;

	/** The mocks */
	private EasyMockSupport mocks;

	/** The data uploader */
	private DataUploader dataUploader;

	/** The download number */
	private int downloadNumber;

	@BeforeEach
	public void setup() {
		mocks = new EasyMockSupport();
		dataUploader = mocks.createMock(DataUploader.class);
		downloadNumber = 1;
		fileDisassembler = new FileDisassemblerImpl("Test.txt", dataUploader, downloadNumber);
		fileDisassemblerLong = new FileDisassemblerImpl("TestLong.txt", dataUploader, downloadNumber);
	}

	/**
	 * Tests that a file is constructed with packets that contain headers and data.
	 * The file can fit in one packet.
	 */
	@Test
	public void testCreateFileWithPacketsFromFileOnePacket() {
		int expectedSequenceNumber = 100;
		int expectedAcknowledgementNumber = 0;
		Flags expectedFlags = Flags.UPLOAD_LAST;
		Types expectedTypes = Types.DATA;
		int expectedDownloadNumber = downloadNumber;
		Header expectedHeader = new HeaderImpl(expectedSequenceNumber, expectedAcknowledgementNumber, expectedFlags,
				expectedTypes, expectedDownloadNumber);
		byte[] expectedData = "Hello!".getBytes();
		Packet expectedPacket = new PacketImpl(expectedHeader, expectedData);
		File expectedFile = new FileImpl();
		expectedFile.addPacket(expectedPacket);

		File file = fileDisassembler.createFileWithPacketsFromFile();

		assertEquals(expectedFile.getPackets().size(), file.getPackets().size());
		assertArrayEquals(expectedData, file.getPackets().get(0).getData());
		assertEquals(expectedSequenceNumber, file.getPackets().get(0).getHeader().getSequenceNumber());
		assertEquals(expectedAcknowledgementNumber, file.getPackets().get(0).getHeader().getAcknowledgementNumber());
		assertEquals(expectedFlags, file.getPackets().get(0).getHeader().getFlags());
		assertEquals(expectedTypes, file.getPackets().get(0).getHeader().getTypes());
		assertEquals(expectedDownloadNumber, file.getPackets().get(0).getHeader().getDownloadNumber());
	}

	/**
	 * Tests that a file is constructed with packets that contain headers and data.
	 * The file is distributed over multiple packets
	 */
	@Test
	public void testCreateFileWithPacketsFromFileMultiplePackets() {
		int expectedSequenceNumberFirstPacket = 100;
		int expectedSequenceNumberSecondPacket = 110;
		int expectedSequenceNumberThirdPacket = 120;
		int expectedAcknowledgementNumber = 0;
		Flags expectedFlagsFirstPacket = Flags.UPLOAD_MORETOCOME;
		Flags expectedFlagsSecondPacket = Flags.UPLOAD_MORETOCOME;
		Flags expectedFlagsThirdPacket = Flags.UPLOAD_LAST;
		Types expectedTypes = Types.DATA;
		int expectedDownloadNumber = downloadNumber;
		Header expectedHeaderFirstPacket = new HeaderImpl(expectedSequenceNumberFirstPacket,
				expectedAcknowledgementNumber, expectedFlagsFirstPacket, expectedTypes, expectedDownloadNumber);
		Header expectedHeaderSecondPacket = new HeaderImpl(expectedSequenceNumberSecondPacket,
				expectedAcknowledgementNumber, expectedFlagsSecondPacket, expectedTypes, expectedDownloadNumber);
		Header expectedHeaderThirdPacket = new HeaderImpl(expectedSequenceNumberThirdPacket,
				expectedAcknowledgementNumber, expectedFlagsThirdPacket, expectedTypes, expectedDownloadNumber);
		byte[] expectedDataFirstPacket = ("During the module you learned about networks, layers and different protocols. Where TCP guarantees reliable transfer of information, UDP does not. For this assignment you will create a wireless storage medium (think of it like a simple NAS), where you should achieve reliable file transfer using the UDP protocol.\n" + 
				"The assignment should be performed alone. Discussion about the assignment is allowed, but you should always be able to defend your programming and design choices. You've got 8 days to finish the assignment and demonstrate your final results.\n" + 
				"To be able to demonstrate your results, you will be provided with a Raspberry Pi 3 Model B starter kit. The Raspberry Pi will be pre-installed with the Raspbian Stretch Lite image (minimal Linux distribution without graphical interface), Java 8 and the Java Cryptography Extension (JCE). When the device boots an ad-hoc WiFi network will be setup. The Raspberry Pi setup guide for configuring your Mac / Windows system and communicate with the devi")
						.getBytes();
		byte[] expectedDataSecondPacket = ("ce will be provided seperately. The ad-hoc network should be used to communicate between your laptop and the Raspberry Pi’s.\n" + 
				"The application you will be making consists of two parts: a storage application (server) on the Raspberry Pi, and a desktop / laptop client which connects to the Pi.\n" + 
				"Your application will provide the following features:\n" + 
				"You should be able to upload and download files from the client to the Raspberry Pi Server.\n" + 
				"The application supports file sizes 100M or more.\n" + 
				"To keep it interesting, use UDP combined with an ARQ protocol. You are not allowed to use TCP/IP.\n" + 
				"The client should be able to ask for and list all available files on the Raspberry Pi.\n" + 
				"You should be able to pause and later resume a paused download.\n" + 
				"The server should be able to transfer several files at the same time.\n" + 
				"You should be able to prove that the file you download from the server is exactly the same as the one on the server, and the other way around (data integrity).\n" + 
				"Your laptop client should be able to").getBytes();
		byte[] expectedDataThirdPacket = (" find the Raspbery Pi on a local network without knowing its IP address.\n" + 
				"You client should be able to show statistics about download speeds, packet loss, retransmissions, etc.\n" + 
				"Bonus: Mesh network support. Download a file from a Raspberry Pi out of range of the WiFi from your laptop. Connect with an intermediate Raspberry Pi, which can see both the other Pi and your laptop. (Hint: It is possible to simulate a Raspberry Pi out of range by blacklisting a Pi from your computer)\n" + 
				"Bonus: Encrypted file transfer. Prove this by transferring a text file and creating a Wireshark dump.").getBytes();
		Packet expectedFirstPacket = new PacketImpl(expectedHeaderFirstPacket, expectedDataFirstPacket);
		Packet expectedSecondPacket = new PacketImpl(expectedHeaderSecondPacket, expectedDataSecondPacket);
		Packet expectedThirdPacket = new PacketImpl(expectedHeaderThirdPacket, expectedDataThirdPacket);
		File expectedFile = new FileImpl();
		expectedFile.addPacket(expectedFirstPacket);
		expectedFile.addPacket(expectedSecondPacket);
		expectedFile.addPacket(expectedThirdPacket);

		File file = fileDisassemblerLong.createFileWithPacketsFromFile();

		assertEquals(expectedFile.getPackets().size(), file.getPackets().size());
		assertArrayEquals(expectedFile.getPackets().get(0).getData(), file.getPackets().get(0).getData());
		assertArrayEquals(expectedFile.getPackets().get(1).getData(), file.getPackets().get(1).getData());
		assertArrayEquals(expectedFile.getPackets().get(2).getData(), file.getPackets().get(2).getData());
		assertEquals(expectedSequenceNumberFirstPacket, file.getPackets().get(0).getHeader().getSequenceNumber());
		assertEquals(expectedSequenceNumberSecondPacket, file.getPackets().get(1).getHeader().getSequenceNumber());
		assertEquals(expectedSequenceNumberThirdPacket, file.getPackets().get(2).getHeader().getSequenceNumber());
		assertEquals(expectedAcknowledgementNumber, file.getPackets().get(0).getHeader().getAcknowledgementNumber());
		assertEquals(expectedAcknowledgementNumber, file.getPackets().get(1).getHeader().getAcknowledgementNumber());
		assertEquals(expectedAcknowledgementNumber, file.getPackets().get(2).getHeader().getAcknowledgementNumber());
		assertEquals(expectedFlagsFirstPacket, file.getPackets().get(0).getHeader().getFlags());
		assertEquals(expectedFlagsSecondPacket, file.getPackets().get(1).getHeader().getFlags());
		assertEquals(expectedFlagsThirdPacket, file.getPackets().get(2).getHeader().getFlags());
		assertEquals(expectedTypes, file.getPackets().get(0).getHeader().getTypes());
		assertEquals(expectedTypes, file.getPackets().get(1).getHeader().getTypes());
		assertEquals(expectedTypes, file.getPackets().get(2).getHeader().getTypes());
		assertEquals(expectedDownloadNumber, file.getPackets().get(0).getHeader().getDownloadNumber());
		assertEquals(expectedDownloadNumber, file.getPackets().get(1).getHeader().getDownloadNumber());
		assertEquals(expectedDownloadNumber, file.getPackets().get(2).getHeader().getDownloadNumber());
	}

}
