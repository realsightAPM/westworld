package com.realsight.westworld.tsp.lib.util.results;

import com.realsight.westworld.tsp.lib.util.Util;
import com.realsight.westworld.tsp.lib.util.random.Normal;

public class Domain {
	private Long timestamp;
	private double value;
	private double fValue;
	private double sd;
	private double alpha = 0.9;
	
	public Domain(Long timestamp, double value, double fValue, double sd, double alpha) {
		Util.check(sd > 0.0);
		Util.check(alpha > 0.0);
		Util.check(alpha < 1.0);
		this.timestamp = timestamp;
		this.fValue = fValue;
		this.value = value;
		this.sd = sd;
		this.alpha = alpha;
	}
	
	public Long getTimestamp(){
		return timestamp;
	}
	public double getValue() {
		return value;
	}
	public double getSd() {
		return sd;
	}
	public double getFValue() {
		return fValue;
	}
	
	public double getUpper_bound() {
		return fValue + Normal.qnorm(0.5+alpha/2.0, sd);
	}
	public double getLower_bound() {
		return fValue + Normal.qnorm(0.5-alpha/2.0, sd);
	}
}
