package com.realsight.brain.timeseries.api;

import java.util.Date;

import com.realsight.brain.timeseries.lib.model.anomaly.AnormalyDetection;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;
import com.realsight.brain.timeseries.lib.util.Util;
import com.realsight.brain.timeseries.lib.util.plot.Plot;

import Jama.Matrix;

public class StockStrategyAPI extends AnormalyDetection {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -4661105653015378284L;

	public StockStrategyAPI(MultipleDoubleSeries nSeries) {
		super(nSeries);
	}
	
	public double todayPrice(Matrix price, Long timestamp) {
		return detection(price, timestamp).getItem();
	}
	
	public double upProbability(Matrix base_price) {
		double radius = base_price.get(0, 0)*0.1;
		double down = 0, up = 0;
		for (double value = -radius; value <= radius; value += radius/100.0) {
			Matrix price = base_price.copy();
			price.set(0, 0, value+base_price.get(0, 0));
			double tmp_p = anormalyHTM.predict(-1L, price);
			if(value < 0) down += tmp_p;
			else if(value > 0) up += tmp_p;
		}
		return up / (up + down);
	}
	
	public void run(MultipleDoubleSeries nSeries) {
		nSeries.sort();
		Stock stock = null;
		DoubleSeries AssetsSeries = new DoubleSeries("Total Assets");
		DoubleSeries scores = new DoubleSeries("Total Scores");
		DoubleSeries nums = new DoubleSeries("Stock Number");
		DoubleSeries S = new DoubleSeries("P");
		int sum = 0, acc = 0;
		double p_price = 0;
		double p = 0, up = 0, down = 0;
		for ( int i = 0; i < nSeries.size(); i++ ) {
			Matrix today_price = Util.toVec(nSeries.get(i).getItem().iterator());
			long timestamp = nSeries.get(i).getInstant();
			double score = todayPrice(today_price, timestamp);
			double tmp_p  = 0.0;
			if ( i < nSeries.size()*19/20 ) continue;
			else if ( i == nSeries.size()*19/20 ) {
				stock = new Stock(today_price.get(0, 0), 0, 1000.0);
			} else {
				if (score > 0.75) {
					scores.add(new Entry<Double> (1.0, timestamp));
					if ( nSeries.getColumn(0).subSeries(i-20, i).mean() < today_price.get(0, 0) ) {
						stock.sell(today_price.get(0, 0), 0);
					} else {
						stock.buy(today_price.get(0, 0), 0);
					}
				}   {
					tmp_p = upProbability(today_price);
					stock.sell(today_price.get(0, 0), 100000);
					
					if (p_price != today_price.get(0, 0)) {
						if (p > 0.5) sum += 1;
						if (p > 0.5 && today_price.get(0, 0)>p_price) acc += 1;
						if (today_price.get(0, 0)>p_price) up += 1.0;
						else down += 1.0;
					}
					p = tmp_p; p_price = today_price.get(0, 0);
					if (tmp_p > 0.50){
						stock.buy(today_price.get(0, 0), 100000);
					}
				}
			} 
//			System.out.println("acc - > " + (1.0*acc/sum) + ", up -> " + (up/(up+down)));
			S.add(new Entry<Double> (today_price.get(0, 0), timestamp));
			Date date = new Date(timestamp);
			System.out.println(date.toString() + "\t" + stock.totalAssets(today_price.get(0, 0)) + "\t" + stock + "\t" + today_price.get(0, 0) + "\t" + tmp_p);
			AssetsSeries.add(new Entry<Double> (stock.totalAssets(today_price.get(0, 0)), timestamp));
			nums.add(new Entry<Double> (stock.getNumStock()+0.0, timestamp));
		}
		S.normly2();
//		nSeries.getColumn(0).normly2();
		AssetsSeries.normly2();
		nums.normly();
		Plot.plot("Total Assets", S, AssetsSeries, scores, nums);
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
				num = this.numStock;
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
