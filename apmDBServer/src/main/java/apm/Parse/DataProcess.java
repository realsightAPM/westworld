package apm.Parse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import apm.db.DBServer;
import apm.globalinfo.Part;
import apm.http.HttpData;
import apm.mode.HeapHistory;

public class DataProcess {
	private final ExecutorService threadPool = Executors.newFixedThreadPool(5);
	
	
	
	public void process(HttpData data){
		ProcessWork work = new ProcessWork(data);
		threadPool.execute(work);
	}
	
	class ProcessWork implements Runnable{
		
		private HttpData data;
		
		ProcessWork(HttpData data){
			this.data = data;
		}
		
		public void run() {
			Part part = Part.PartofString(data.part);
			switch(part){
				case HEAPHISTORY:{
					HeapDataParse parse = new HeapDataParse();
					HeapHistory heapHistory = parse.parse(data.message);
					DBServer.saveHeapData(heapHistory);
					System.out.println("add heapHistory");
				}
			}
		}
	}
}
