package apm.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import apm.globalinfo.Part;
import apm.http.HttpData;

public class DataProcess {
	private final ExecutorService threadPool = Executors.newFixedThreadPool(5);
	
	private HeapDataProcesser heapDataProcesser = new HeapDataProcesser();
	
	private SystemInfoProcesser systemInfoProcess = new SystemInfoProcesser();
	
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
					heapDataProcesser.process(data);
				}break;
				case SYSTEMINFO:{
					systemInfoProcess.process(data);
				}break;
			}
		}
	}
}
