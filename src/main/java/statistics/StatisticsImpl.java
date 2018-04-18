package statistics;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class StatisticsImpl implements Statistics {

	/** The retransmission count */
	private int retransmissionCount;

	/** The start time */
	private LocalDateTime startTime;

	/** The end time */
	private LocalDateTime endTime;

	/** The amount of data sent */
	private long bytesSent;

	/** The total amount of bytes in the file */
	private long totalBytes;

	/**
	 * -----Constructor-----
	 * 
	 * Creates a statistics monitor.
	 * 
	 * @param fileName
	 *            The file name
	 */
	public StatisticsImpl(String fileName) {
		File file = new File(fileName);
		totalBytes = file.length();
		retransmissionCount = 0;
		bytesSent = 0;
	}

	@Override
	public void updateRetransmissionCount(int currentRetransmissionCount) {
		retransmissionCount = retransmissionCount + currentRetransmissionCount;
	}

	@Override
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	@Override
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	@Override
	public void updatePartSent(int lastPacketSize) {
		bytesSent = bytesSent + lastPacketSize;
	}

	@Override
	public String getStatistics() {
		String statistics;
		if (endTime == null) {
			long duration = Duration.between(startTime, LocalDateTime.now()).getSeconds();
			long speed;
			if (duration != 0) {
				speed = bytesSent / duration;
			} else {
				speed = bytesSent;
			}
			long percentageComplete = (bytesSent * 100L) / totalBytes;
			if (duration != 0) {
				statistics = bytesSent + " of " + totalBytes + " bytes are sent in " + duration + " seconds\n"
						+ "The average upload speed is " + speed + " bytes/second\n" + retransmissionCount
						+ " retransmissions occurred\n" + "The progress is " + percentageComplete + " %\n";
			} else {
				statistics = bytesSent + " of " + totalBytes + " bytes are sent in less than a second\n"
						+ "The average upload speed is higher than " + speed + " bytes/seconds\n" + retransmissionCount
						+ " retransmissions occurred\n" + "The progress is " + percentageComplete + " %\n";
			}
		} else {
			long duration = Duration.between(startTime, endTime).getSeconds();
			long speed;
			if (duration != 0) {
				speed = bytesSent / duration;
			} else {
				speed = bytesSent;
			}
			int percentageComplete = 100;
			if (duration != 0) {
				statistics = bytesSent + " of " + totalBytes + " bytes are sent in " + duration + " seconds\n"
						+ "The average upload speed is " + speed + " bytes/second\n" + retransmissionCount
						+ " retransmissions occurred\n" + "The progress is " + percentageComplete + " %\n";
			} else {
				statistics = bytesSent + " of " + totalBytes + " bytes are sent in less than a second\n"
						+ "The average upload speed is higher than " + speed + " bytes/seconds\n" + retransmissionCount
						+ " retransmissions occurred\n" + "The progress is " + percentageComplete + " %\n";
			}
		}
		return statistics;
	}

	@Override
	public void setTotalDataSize(byte[] packet) {
		String data = new String(packet);
		String[] words = data.split(" ");
		totalBytes = Integer.parseInt(words[1]);
	}
}
