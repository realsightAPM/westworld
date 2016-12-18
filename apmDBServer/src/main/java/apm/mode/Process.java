package apm.mode;

import lombok.Data;

/*
 * 进程
 */
@Data
public class Process {
	private String user;
	private String pid;
	private double cpuPercentage;
	private double memPercentage;
	private int vsz;
	private int rss;
	private String tty;
	private String stat;
	private String start;
	private String cpuTime;
	private String command;
}
