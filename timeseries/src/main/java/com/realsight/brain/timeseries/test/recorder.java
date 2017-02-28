package com.realsight.brain.timeseries.test;

import javax.sound.sampled.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abedaigorou on 16/01/22.
 */
public class recorder
{
    private int bit;
    private int hz;
    private int MONO;
    private int N;

    private TargetDataLine target;
    private AudioInputStream stream;
    private byte[] voice;
    private FileOutputStream output;
    private boolean isRunning=true;

    recorder(int bit,int hz,int MONO,int N)
    {
        this.bit=bit;
        this.hz=hz;
        this.MONO=MONO;
        this.N=N;
        this.voice=new byte[2*N];

        try {
            //オーディオフォーマットの指定
            AudioFormat af = new AudioFormat(hz,bit,MONO,true,false);
            //System.out.println(af.toString());
            //ターゲットデータラインを取得
            DataLine.Info info =new DataLine.Info(TargetDataLine.class,af);
            target=(TargetDataLine)AudioSystem.getLine(info);
            //ターゲットデータラインを開く
            target.open(af);

            //音声入力スタート
            target.start();

            //入力ストリームを取得
            stream=new AudioInputStream(target);


        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        try{
            output=new FileOutputStream("output.dat");
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                while (true) {
                    if (!isRunning)
                        return;
                    try {
                        stream.read(voice, 0, voice.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
    }

    public Short[] getVoice()
    {
        List<Short> LdataList=new ArrayList<Short>();
        List<Short> RdataList=new ArrayList<Short>();
        int j=0;
        for(int i=0;i<voice.length;i+=2){
            if(j++%2==0&&MONO==2)
                RdataList.add((short)readBytes(2,i,voice));
            else
                LdataList.add((short)readBytes(2,i,voice));
        }
        try {
            output.write(voice);
        }catch(IOException e)
        {
            e.printStackTrace();
        }

        return LdataList.toArray(new Short[0]);
    }

    public byte[] getBVoice()
    {
        return voice;
    }

    public void stop()
    {
        isRunning=false;
        target.stop();
        target.close();
    }

    private int readBytes(int byteNum,int indexNum,byte[] data)//リトルエンディアンでindexNum番目からbyteNumバイト読み込む
    {
        indexNum+=byteNum-1;
        int bytes=0;
        int j=0;
        for (int i = 2*(byteNum-1); i >= 0; i -= 2)
            bytes += ((data[indexNum + j--]&0xff)* Math.pow(16, i));

        return (byteNum<3)?((short)bytes):(bytes);
    }


}
