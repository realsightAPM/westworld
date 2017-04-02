package com.realsight.westworld.tsp.test;

import com.realsight.westworld.tsp.api.StockStrategyAPI;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.util.data.StockData;
import com.realsight.westworld.tsp.lib.util.plot.Plot;

/**
 * @author Sun Muxin
 * 
 */ 
public class StockStrategyExample {

	public void main() throws Exception {
		StockData sd = new StockData();
		String id = "601588";
		DoubleSeries ss = sd.history_data("000001");
		DoubleSeries sss = sd.history_data(id);
		ss = ss.mul(sss.max()/ss.max());
		MultipleDoubleSeries mseries = new MultipleDoubleSeries(id, sss, ss);
		Plot.plot(id, sss, ss);
		StockStrategyAPI ssa = new StockStrategyAPI(mseries);
		ssa.run(mseries);
	}
	
	public static void main(String[] args) throws Exception {
		new StockStrategyExample().main();
	}
}
