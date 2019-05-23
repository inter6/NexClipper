package com.nexcloud.docker.container.log.domain;

public class ContainerLog {
	private Long time;
	private String stream;
	private String log;

	public ContainerLog(Long time, String stream, String log) {
		this.time = time;
		this.stream = stream;
		this.log = log;
	}

	public Long getTime() {
		return time;
	}

	public String getStream() {
		return stream;
	}

	public String getLog() {
		return log;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("***** Container Log *****\n");
		sb.append("Time   = "+getTime()+"\n");
		sb.append("Stream = "+getStream()+"\n");
		sb.append("Log    = "+getLog()+"\n");
		return sb.toString();
	}
}
