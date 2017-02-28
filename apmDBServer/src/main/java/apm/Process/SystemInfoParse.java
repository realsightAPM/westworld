package apm.Process;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;

import apm.mode.SystemInfo;
import apm.webstress.Stress;

public class SystemInfoParse implements Parse{

	public SystemInfo parse(String message) {
		String[] values = message.split(",");
		SystemInfo systemInfo = new SystemInfo();
		BigDecimal bigDecimal = new BigDecimal(values[0]);
		systemInfo.setNowTime(new Timestamp(System.currentTimeMillis()));
		systemInfo.setUsedMemory(bigDecimal.intValue());
		systemInfo.setClients(Stress.queue.size());
		Field[] fields = SystemInfo.class.getDeclaredFields();
		for(int i=3;i<(fields.length-1);i++){
			fields[i].setAccessible(true);
			try {
				fields[i].set(systemInfo, Float.valueOf(values[i-2]));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return systemInfo;
	}

}
