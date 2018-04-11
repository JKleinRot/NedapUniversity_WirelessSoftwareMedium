package protocol.file;

import java.util.ArrayList;
import java.util.List;

import protocol.file.packet.Packet;

public class FileImpl implements File {

	/** The packets in the file */
	private List<Packet> packets;
	
	/** The data size */
	private int dataSize;

	/**
	 * Creates a new FileImpl.
	 */
	public FileImpl() {
		packets = new ArrayList<>();
		dataSize = 0;
	}

	@Override
	public void addPacket(Packet packet) {
		packets.add(packet);
		dataSize = dataSize + packet.getLength();
	}

	@Override
	public List<Packet> getPackets() {
		return packets;
	}
	
	@Override 
	public int getDataSize() {
		return dataSize;
	}
}
