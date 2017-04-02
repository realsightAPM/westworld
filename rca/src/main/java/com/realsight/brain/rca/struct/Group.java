package com.realsight.brain.rca.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.realsight.brain.timeseries.api.OnlineAnormalyDetectionAPI;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;

public class Group {
	private static final Map<String, Group> groups = new HashMap<String, Group>();
	private OnlineAnormalyDetectionAPI AD = null;
	private List<String> property_list = null;
	private String name = null;
	private MultipleDoubleSeries series = null;
	private final Integer init_series_len = 200;
	
	public List<String> getProperty_list() {
		return property_list;
	}
	
	public String getName() {
		return name;
	}

	private Group(String name, String propertys) {
		this.name = name;
		this.property_list = new ArrayList<String>();
		for (int i = 0; i < propertys.split(",").length; i++) {
			if (propertys.split(",")[i].equals("timestamp")) continue;
			property_list.add(propertys.split(",")[i].trim());
		}
		this.series = new MultipleDoubleSeries(name, property_list);
	}
	
	public Entry<Double> anormalyScore(Double value, Long timestamp) {
		return this.AD.detection(value, timestamp);
	}
	
	@Deprecated
	public Entry<Double> uploadValues(Map<String, String> values, Long timestamp) {
		LinkedList<Double> list = new LinkedList<Double>();
		String key_property = property_list.get(0);
		for (String property : property_list) {
			String value = values.get(property).trim();
			list.add(Double.parseDouble(value));
		}
		if (AD==null && series.size()>init_series_len) {
			double minValue = series.getColumn(key_property).min();
			double maxValue = series.getColumn(key_property).max();
			double stepLen = maxValue - minValue;
			minValue -= stepLen/5;
			maxValue += stepLen/5;
			AD = new OnlineAnormalyDetectionAPI(series.getColumn(key_property), minValue, maxValue);
		} else if ( AD != null ) {
			String value = values.get(key_property).trim();
			return AD.detection(Double.parseDouble(value), timestamp);
		} else {
			series.add(new Entry<LinkedList<Double>>(list, timestamp));
		}
		return new Entry<Double>(0.0, timestamp);
	}
	
	public static boolean createGroup(String group_name, String propertys) {
		
		if(groups.containsKey(group_name))
			return false;
		groups.put(group_name, new Group(group_name, propertys));
		return true;
	}
	
	public static boolean deleteGroup(String group_name) {
		if(!groups.containsKey(group_name))
			return true;
		groups.remove(group_name);
		return true;
	}
	
	public static Group findGroup(String group_name) {
		if(!groups.containsKey(group_name))
			return null;
		return groups.get(group_name);
	}
	
	@Deprecated
	public static Double uploadDigitData(String group_name, String values) throws Exception {
		JSONObject propertysJSON = new JSONObject(values);
		if (! propertysJSON.has("timestamp"))
			throw new NoSuchFieldError("no find 'timestamp'.");
		
		Group group = findGroup(group_name);
		
		for (String property : group.getProperty_list()) {
			if(! propertysJSON.has(property)) {
				throw new NoSuchFieldException(property);
			}
		}
		
		Long timestamp = Long.parseLong(propertysJSON.optString("timestamp").trim());
		Map<String, String> property_values = new HashMap<String, String>();
		for (String property : group.getProperty_list()) {
			property_values.put(property, propertysJSON.optString(property));
		}
		Entry<Double> score = group.uploadValues(property_values, timestamp);
		return score.getItem();
	}
}
