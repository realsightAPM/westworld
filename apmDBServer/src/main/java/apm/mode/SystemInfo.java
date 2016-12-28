package apm.mode;

import lombok.Data;

@Data
public class SystemInfo {
	private int usedMemory;
	private float cpu;
	private int httpTime;//响应时间
	private int sessionCount;//当前回话个数
	//private int httpCount;//http链接个数
	private int sessionTime;//平均回话时间
	private int threadCount;//线程个数
}
