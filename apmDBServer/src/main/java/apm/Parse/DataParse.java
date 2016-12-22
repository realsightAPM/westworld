package Parse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

import apm.cache.Cache;
import apm.db.DBServer;
import apm.globalinfo.Part;
import apm.http.HttpData;
import apm.mode.HeapHistory;

public class DataParse {
	private final ExecutorService threadPool = Executors.newFixedThreadPool(5);
	
	
	
	public void parse(HttpData data){
		ParseWork work = new ParseWork(data);
		threadPool.execute(work);
	}
	
	class ParseWork implements Runnable{
		
		private HttpData data;
		
		ParseWork(HttpData data){
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
