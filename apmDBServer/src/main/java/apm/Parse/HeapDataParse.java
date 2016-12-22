package Parse;

import com.google.gson.Gson;

import apm.mode.HeapHistory;

public class HeapDataParse implements Parse {

	class JsonHeapData{
		private HeapHistory heapHisto;
	}
	public   HeapHistory parse(String json) {
		// TODO Auto-generated method stub
			Gson gson = new Gson();
		//	System.out.println(data.message);
			
			JsonHeapData data = gson.fromJson(json, JsonHeapData.class);
			//Cache.add(data.part, heapHistory);
			HeapHistory  heapHistory =data.heapHisto;
			System.out.println("  $$$$$$$$$ time  "+heapHistory.getTime());
		
		return heapHistory;
	}
	
	
}
