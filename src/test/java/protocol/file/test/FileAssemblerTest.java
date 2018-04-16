package protocol.file.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
public class FileAssemblerTest {

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

	/** The file input stream */
	private InputStream inputStream;

	/** The packet */
	private Packet packet;

	/** The last packet */
	private Packet lastPacket;

	/** The total data size of the short file */
	private int totalDataSize;

	/** The first packet */
	private Packet firstPacket;

	/** The second packet */
	private Packet secondPacket;

	/** The third packet */
	private Packet thirdPacket;

	/** The last packet for the long file */
	private Packet lastPacketLong;

	/** The total data size of the long file */
	private int totalDataSizeLong;

	@BeforeEach
	public void setup() {
		mocks = new EasyMockSupport();
		dataUploader = mocks.createMock(ClientUploader.class);
		dataDownloader = mocks.createMock(ServerDownloader.class);
		newFileName = "TestResult.txt";
		newFileNameLong = "TestLongResult.txt";
		fileDirectory = "/Users/janine.kleinrot/Documents/NedapUniversity/Module1_SoftwareSystems/Software/Eclipse_Workspace/NedapUniversity_WirelessStorageMedium/";
		downloadNumber = 1;
		oldFileName = "Test.txt";
		oldFileNameLong = "TestLong.txt";
		fileDisassembler = new ClientFileDisassemblerImpl(oldFileName, dataUploader, downloadNumber);
		fileDisassemblerLong = new ClientFileDisassemblerImpl(oldFileNameLong, dataUploader, downloadNumber);
		packet = fileDisassembler.getNextPacket();
		totalDataSize = fileDisassembler.getTotalDataSize();
		firstPacket = fileDisassemblerLong.getNextPacket();
		secondPacket = fileDisassemblerLong.getNextPacket();
		thirdPacket = fileDisassemblerLong.getNextPacket();
		totalDataSizeLong = fileDisassemblerLong.getTotalDataSize();
		Header header = new HeaderImpl(20, 0, Flags.DOWNLOAD_DATAINTEGRITY, Types.DATAINTEGRITY, downloadNumber);
		byte[] data = ("DataSize " + 6).getBytes();
		lastPacket = new PacketImpl(header, data);
		header = new HeaderImpl(20, 0, Flags.DOWNLOAD_DATAINTEGRITY, Types.DATAINTEGRITY, downloadNumber);
		byte[] dataLong = ("DataSize " + 2588).getBytes();
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

		byte[] dataBuffer = new byte[totalDataSize];
		int readDataSize = 0;
		try {
			inputStream = new FileInputStream(newFileName);
			readDataSize = inputStream.read(dataBuffer);
		} catch (IOException e) {
			System.out.println("ERROR: File could not be read");
		}
		byte[] data;
		if (readDataSize != -1) {
			data = Arrays.copyOfRange(dataBuffer, 0, readDataSize);
		} else {
			data = new byte[0];
		}

		assertArrayEquals(packet.getData(), data);
	}

	/**
	 * Tests that the file consisting of multiple packets is correctly assembled and
	 * saved. Packets arrived in correct order.
	 */
	@Test
	public void testFileAssemblyMultiplePacketFile() {
		fileAssemblerLong.addPacket(firstPacket);
		fileAssemblerLong.addPacket(secondPacket);
		fileAssemblerLong.addPacket(secondPacket);
		fileAssemblerLong.addPacket(thirdPacket);
		fileAssemblerLong.addPacket(lastPacketLong);

		byte[] dataBuffer = new byte[totalDataSizeLong];
		int readDataSize = 0;
		try {
			inputStream = new FileInputStream(newFileNameLong);
			readDataSize = inputStream.read(dataBuffer);
		} catch (IOException e) {
			System.out.println("ERROR: File could not be read");
		}
		byte[] data;
		if (readDataSize != -1) {
			data = Arrays.copyOfRange(dataBuffer, 0, readDataSize);
		} else {
			data = new byte[0];
		}
		assertEquals(firstPacket.getData().length + secondPacket.getData().length + thirdPacket.getData().length,
				data.length);
	}
}
