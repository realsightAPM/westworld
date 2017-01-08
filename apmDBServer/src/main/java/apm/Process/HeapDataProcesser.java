package apm.Process;

import apm.db.DBServer;
import apm.http.HttpData;
import apm.mode.HeapHistory;

public class HeapDataProcesser implements Processer{
	private static HeapDataParse parse = new HeapDataParse();
	
	public void process(HttpData data) {
		HeapHistory heapHistory = parse.parse(data.message);
		DBServer.saveHeapData(heapHistory);
		System.out.println("add heapHistory");
	}

}
