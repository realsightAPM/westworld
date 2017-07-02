package com.realsight.westworld.tsp.stock.context;

import com.realsight.westworld.tsp.lib.util.TimeUtil;

public class StockResult{
	private String stockId = "";
	private Long timestamp = -1L;
	private String UTC8Time = "";
	private Double price = 0.0;
	private Double score = 0.0;
	private Double up = 0.0;
	
	
	public String getStockId() {
		return this.stockId;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public Double getPrice() {
		return price;
	}
	public Double getScore() {
		return score;
	}
	public Double getUp() {
		return up;
	}
	public String getUTC8Time() {
		return this.UTC8Time;
	}
	
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public void setPrice(Double price) {
		if (price.isNaN() || price.isInfinite()) {
			this.price = 0.0;
		} else {
			this.price = price;
		}
	}
	public void setScore(Double score) {
		if (score.isNaN() || score.isInfinite()) {
			this.score = 0.0;
		} else {
			this.score = score;
		}
	}
	public void setUp(Double up) {
		if (up.isNaN() || up.isInfinite()) {
			this.up = 0.0;
		} else {
			this.up = up;
		}
	}
	
	public StockResult() {}
	
	public StockResult(String stockId, Long timestamp, Double price, Double score, Double up) {
		this.stockId = stockId;
		this.timestamp = timestamp;
		this.UTC8Time = TimeUtil.formatUnixtime2(timestamp);
		if (price.isNaN() || price.isInfinite()) {
			this.price = 0.0;
		} else {
			this.price = price;
		}
		if (score.isNaN() || score.isInfinite()) {
			this.score = 0.0;
		} else {
			this.score = score;
		}
		if (up.isNaN() || up.isInfinite()) {
			this.up = 0.0;
		} else {
			this.up = up;
		}
	}
}
