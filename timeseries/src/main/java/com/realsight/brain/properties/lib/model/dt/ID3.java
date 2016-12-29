package com.realsight.brain.properties.lib.model.dt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ID3 {
	
	public static final Logger LOG = LoggerFactory.getLogger(ID3.class);
	
	private ArrayList<String> attribute = new ArrayList<String>(); // 存储属性的名称
	private ArrayList<ArrayList<String>> attributevalue = new ArrayList<ArrayList<String>>(); // 存储每个属性的取值
	private ArrayList<String[]> data = new ArrayList<String[]>(); // 原始数据
	int decatt; // 决策变量在属性集中的索引
	public static final String patternString = "@attribute(.*)[{](.*?)[}]";

	Document xmldoc;
	Element root;

	public ID3() {
		xmldoc = DocumentHelper.createDocument();
		root = xmldoc.addElement("root");
		root.addElement("DecisionTree").addAttribute("value", "null");
	}

	public void readARFF(File file) {
		try {
			LOG.info("start read ARFF file.");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			Pattern pattern = Pattern.compile(patternString);
			while ((line = br.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					attribute.add(matcher.group(1).trim());
					String[] values = matcher.group(2).split(",");
					ArrayList<String> al = new ArrayList<String>(values.length);
					for (String value : values) {
						al.add(value.trim());
					}
					attributevalue.add(al);
				} else if (line.startsWith("@data")) {
					while ((line = br.readLine()) != null) {
						if (line == "")
							continue;
						String[] row = line.split(",");
						data.add(row);
					}
				} else {
					continue;
				}
			}
			br.close();
			LOG.info("end read ARFF file.");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void setDec(int n) {
		if (n < 0 || n >= attribute.size()) {
			System.err.println("决策变量指定错误。");
			System.exit(2);
		}
		decatt = n;
	}

	public void setDec(String name) {
		int n = attribute.indexOf(name);
		setDec(n);
	}

	public double getEntropy(int[] arr) {
		double entropy = 0.0;
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			entropy -= arr[i] * Math.log(arr[i] + Double.MIN_VALUE) / Math.log(2);
			sum += arr[i];
		}
		entropy += sum * Math.log(sum + Double.MIN_VALUE) / Math.log(2);
		entropy /= sum;
		return entropy;
	}

	public double getEntropy(int[] arr, int sum) {
		double entropy = 0.0;
		for (int i = 0; i < arr.length; i++) {
			entropy -= arr[i] * Math.log(arr[i] + Double.MIN_VALUE) / Math.log(2);
		}
		entropy += sum * Math.log(sum + Double.MIN_VALUE) / Math.log(2);
		entropy /= sum;
		return entropy;
	}

	public boolean infoPure(ArrayList<Integer> subset) {
		String value = data.get(subset.get(0))[decatt];
		for (int i = 1; i < subset.size(); i++) {
			String next = data.get(subset.get(i))[decatt];
			if (!value.equals(next))
				return false;
		}
		return true;
	}

	public double calNodeEntropy(ArrayList<Integer> subset, int index) {
		int sum = subset.size();
		double entropy = 0.0;
		int[][] info = new int[attributevalue.get(index).size()][];
		for (int i = 0; i < info.length; i++)
			info[i] = new int[attributevalue.get(decatt).size()];
		int[] count = new int[attributevalue.get(index).size()];
		for (int i = 0; i < sum; i++) {
			int n = subset.get(i);
			String nodevalue = data.get(n)[index];
			int nodeind = attributevalue.get(index).indexOf(nodevalue);
			count[nodeind]++;
			String decvalue = data.get(n)[decatt];
			int decind = attributevalue.get(decatt).indexOf(decvalue);
			info[nodeind][decind]++;
		}
		for (int i = 0; i < info.length; i++) {
			entropy += getEntropy(info[i]) * count[i] / sum;
		}
		return entropy;
	}

	public void buildDT(String name, String value, ArrayList<Integer> subset, LinkedList<Integer> selatt) {
		LOG.info("new build DT node name is \"" + name + "\", value is \"" + value + "\"");
		Element ele = null;
		List<Node> list = root.selectNodes("//" + name);
		Iterator<Node> iter = list.iterator();
		while (iter.hasNext()) {
			ele = (Element) iter.next();
			if (ele.attributeValue("value").equals(value))
				break;
		}
		if (infoPure(subset)) {
			ele.setText(data.get(subset.get(0))[decatt]);
			return;
		}
		int minIndex = -1;
		double minEntropy = Double.MAX_VALUE;
		for (int i = 0; i < selatt.size(); i++) {
			if (i == decatt)
				continue;
			double entropy = calNodeEntropy(subset, selatt.get(i));
			if (entropy < minEntropy) {
				minIndex = selatt.get(i);
				minEntropy = entropy;
			}
		}
		String nodeName = attribute.get(minIndex);
		selatt.remove(new Integer(minIndex));
		ArrayList<String> attvalues = attributevalue.get(minIndex);
		for (String val : attvalues) {
			ele.addElement(nodeName).addAttribute("value", val);
			ArrayList<Integer> al = new ArrayList<Integer>();
			for (int i = 0; i < subset.size(); i++) {
				if (data.get(subset.get(i))[minIndex].equals(val)) {
					al.add(subset.get(i));
				}
			}
			buildDT(nodeName, val, al, selatt);
		}
	}

	public void writeXML(String filename) {
		try {
			File file = new File(filename);
			if (!file.exists())
				file.createNewFile();
			FileWriter fw = new FileWriter(file);
			OutputFormat format = OutputFormat.createPrettyPrint(); // 美化格式
			XMLWriter output = new XMLWriter(fw, format);
			output.write(xmldoc);
			output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		ID3 inst = new ID3();
		String arffDir = Paths.get(System.getProperty("user.dir"), "target", "data", "weather.nominal.arff").toString();
		String dtDir = Paths.get(System.getProperty("user.dir"), "target", "data", "dt.xml").toString();
		inst.readARFF(new File(arffDir));
		inst.setDec("play");
		LinkedList<Integer> ll = new LinkedList<Integer>();
		for (int i = 0; i < inst.attribute.size(); i++) {
			if (i != inst.decatt)
				ll.add(i);
		}
		ArrayList<Integer> al = new ArrayList<Integer>();
		for (int i = 0; i < inst.data.size(); i++) {
			al.add(i);
		}
		inst.buildDT("DecisionTree", "null", al, ll);
		inst.writeXML(dtDir);
		LOG.info("DT completed.");
	}
}
