package com.realsight.westworld.tsp.lib.model.analysis.context;

import java.util.ArrayList;
import java.util.List;

public class LoggerContext implements Comparable<LoggerContext>{
		private Double score = 0.0;
		private String url = null;
		private List<String> infos = new ArrayList<String>();
		private int average = 0;
		private int appear = 0;
		
		public Double getScore() {
			return score;
		}

		public String getUrl() {
			return url;
		}

		public List<String> getInfos() {
			return infos;
		}
		
		public int getAverage() {
			return average;
		}
		
		public int getAppear() {
			return appear;
		}

		public LoggerContext(Double score, String url, int average, int appear, List<String> infos) {
			this.score = score;
			this.url = url;
			this.infos = infos;
			this.average = average;
			this.appear = appear;
		}

		@Override
		public int compareTo(LoggerContext o) {
			// TODO Auto-generated method stub
			if (this.getScore() > o.getScore())
				return -1;
			if (this.getScore() < o.getScore())
				return 1;
			return 0;
		}
}
