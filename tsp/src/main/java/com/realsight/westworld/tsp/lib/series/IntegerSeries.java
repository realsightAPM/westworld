package com.realsight.westworld.tsp.lib.series;

import java.util.ArrayList;
import java.util.List;

public class IntegerSeries extends TimeSeries<Integer> {
    String mName;

    public IntegerSeries(List<Entry<Integer>> data, String name) {
        super(data);
        mName = name;
    }

    public IntegerSeries(String name) {
        super();
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public IntegerSeries and(IntegerSeries other) {
    	throw new UnsupportedOperationException("and");
    }

    public IntegerSeries or(IntegerSeries other) {
    	throw new UnsupportedOperationException("or");
    }

    public IntegerSeries plus(IntegerSeries other) {
    	throw new UnsupportedOperationException("plus");
    }

    public IntegerSeries plus(double factor) {
    	throw new UnsupportedOperationException("plus");
    }

    public IntegerSeries mul(IntegerSeries other) {
    	throw new UnsupportedOperationException("mul");
    }

    public IntegerSeries mul(double factor) {
    	throw new UnsupportedOperationException("mul");
    }

    public IntegerSeries div(IntegerSeries other) {
    	throw new UnsupportedOperationException("div");
    }
    
    public IntegerSeries returns() {
    	throw new UnsupportedOperationException("returns");
    }

    public double getLast() {
        return getData().get(size() - 1).getItem();
    }

    public IntegerSeries tail(int n) {
        return new IntegerSeries(getData().subList(size() - n, size()), getName());
    }

    public IntegerSeries returns(int days) {
    	throw new UnsupportedOperationException("returns");
    }

    public double[] toArray() {
    	double[] res = new double[mData.size()];
    	for(int i = 0; i < mData.size(); i++)
    		res[i] = mData.get(i).getItem();
        return res;
    }

    @Override
    public IntegerSeries toAscending() {
        return new IntegerSeries(super.toAscending().mData, getName());
    }

    @Override
    public IntegerSeries toDescending() {
        return new IntegerSeries(super.toDescending().mData, getName());
    }

    @Override
    public IntegerSeries lag(int k) {
        return new IntegerSeries(super.lag(k).mData, getName());
    }
    
    public IntegerSeries subSeries(int l, int r) throws Exception {
    	if(l<0 || r>size())
    		throw new Exception("l<0 || r>size()");
    	List<Entry<Integer>> newEntries = new ArrayList<com.realsight.westworld.tsp.lib.series.TimeSeries.Entry<Integer>>();
    	for(int i = l; i < r; i++){
    		newEntries.add(new Entry<Integer>(mData.get(i).getItem(), this.mData.get(i).getInstant()));
    	}
    	return new IntegerSeries(newEntries, mName);
    }
    
    public int getIdxFromInstant(Long mInstant){
    	for(int i = 0; i < this.size(); i++){
    		if(mInstant == this.get(i).getInstant())
    			return i;
    	}
    	return -1;
    }
    
    public double sqrtSum(){
    	double sum = 0;
    	for(int i = 0; i < this.size(); i++){
    		sum += Math.pow(this.getData().get(i).getItem(), 2);
    	}
    	return sum/this.size();
    }

    public double min(){
    	double min_value = Double.MAX_VALUE;
    	for(int i = 0; i < this.size(); i++){
    		min_value = Math.min(min_value, this.getData().get(i).getItem());
    	}
    	return min_value;
    }
    
    public double max(){
    	double max_value = Double.MAX_VALUE;
    	for(int i = 0; i < this.size(); i++){
    		max_value = Math.min(max_value, this.getData().get(i).getItem());
    	}
    	return max_value;
    }
    
    public double kth(int k) {
    	double l = min(), r = max();
    	for(int iter = 0; iter < 50; iter++){
    		double k_value = (l+r)/2.0;
    		int num = 0;
    		for(int i = 0; i < this.size(); i++){
        		if(k_value < this.getData().get(i).getItem())
        			num++;
        	}
    		if(num < k) l = k_value;
    		else r = k_value;
    	}
    	return (l+r)/2.0;
    }
    
    @Override
    public String toString() {
        return mData.isEmpty() ? "DoubleSeries{empty}" :
            "DoubleSeries{" +
                "mName=" + mName +
                ", from=" + mData.get(0).getInstant() +
                ", to=" + mData.get(mData.size() - 1).getInstant() +
                ", size=" + mData.size() +
                '}';
    }
}

