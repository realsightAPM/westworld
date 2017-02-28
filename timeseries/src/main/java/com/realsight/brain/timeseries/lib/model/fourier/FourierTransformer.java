package com.realsight.brain.timeseries.lib.model.fourier;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.math3.complex.Complex;

import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.util.data.SoundData;
import com.realsight.brain.timeseries.lib.util.data.TestData;
//import com.realsight.brain.timeseries.lib.util.data.RealSightData;
import com.realsight.brain.timeseries.lib.util.plot.Plot;

@SuppressWarnings("serial")
public class FourierTransformer  extends JFrame{
	private BufferedImage imageAuth = null;
	private DoubleSeries series = null;
	private int stepWidth = 0;
	private int spectrumHeight = 0;
	
	
	public FourierTransformer(DoubleSeries series, int stepWidth, int spectrumHeight) {
		this.series = series;
		this.stepWidth = stepWidth;
		this.spectrumHeight = spectrumHeight;
	}
	
	public FourierTransformer(DoubleSeries series) {
		this(series, 120, 128);
	}

	public void paint(Graphics g) {
		super.paint(g);
		if(imageAuth != null)
			g.drawImage(imageAuth, 0, 0, imageAuth.getWidth(), imageAuth.getHeight(), this);
		
	}
	
	public static void main(String[] args) {
		String root = "D:/DATA";
		String localPath = Paths.get(root, "EmotionSongs", "Angry_CSV", 
				"21st Century (Digital Boy) (Album Version).mp3.csv").toString();
		SoundData td = new SoundData(localPath);
		DoubleSeries nSeries = td.getPropertySeries();
		Plot.plot("sound", nSeries);
//		nSeries.normly();
		FourierTransformer frame = new FourierTransformer(nSeries);
		
		frame.setSize(200, 500);
		frame.setTitle("ImageMenu");
		frame.setName("hello my dongjing");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		frame.setVisible(true);
		frame.convert(frame.getGraphics());
	}
	public Image convert(Graphics g) {
		
		List<List<Double>> spectrumList = new ArrayList<List<Double>>();
		
		for ( int i = 0; i+this.spectrumHeight < this.series.size(); i += this.stepWidth ) {
			
			// 赋初值
			Complex[] src = new Complex[this.spectrumHeight];
			double[] pixels = new double[this.spectrumHeight];
			
			for ( int j = 0; j < this.spectrumHeight; j++ ) {
				pixels[j] = this.series.get(i+j).getItem();
			}
			for ( int j = 0; j < this.spectrumHeight; j++ ) {
				src[j] = new Complex(pixels[j], 0);
			}
			Complex[] dest = FFT.fft(src);
			
			List<Double> spectrum = new ArrayList<Double>();
			double mx = 0;
			for (int j = 0; j < this.spectrumHeight; j++) {
				if ( j >= dest.length){
					spectrum.add(0.0);
				} else {
					double re = dest[j].getReal();
					double im = dest[j].getImaginary();
					spectrum.add(Math.sqrt(re*re + im*im));
				}
			}
			spectrumList.add(spectrum);
		}
		
		int[] spectrumArray = new int[spectrumList.size()*this.spectrumHeight*3];
		
		for ( int i = 0; i < spectrumList.size(); i++ ) {
			for ( int j = 0; j < this.spectrumHeight; j++ ) {
				double value = spectrumList.get(i).get(j).intValue();
				for ( int k = 0; k < 2; k++ ) {
					int c = 0;
					if (value > 1.0) c = 255;
					else c = (int) (value*255);
					value -= 1.00;
					if (value < 0) value = 0;
					spectrumArray[(i*this.spectrumHeight+j) * 3 + k] = c;
//					System.out.println(spectrumArray[i*this.spectrumHeight+j]);
				}
			}
		}
		
		imageAuth = new BufferedImage(spectrumList.size(), this.spectrumHeight, BufferedImage.TYPE_3BYTE_BGR);
		ColorModel colorModel = imageAuth.getColorModel();
		WritableRaster raster = colorModel.createCompatibleWritableRaster(spectrumList.size(), this.spectrumHeight);	
		System.out.println(raster.getTransferType() + " " + raster.getNumDataElements());
		raster.setPixels(0, 0, spectrumList.size(), this.spectrumHeight, spectrumArray);
		imageAuth.setData(raster);
		
		try {
			ImageIO.write(imageAuth, "jpg", new File("fft_result.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.update(g);
		return imageAuth;
	}
}
