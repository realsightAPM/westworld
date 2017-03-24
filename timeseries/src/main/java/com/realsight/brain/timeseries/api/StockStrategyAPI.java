package com.realsight.brain.timeseries.api;

import java.util.Date;

import com.realsight.brain.timeseries.lib.model.anomaly.AnormalyDetection;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;
import com.realsight.brain.timeseries.lib.util.plot.Plot;

public class StockStrategyAPI extends AnormalyDetection{
	
	private final double eps = 1e-5;
	
	public StockStrategyAPI(DoubleSeries nSeries, double minValue, double maxValue) {
		super(nSeries, minValue, maxValue);
	}
	
	public StockStrategyAPI(double minValue, double maxValue) {
		this(null, minValue, maxValue);
	}
	
	public StockStrategyAPI(DoubleSeries nSeries) {
		super(nSeries);
	}
	
	public double todayPrice(double price, Long timestamp) {
		return detection(price, timestamp).getItem();
	}
	
	public double upProbability(double base_price) {
		double up = eps, down = eps;
		double radius = (maxValue-minValue)*0.10;
		for (double value = base_price-radius; value < base_price+radius; value += radius/100.0) {
//			System.out.println(anormalyHTM.predict(-1L, value) + " " + value);
			if(value < base_price) down += anormalyHTM.predict(-1L, value);
			else up += anormalyHTM.predict(-1L, value);
		}
		return up / (up + down);
	}
	
	public void run(DoubleSeries nSeries) {
		Stock stock = null;
		DoubleSeries AssetsSeries = new DoubleSeries("Total Assets");
		DoubleSeries scores = new DoubleSeries("Total Scores");
		DoubleSeries nums = new DoubleSeries("Stock Number");
		DoubleSeries S = new DoubleSeries("P");
		for ( int i = 0; i < nSeries.size(); i++ ) {
			double today_price = nSeries.get(i).getItem();
			long timestamp = nSeries.get(i).getInstant();
			double score = todayPrice(today_price, timestamp);
			
			double tmp_p  = 0.0;
			if ( i < 1500 ) continue;
			else if ( i == 1500) {
				stock = new Stock(today_price, 10000, 0.0);
			} else {
				
				if (score > 0.7) {
					scores.add(new Entry<Double> (1.0, timestamp));
					if ( stock.getPrice() < today_price ) {
						stock.sell(today_price, stock.getNumStock()/2);
					} else {
						stock.buy(today_price, 100);
					}
				}
				tmp_p = upProbability(today_price);
				if (tmp_p < 0.5) {
					stock.sell(today_price, (long) (stock.getNumStock()/10+1));
				} else {
					Long num = 10L;
					stock.buy(today_price, num);
				}
			} 
			S.add(new Entry<Double> (today_price, timestamp));
			Date date = new Date(timestamp);
			System.out.println(date.toString() + "\t" + stock.totalNumStock(today_price) + "\t" + stock + "\t" + today_price + "\t" + tmp_p);
			AssetsSeries.add(new Entry<Double> (stock.totalNumStock(today_price), timestamp));
			nums.add(new Entry<Double> (stock.getNumStock()+0.0, timestamp));
		}
		S.normly();
		nSeries.normly();
		AssetsSeries.normly();
		nums.normly();
		Plot.plot("Total Assets", S, AssetsSeries, nums, scores);
	}
	
	private class Stock {
		private double price = 0.0;
		private long numStock = 0;
		private double money = 0.0;
		
		public double getPrice() {
			return price;
		}

		public long getNumStock() {
			return numStock;
		}
//
//		public double getMoney() {
//			return money;
//		}

		public Stock(double price, long numStock, double menoy) {
			this.price = price;
			this.numStock = numStock;
			this.money = menoy;
		}
		
		public double totalAssets(double today_price) {
			return money + numStock*today_price;
		}
		
		public double totalNumStock(double today_price) {
			return money/today_price + numStock;
		}
		
		public void buy(double today_price, long num){
			if(money < today_price*num) {
				num = (long) (money/today_price);
			}
			this.price = (today_price*num+price*numStock) / (num+numStock);
			this.numStock += num;
			this.money -= today_price*num;
		}
		
		public void sell(double today_price, long num) {
			if (this.numStock < num) {
				num = this.numStock-1;
			}
			this.numStock -= num;
			this.money += today_price*num;
		}
		
		public String toString(){
			String str = "money = " + this.money + ", stock = " + this.numStock;
			return str;
		}
	}
}
