package com.realsight.westworld.tsp.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import com.realsight.westworld.tsp.lib.model.StockStrategy;
import com.realsight.westworld.tsp.lib.model.hashcode.ValueHash;
import com.realsight.westworld.tsp.lib.model.htm.StrategyHierarchy;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;
import com.realsight.westworld.tsp.lib.util.Util;
import com.realsight.westworld.tsp.lib.util.plot.Plot;

import Jama.Matrix;

@SuppressWarnings("unused")
public class OnlineStockStrategyAPI extends StockStrategy {

	/**
	 * @author √»
	 */

	private static final long serialVersionUID = -639738077125614136L;

	private Double minValue = -0.10;
	private Double maxValue = 0.10;
	
	private String action = null;

	public OnlineStockStrategyAPI() throws Exception {
		super();
		List<String> actions = new ArrayList<String>();
		actions.add("a");
		actions.add("b");
		super.HTM = new StrategyHierarchy(minValue, maxValue, actions);
	}

	public String todayPrice(Matrix yestoday, Matrix today, Matrix tomorrow, Long timestamp) {
		Matrix value = null;
		if (yestoday == null) {
			value = new Matrix(1, 1);
		} else {
			value = today.minus(yestoday).arrayRightDivide(yestoday);
		}
		HTM.learn(value);
		Double reward = tomorrow.minus(today).get(0, 0)/today.get(0, 0);
		
		action = HTM.action();
		if (action == null) {
			action = "a";
		}
		if (action.equals("b")) {
			reward = -reward;
		}
		HTM.learnAction(action, reward);
		return action;
	}

	public double getOpt(DoubleSeries prices, DoubleSeries upScores, Double value, boolean flag) {
		DoubleSeries AssetsSeries = new DoubleSeries("Total Assets");
		DoubleSeries nums = new DoubleSeries("Stock Number");
		DoubleSeries S = new DoubleSeries("P");
		Stock stock = new Stock(prices.get(0).getItem(), 0, 1000000.0);
		Double price = 0.0;
		for (int i = 0; i < upScores.size(); i++){
			Long timestamp = prices.get(i).getInstant();
			stock.sell(prices.get(i).getItem(), 100000);
			double tmp_p = upScores.get(i).getItem() + value;
			if (tmp_p > 0.0) {
				stock.buy(prices.get(i).getItem(), 100000);
			}
			S.add(new Entry<Double>(prices.get(i).getItem(), timestamp));
			Date date = new Date(timestamp);
			if (flag)
				System.out.println(date.toString() + "\t" + stock.totalAssets(prices.get(i).getItem()) + "\t" + stock + "\t"
						+ prices.get(i).getItem() + "\t" + tmp_p);
			AssetsSeries.add(new Entry<Double>(stock.totalAssets(prices.get(i).getItem()), timestamp));
			nums.add(new Entry<Double>(stock.getNumStock() + 0.0, timestamp));
			price = prices.get(i).getItem();
		}
		
		S.normly2();
		AssetsSeries.normly2();
		nums.normly();
		if (flag)
			Plot.plot("Total Assets " + value, S, AssetsSeries);
		return stock.totalAssets(price);
	}
	
	public void run(MultipleDoubleSeries nSeries) {
		nSeries.sort();
		Plot.plot(nSeries);
		for (int e = 1; e <= 0; e++) {
			System.err.println("epoch = " + e);
			for (int i = 1; i+1 < nSeries.size(); i++) {
				long timestamp = nSeries.get(i).getInstant();
				if (timestamp >= 1451577600000L) continue;
				
				Matrix yestoday = Util.toVec(nSeries.get(i-1).getItem().iterator());
				Matrix today = Util.toVec(nSeries.get(i).getItem().iterator());
				Matrix tomorrow = Util.toVec(nSeries.get(i+1).getItem().iterator());
				todayPrice(yestoday, today, tomorrow, timestamp);
			}
			super.HTM.sleep();
		}
		DoubleSeries upScores = new DoubleSeries("Up Scores");
		DoubleSeries prices = new DoubleSeries("prices");
		for (int i = 1; i+1 < nSeries.size(); i++) {
			long timestamp = nSeries.get(i).getInstant();
			
			Matrix yestoday = Util.toVec(nSeries.get(i-1).getItem().iterator());
			Matrix today = Util.toVec(nSeries.get(i).getItem().iterator());
			Matrix tomorrow = Util.toVec(nSeries.get(i+1).getItem().iterator());
			
			String action = todayPrice(yestoday, today, tomorrow, timestamp);
			
			if (timestamp < 1451577600000L) continue;
			
			double score = action.equals("a")?1.0:-1.0;
			upScores.add(new Entry<Double>(score, timestamp));
			prices.add(new Entry<Double>(today.get(0, 0), timestamp));
		}
		this.getOpt(prices, upScores, 0.0, true);

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

		public double getMoney() {
			return money;
		}

		public Stock(double price, long numStock, double menoy) {
			this.price = price;
			this.numStock = numStock;
			this.money = menoy;
		}

		public double totalAssets(double today_price) {
			return money + numStock * today_price;
		}

		public double totalNumStock(double today_price) {
			return money / today_price + numStock;
		}

		public void buy(double today_price, long num) {
			if (money < today_price * num) {
				num = (long) (money / today_price);
			}
			this.price = (today_price * num + price * numStock) / (num + numStock);
			this.numStock += num;
			this.money -= today_price * num;
		}

		public void sell(double today_price, long num) {
			if (this.numStock < num) {
				num = this.numStock;
			}
			this.numStock -= num;
			this.money += today_price * num;
		}

		public String toString() {
			String str = "money = " + this.money + ", stock = " + this.numStock;
			return str;
		}
	}
}
