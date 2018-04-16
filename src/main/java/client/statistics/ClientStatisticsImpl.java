package client.statistics;

import java.io.File;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ClientStatisticsImpl implements ClientStatistics {

	/** The retransmission count */
	private int retransmissionCount;

	/** The start time */
	private LocalDateTime startTime;

	/** The end time */
	private LocalDateTime endTime;

	/** The amount of data send */
	private int bytesSend;

	/** The total amount of bytes in the file */
	private int totalBytes;

	public ClientStatisticsImpl(String fileName) {
		File file = new File(fileName);
		totalBytes = (int) file.length();
		retransmissionCount = 0;
		bytesSend = 0;
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
	public void updatePartSend(int lastPacketSize) {
		bytesSend = bytesSend + lastPacketSize;
	}

	@Override
	public String getStatistics() {
		String statistics;
		if (endTime == null) {
			int duration = (int) Duration.between(startTime, endTime).getSeconds();
			int speed;
			if (duration != 0) { 
				speed = bytesSend / duration;
			} else {
				speed = bytesSend;
			}
			int percentageComplete = (bytesSend / totalBytes) * 100;
			if (duration != 0) {
				statistics = bytesSend + " of " + totalBytes + " bytes are send in " + duration + "\n"
						+ "The average upload speed is " + speed + " bytes/second\n" + retransmissionCount + " retransmissions occurred\n" + "The progress is " + percentageComplete + " %\n";
			} else {
				statistics = bytesSend + " of " + totalBytes + " bytes are send in less than a second\n"
						+ "The average upload speed is higher than " + speed + " bytes/seconds\n" + retransmissionCount + " retransmissions occurred\n"+ "The progress is " + percentageComplete + " %\n";
			}
		} else {
			int duration = (int) Duration.between(startTime, endTime).getSeconds();
			int speed;
			if (duration != 0) { 
				speed = bytesSend / duration;
			} else {
				speed = bytesSend;
			}
			int percentageComplete = 100;
			if (duration != 0) {
				statistics = bytesSend + " of " + totalBytes + " bytes are send in " + duration + "\n"
						+ "The average upload speed is " + speed + " bytes/second\n" + retransmissionCount + " retransmissions occurred\n" + "The progress is " + percentageComplete + " %\n";
			} else {
				statistics = bytesSend + " of " + totalBytes + " bytes are send in less than a second\n"
						+ "The average upload speed is higher than " + speed + " bytes/seconds\n" + retransmissionCount + " retransmissions occurred\n" + "The progress is " + percentageComplete + " %\n";
			}
		}
		return statistics;
	}

}
