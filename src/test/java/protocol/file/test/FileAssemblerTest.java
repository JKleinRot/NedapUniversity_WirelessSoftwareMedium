package protocol.file.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import protocol.file.File;
import protocol.file.FileAssembler;
import protocol.file.FileAssemblerImpl;
import protocol.file.FileDisassembler;
import protocol.file.FileDisassemblerImpl;
import protocol.file.packet.Packet;
import protocol.file.packet.PacketImpl;
import protocol.file.packet.header.Header;
import protocol.file.packet.header.HeaderImpl;
import protocol.file.packet.header.parts.Flags;
import protocol.file.packet.header.parts.Types;
import uploader.DataUploader;

/**
 * Test program for FileAssembler.
 * 
 * @author janine.kleinrot
 */
public class FileAssemblerTest {

	/** The file assembler for the short file */
	private FileAssembler fileAssembler;
	
	/** The file assembler for the long file */
	private FileAssembler fileAssemblerLong;
	
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
	private DataUploader dataUploader;
	
	/** The old file name of the short file*/
	private String oldFileName;
	
	/** The old file name of the long file*/
	private String oldFileNameLong;
	
	/** The file disassembler for the short file*/
	private FileDisassembler fileDisassembler;
	
	/** The file disassembler for the long file*/
	private FileDisassembler fileDisassemblerLong;
	
	/** The short file with packets */
	private File file;
	
	/** The long file with packets */
	private File fileLong;
	
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
	
	/** The data uploader */
	private DataUploader readDataUploader;
	
	@BeforeEach
	public void setup() {
		mocks = new EasyMockSupport();
		dataUploader = mocks.createMock(DataUploader.class);		
		newFileName = "TestResult.txt";
		fileDirectory = "/Users/janine.kleinrot/Documents/NedapUniversity/Module1_SoftwareSystems/Software/Eclipse_Workspace/NedapUniversity_WirelessStorageMedium/";
		downloadNumber = 1;
		oldFileName = "Test.txt";
		oldFileNameLong = "TestLong.txt";
		fileDisassembler = new FileDisassemblerImpl(oldFileName, dataUploader, downloadNumber);
		fileDisassemblerLong = new FileDisassemblerImpl(oldFileNameLong, dataUploader, downloadNumber);
		file = fileDisassembler.createFileWithPacketsFromFile();
		fileLong = fileDisassemblerLong.createFileWithPacketsFromFile();
		packet = file.getPackets().get(0);
		Header header = new HeaderImpl(20, 0, Flags.UPLOAD_DATAINTEGRITY, Types.DATAINTEGRITY, downloadNumber);
		byte[] data = ("DataSize " + 6).getBytes();
		lastPacket = new PacketImpl(header, data);
		firstPacket = fileLong.getPackets().get(0);
		secondPacket = fileLong.getPackets().get(1);
		thirdPacket = fileLong.getPackets().get(2);
		header = new HeaderImpl(20, 0, Flags.UPLOAD_DATAINTEGRITY, Types.DATAINTEGRITY, downloadNumber);
		byte[] dataLong = ("DataSize " + 2571).getBytes();
		lastPacketLong = new PacketImpl(header, dataLong);
		fileAssembler = new FileAssemblerImpl(newFileName, fileDirectory, downloadNumber);
		fileAssemblerLong = new FileAssemblerImpl(newFileNameLong, fileDirectory, downloadNumber);
	}
	
	@Test
	public void testFileAssemblyOnePacketFile() {
		fileAssembler.addPacket(packet);
		fileAssembler.addPacket(lastPacket);
		
		byte[] content = null;
		try {
			FileReader fileReader = new FileReader(newFileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuilder stringBuilder = new StringBuilder();
			String line = bufferedReader.readLine();
			while (line != null) {
				stringBuilder.append(line);
				line = bufferedReader.readLine();
			}
			content = stringBuilder.toString().getBytes();
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			// ?
		} catch (IOException e) {
			// ?
		}
				
		assertArrayEquals(packet.getData(), content);
		
	}
}