package apm.Process;

import java.sql.SQLException;

import apm.db.DBServer;
import apm.http.HttpData;
import apm.mode.SystemInfo;

class SystemInfoProcesser implements Processer{
	private static SystemInfoParse systemInfoParse = new SystemInfoParse();
	@Override
	public void process(HttpData data) {
		SystemInfo systemInfo = systemInfoParse.parse(data.message);
		try {
			DBServer.saveSystemInfo(systemInfo);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
