package apm.Process;

import java.math.BigDecimal;
import java.sql.Timestamp;

import apm.mode.SystemInfo;

public class SystemInfoParse implements Parse{

	public SystemInfo parse(String message) {
		String[] values = message.split(",");
		SystemInfo systemInfo = new SystemInfo();
		BigDecimal bigDecimal = new BigDecimal(values[0]);
		systemInfo.setNowTime(new Timestamp(System.currentTimeMillis()));
		systemInfo.setUsedMemory(bigDecimal.intValue());
		systemInfo.setCpu(Float.valueOf(values[1]));
		systemInfo.setHttpTime(Float.valueOf(values[2]).intValue());
		systemInfo.setSessionCount(Float.valueOf(values[3]).intValue());
		systemInfo.setThreadCount(Float.valueOf(values[4]).intValue());
		
		return systemInfo;
	}

}