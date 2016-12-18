package apm.http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

import apm.cache.Cache;
import apm.globalinfo.Part;
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
					Gson gson = new Gson();
					HeapHistory heapHistory = gson.fromJson(data.message, HeapHistory.class);
					Cache.add(data.part, heapHistory);
					System.out.println("add heapHistory");
				}
			}
		}
	}
}
