package com.realsight.westworld.tsp.lib.util.random;

import com.realsight.westworld.tsp.lib.util.Util;

public class Normal {
	
	public static double norm(double x){
		return norm(x, 1.0);
	}
	public static double norm(double x, double sd){
		return norm(x, sd, 0.0);
	}
	public static double norm(double x, double sd, double mean){
		double t = 1.0/(Math.sqrt(2.0*Math.PI)*sd);
		t *= Math.exp(-0.5*Math.pow((x-mean)/sd, 2.0));
		return t;
	}
	
	public static double qnorm(double alpha) {
		return qnorm(alpha, 1.0);
	}
	public static double qnorm(double alpha, double sd) {
		return qnorm(alpha, sd, 0.0);
	}
	public static double qnorm(double alpha, double sd, double mean){
		Util.check(alpha > 0.0, "alpha error");
		Util.check(alpha < 1.0, "alpha error");
		double sum = 0.0;
		for(double x = -50.0*sd; x < 50.0*sd; x += 0.005*sd){
			sum += norm(x+mean, sd, mean)*(0.005*sd);
			if(sum >= alpha)
				return x+mean;
		}
		return Double.MAX_VALUE;
	}
}
