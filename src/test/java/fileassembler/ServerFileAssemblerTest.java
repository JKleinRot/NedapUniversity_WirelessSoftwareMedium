package fileassembler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URISyntaxException;

import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.uploader.ClientUploader;
import fileassembler.ServerFileAssembler;
import fileassembler.ServerFileAssemblerImpl;
import filedisassembler.ClientFileDisassembler;
import filedisassembler.ClientFileDisassemblerImpl;
import packet.Packet;
import packet.PacketImpl;
import packet.header.Flags;
import packet.header.Header;
import packet.header.HeaderImpl;
import packet.header.Types;
import server.downloader.ServerDownloader;

/**
 * Test program for FileAssembler.
 * 
 * @author janine.kleinrot
 */
public class ServerFileAssemblerTest {

	/** The file assembler for the short file */
	private ServerFileAssembler fileAssembler;

	/** The file assembler for the long file */
	private ServerFileAssembler fileAssemblerLong;

	/** The new file name of the short file */
	private String newFileName;

	/** The new file name of the long file */
	private String newFileNameLong;

	/** The file directory */
	private String fileDirectory;

	/** The download number */
	private int downloadNumber;

	/** The mocks */
	private EasyMockSupport mocks;

	/** The data uploader */
	private ClientUploader dataUploader;

	/** The data downloader */
	private ServerDownloader dataDownloader;

	/** The old file name of the short file */
	private String oldFileName;

	/** The old file name of the long file */
	private String oldFileNameLong;

	/** The file disassembler for the short file */
	private ClientFileDisassembler fileDisassembler;

	/** The file disassembler for the long file */
	private ClientFileDisassembler fileDisassemblerLong;

	/** The packet */
	private Packet packet;

	/** The last packet */
	private Packet lastPacket;

	/** The first packet */
	private Packet firstPacket;

	/** The second packet */
	private Packet secondPacket;

	/** The third packet */
	private Packet thirdPacket;

	/** The last packet for the long file */
	private Packet lastPacketLong;

	@BeforeEach
	public void setup() throws URISyntaxException {
		mocks = new EasyMockSupport();
		dataUploader = mocks.createMock(ClientUploader.class);
		dataDownloader = mocks.createMock(ServerDownloader.class);
		newFileName = "TestResult.txt";
		newFileNameLong = "TestLongResult.txt";
		downloadNumber = 1;
		oldFileName = "Test.txt";
		oldFileNameLong = "TestLong.txt";
		File file = new File(oldFileName);
		oldFileName = file.getAbsolutePath();
		File absoluteFile = file.getAbsoluteFile();
		fileDirectory = absoluteFile.getParent() + "/";
		file = new File(oldFileNameLong);
		oldFileNameLong = file.getAbsolutePath();
		fileDisassembler = new ClientFileDisassemblerImpl(oldFileName, dataUploader, downloadNumber);
		fileDisassemblerLong = new ClientFileDisassemblerImpl(oldFileNameLong, dataUploader, downloadNumber);
		packet = fileDisassembler.getNextPacket();
		firstPacket = fileDisassemblerLong.getNextPacket();
		secondPacket = fileDisassemblerLong.getNextPacket();
		thirdPacket = fileDisassemblerLong.getNextPacket();
		Header header = new HeaderImpl(20, 0, Flags.DOWNLOAD_DATAINTEGRITY, Types.DATAINTEGRITY, downloadNumber);
		byte[] data = new byte[] { -107, 45, 44, 86, -48, 72, 89, 88, 51, 103, 71, -68, -35, -104, 89, 13 };
		lastPacket = new PacketImpl(header, data);
		header = new HeaderImpl(20, 0, Flags.DOWNLOAD_DATAINTEGRITY, Types.DATAINTEGRITY, downloadNumber);
		byte[] dataLong = new byte[] { 98, -49, -19, 10, 111, 69, 11, 30, 39, 90, -76, -77, -9, 70, -126, -66 };
		lastPacketLong = new PacketImpl(header, dataLong);
		fileAssembler = new ServerFileAssemblerImpl(newFileName, fileDirectory, downloadNumber, dataDownloader);
		fileAssemblerLong = new ServerFileAssemblerImpl(newFileNameLong, fileDirectory, downloadNumber, dataDownloader);
	}

	/**
	 * Tests that the file consisting of one packet is correctly assembled and
	 * saved.
	 */
	@Test
	public void testFileAssemblyOnePacketFile() {
		fileAssembler.addPacket(packet);
		fileAssembler.addPacket(lastPacket);

		assertTrue(fileAssembler.isFileCorrect());
	}

	/**
	 * Tests that the file consisting of multiple packets is correctly assembled and
	 * saved. Packets arrived in correct order.
	 */
	@Test
	public void testFileAssemblyMultiplePacketFile() {
		fileAssemblerLong.addPacket(firstPacket);
		fileAssemblerLong.addPacket(secondPacket);
		fileAssemblerLong.addPacket(thirdPacket);
		fileAssemblerLong.addPacket(lastPacketLong);

		assertTrue(fileAssemblerLong.isFileCorrect());
	}
}
