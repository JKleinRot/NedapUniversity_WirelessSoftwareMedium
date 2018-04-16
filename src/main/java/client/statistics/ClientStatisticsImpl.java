package client.statistics;

import java.time.LocalDateTime;

public class ClientStatisticsImpl implements ClientStatistics {

	/** The retransmission count */
	private int retransmissionCount;
	
	/** The start time */
	private LocalDateTime startTime;
	
	/** The end time */
	private LocalDateTime endTime;
	
	public ClientStatisticsImpl() {
		retransmissionCount = 0;
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
	
}
