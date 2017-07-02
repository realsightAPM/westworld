package com.realsight.westworld.tsp.lib.model.analysis.context;

import java.util.ArrayList;
import java.util.List;

public class LoggerContext implements Comparable<LoggerContext>{
		private Double score = 0.0;
		private String url = null;
		private List<String> infos = new ArrayList<String>();
		private int average_fre = 0;
		private int recent_fre = 0;
		private long average_elapsed = 0;
		private long recent_elapsed = 0;

		public Double getScore() {
			return score;
		}
		public String getUrl() {
			return url;
		}
		public List<String> getInfos() {
			return infos;
		}
		public int getAverage_fre() {
			return average_fre;
		}
		public int getRecent_fre() {
			return recent_fre;
		}
		public long getAverage_elapsed() {
			return average_elapsed;
		}
		public long getRecent_elapsed() {
			return recent_elapsed;
		}

		public LoggerContext(Double score, String url, List<String> infos, 
				int average_fre, int recent_fre, long average_elapsed, long recent_elapsed) {
			this.score = score;
			this.url = url;
			this.infos = infos;
			this.average_fre = average_fre;
			this.recent_fre = recent_fre;
			this.average_elapsed = average_elapsed;
			this.recent_elapsed = recent_elapsed;
		}

		public int compareTo(LoggerContext o) {
			// TODO Auto-generated method stub
			if (this.getScore() > o.getScore())
				return -1;
			if (this.getScore() < o.getScore())
				return 1;
			return 0;
		}
}
