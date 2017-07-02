package com.realsight.westworld.tsp.test;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.realsight.westworld.tsp.api.OnlineStockStrategyAPI;
import com.realsight.westworld.tsp.lib.util.data.TimeseriesData;

/**
 * @author √»
 * 
 */ 
public class StockExample {
	
	public static void main(String[] args) throws Exception {
		Path root = Paths.get(System.getProperty("user.dir"), "data", "stock_data", "600010.csv");
		TimeseriesData td = new TimeseriesData(root.toAbsolutePath());
		OnlineStockStrategyAPI ossa = new OnlineStockStrategyAPI();
		ossa.run(td.getPropertyDoubleSeries());
	}
}
