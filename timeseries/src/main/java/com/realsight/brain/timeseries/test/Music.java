package com.realsight.brain.timeseries.test;

import com.lightdev.lib.audio.Player;
import com.lightdev.lib.audio.ui.SpectrumAnalyzer;

import kj.dsp.KJDigitalSignalProcessingAudioDataConsumer;

public class Music {
	private int main() {
		// create an instance of KJDigitalSignalProcessingAudioDataConsumer
		KJDigitalSignalProcessingAudioDataConsumer synchronizer = 
				new KJDigitalSignalProcessingAudioDataConsumer(4096, 10);

		// create an instance of the spectrum analyzer component
		SpectrumAnalyzer analyzer = new SpectrumAnalyzer(20, 12);

		// add the analyzer to the synchronizer as a KJDigitalSignalProcessor
		synchronizer.add(analyzer);

		// create a Player object
		Player player = new Player();

		// add the analyzer to the Player as a listener to player events
		player.addPlayerListener(analyzer);
		
		switch (Player.PLAYING) {
		case Player.PLAYING:
			// when the player fires a START event, start the synchronizer too
			synchronizer.start(player.getSourceDataLine());
		case Player.STOPPED_PLAY:
			// when the player fires PAUSE or STOP, stop the synchronizer
			synchronizer.stop();
		}
		return 0;
	}

	public static void main(String[] args) {
		new Music().main();
	}
}
