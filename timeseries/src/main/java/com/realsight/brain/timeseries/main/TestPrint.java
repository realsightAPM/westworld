package com.realsight.brain.timeseries.main;

/**
 * @author qefee
 * 
 */
public class TestPrint {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			int n = i + 1;
			String s = n + "%";
			System.out.print(n + "%");

			if (i == 99) {
				break;
			}

			for (int j = 0; j < s.length(); j++) {
				System.out.print('\b');
			}

			waitForSometime(50);
		}

	}

	/**
	 * waitForSometime.
	 * 
	 * @param time
	 */
	private static void waitForSometime(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
