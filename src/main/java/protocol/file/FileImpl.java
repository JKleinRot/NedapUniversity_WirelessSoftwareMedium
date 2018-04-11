package protocol.file;

import java.util.ArrayList;
import java.util.List;

import protocol.file.packet.Packet;

public class FileImpl implements File {

	/** The packets in the file */
	private List<Packet> packets;

	/**
	 * Creates a new FileImpl.
	 */
	public FileImpl() {
		packets = new ArrayList<>();
	}

	@Override
	public void addPacket(Packet packet) {
		packets.add(packet);
	}

	@Override
	public List<Packet> getPackets() {
		return packets;
	}
}
