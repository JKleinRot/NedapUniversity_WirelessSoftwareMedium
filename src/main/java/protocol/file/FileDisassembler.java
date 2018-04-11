package protocol.file;

/**
 * Disassembles file into packets with headers ready to send.
 * 
 * @author janine.kleinrot
 */
public interface FileDisassembler {

	/** 
	 * Creates ready to send packets of the file.
	 */
	public File createFileWithPacketsFromFile();
}
