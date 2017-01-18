package com.realsight.brain.timeseries.test;

/**
 * Created by abedaigorou on 16/04/10.
 */
public class SFFT extends Thread implements Runnable
{
    protected final int bit=16;
    protected final int hz=44100;
    protected final int MONO=1;
    protected final int N=1024;

    protected boolean isRunning=true;
    protected recorder rec=new recorder(bit,hz,MONO,N);
    protected Short[] rdata=new Short[N/2];
    protected FFT fft;
    protected Double[] FFTData;

    @Override
    public void run() {
        while (true) {
            if (!isRunning)
                break;
            rdata = rec.getVoice();
            fft = new FFT(rdata,hz);
            FFTData = fft.getFFTData();
            System.out.println("max:" + ((double) (hz/ fft.FFTLength)) * fft.arrayMaxIndex());
        }
    }

    public Double[] getFFTData()
    {
        return FFTData;
    }

    public void Stop()
    {
        isRunning=false;
        rec.stop();
    }
}
