package com.realsight.westworld.tsp.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.realsight.westworld.tsp.api.VideoCutAPI;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;
import com.realsight.westworld.tsp.lib.util.Util;
import com.realsight.westworld.tsp.lib.util.data.TimeseriesData;
import com.realsight.westworld.tsp.lib.util.plot.Plot;

import Jama.Matrix;

/**
 * @author √»
 * 
 */ 
public class VideoCutExample {
	/**
	 * @param args
	 * @throws Exception 
	 */
	
	// 14_14
//	private int[] base = {0,470,1000,1600,2300,3550,4653};
//	private int[] pca = {0,320,1480,1740,2320,3140,3620,4653};
//	private int[] ppca = {0,480,1069,1608,2267,3586,4075,4344,4653};
//	private int[] ershixiong = {0,477,837,1477,1963,2394,3003,3521,3954,4461,4653};
	
	// seq1 
//	private int[] base = {0,762, 880, 1036, 1259, 1408, 1578, 1675, 2031};
//	private int[] pca = {0,700,1390,4653};
//	private int[] ppca = {0,700,1209,1693,4653};
//	private int[] ershixiong = {0,743,857,929,1022,1079,1143,1246,1303,1376,1442,1522,1574,1630,1684,4653};
	
	// seq2
//	private int[] base = {0,420,561,733,892,1038,1210, 1330, 1535};
//	private int[] pca = {0,220,400,1535};
//	private int[] ppca = {0,640,1280,1535};
//	private int[] ershixiong = {0,404,465,560,643,695,771,822,878,929,1004,1094,1192,1271,1535};
	
	// seq3
//	private int[] base = {0, 1120, 1218, 1336, 1500, 1618, 1775, 1912, 2097};
//	private int[] pca = {0, 2097};
//	private int[] ppca = {0, 560,979,1258,1747, 2097};
//	private int[] ershixiong = {0, 977,1050,1142,1252,1338,1399,1458,1552,1630,1738, 2097};

	// seq4
//	private int[] base = {0, 146, 229, 399, 541, 682, 801, 940, 1242};
//	private int[] pca = {0, 1242};
//	private int[] ppca = {0, 230,1036, 1242};
//	private int[] ershixiong = {0, 111,163,229,308,371,422,484,594,682,758,884,952,1027, 1242};

	// seq5
//	private int[] base = {0, 600, 748, 875, 1025, 1210, 1401, 1527, 1816};
//	private int[] pca = {0, 660, 1816};
//	private int[] ppca = {0, 410,729,1514, 1816};
//	private int[] ershixiong = {0, 543,634,702,798,872,935,1019,1124,1226,1283,1338,1392,1445,1505, 1816};

	// seq6
//	private int[] base = {0, 190, 288, 439, 615, 739, 865, 1458};
//	private int[] pca = {0, 1458};
//	private int[] ppca = {0, 250,649,1216, 1458};
//	private int[] ershixiong = {0, 180,262,366,417,491,559,633,722,785,870,952,1021,1094,1151,1207, 1458};

	// seq7
//	private int[] base = {0, 790, 918, 1051, 1188, 1284, 1403, 1537, 2168};
//	private int[] pca = {0, 330,790, 2168};
//	private int[] ppca = {0, 440,759,1318,1807, 2168};
//	private int[] ershixiong = {0, 693,783,874,953,1030,1084,1154,1213,1319,1400,1468,1519,1589,1672,1732,1807, 2168};
	
	// seq8
//	private int[] base = {0, 1080, 1239, 1350, 1493, 1610, 1764, 1942, 2090, 2378};
//	private int[] pca = {0, 400,1850, 2378};
//	private int[] ppca = {0, 390,709,1048,1427,1982, 2378};
//	private int[] ershixiong = {0, 1022,1101,1157,1217,1303,1381,1461,1518,1580,1639,1716,1778,1832,1893,1973, 2378};
	
	private int[] base = null;
	private int[] pca = null;
	private int[] ppca = null;
	private int[] ershixiong = null;
	
	public double Jaccard(double l1, double r1, double l2, double r2) {
		double l = Math.max(l1, l2);
		double r = Math.min(r1, r2);
		double jiao = Math.max(r-l, 0);
		double bing = r1-l1+r2-l2-jiao;
		return 1.0*jiao/bing;
	}
	
	public List<Integer> toList(int[] arrays) {
		List<Integer> list = new ArrayList<Integer>();
		for (Integer i : arrays) {
			list.add(i);
		}
		return list;
	}
	
	public double evaluation(List<Integer> Base, List<Integer> Res) {
		double sum = 0.0;
		for (int b = 1; b < Base.size(); b++) {
			double f1 = 0;
			for (int r = 1; r < Res.size(); r++) {
				f1 = Math.max(f1, Jaccard(Base.get(b-1), Base.get(b), Res.get(r-1), Res.get(r)));
			}
//			System.out.print(f1 + ", ");
			sum += f1;
		}
//		System.out.println("");
		return sum/(Base.size()-1);
	}
	
	public void show() {
		System.out.println("pca : " + evaluation(toList(base),toList(pca)));
		System.out.println("ppca : " + evaluation(toList(base),toList(ppca)));
		System.out.println("ershixiong : " + evaluation(toList(base),toList(ershixiong)));
	}
	
	public DoubleSeries run(MultipleDoubleSeries mSeries) {
		VideoCutAPI cva = new VideoCutAPI(mSeries);
		
		DoubleSeries cut_scores = new DoubleSeries("cut score");
		DoubleSeries cuts = new DoubleSeries("cuts");
		List<Integer> Res = new ArrayList<Integer>();
		List<Integer> Base = toList(base);
		Res.add(0);
		int scope = (int) (mSeries.size()*0.050);
		int start = 500;
		double th = 0.7;
		boolean flag = false;
		System.out.print("ours = ");
		for (int i = 0; i < mSeries.size(); i++) {
			Matrix value = Util.toVec(mSeries.get(i).getItem().iterator());
			Long timestamp = mSeries.get(i).getInstant();
			Entry<Double> cut_score = cva.detection(value, timestamp);
			double score = cut_score.getItem();
			if (cut_scores.subSeries(Math.max(0, i-scope), i).max() >= th) {
				score = 0.0;
			}
			else if (cut_scores.size() < start) {
				score = 0.0;
			}
			else if (score < th) {
				score = 0.0;
			}
			if (score >= th) {
				Res.add(Integer.parseInt(String.valueOf(timestamp)));
				if (flag) System.out.print(",");
				System.out.print(cut_score.getInstant());
				flag = true;
			}
			cut_scores.add(new Entry<Double>(score, timestamp));
		}
		System.out.println("");
		Res.add(base[base.length-1]);
		for (int i = 0, j = 0; i < cut_scores.size(); i++) {
			Long timestamp = mSeries.get(i).getInstant();
			while (i > base[j]){
				j++;
			}
			double value = j%2;
			cuts.add(new Entry<Double>(value, timestamp));
		}
		Plot.plot("sb", cut_scores, cuts);
		System.out.println(evaluation(Base, Res));
		return cut_scores;
	}
	
	public void main() throws IOException {
		Path propertyPath = Paths.get(System.getProperty("user.dir"), "data", "baseline.txt");
        Properties property = new Properties();
        property.load(new FileInputStream(propertyPath.toFile()));
        System.out.println(property);
        Path root = Paths.get(System.getProperty("user.dir"), "data", property.getProperty("path").trim());
        base = new int[property.get("Base").toString().split(",").length];
        for (int i = 0; i < base.length; i++) {
        	base[i] = Integer.parseInt(property.get("Base").toString().split(",")[i].trim());
        }
        pca = new int[property.get("PCA").toString().split(",").length];
        for (int i = 0; i < pca.length; i++) {
        	pca[i] = Integer.parseInt(property.get("PCA").toString().split(",")[i].trim());
        }
        ppca = new int[property.get("PPCA").toString().split(",").length];
        for (int i = 0; i < ppca.length; i++) {
        	ppca[i] = Integer.parseInt(property.get("PPCA").toString().split(",")[i].trim());
        }
        ershixiong = new int[property.get("Ershixiong").toString().split(",").length];
        for (int i = 0; i < ershixiong.length; i++) {
        	ershixiong[i] = Integer.parseInt(property.get("Ershixiong").toString().split(",")[i].trim());
        }
        show();
		TimeseriesData tsd = new TimeseriesData(root);
		MultipleDoubleSeries mSeries = tsd.getPropertyDoubleSeries();
		run(mSeries);
	}
	
	public static void main(String[] args) throws IOException {
		new VideoCutExample().main();
	}
}
