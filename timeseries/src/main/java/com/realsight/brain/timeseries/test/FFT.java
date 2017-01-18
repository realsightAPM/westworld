package com.realsight.brain.timeseries.test;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abedaigorou on 16/03/21.aaaaa
 */
public class FFT
{
    public static double PI=FastMath.PI;
    public Complex[] data;
    private List<Complex> FFTList=new ArrayList<Complex>();
    private Double[] FFTData;
    public  int FFTLength;
    private int bitsNum;
    private int smpf;

    public FFT(Short[] data, int smpf)
    {
        this.bitsNum=aryAdd(data);
        this.smpf=smpf;
    }

    public Double[] getFFTData()
    {
        butterFlyCalc(this.data.length,this.data);
        Complex[] FFTData=FFTList.toArray(new Complex[0]);
        List<Double> data=new ArrayList<Double>();
        for(Complex c:FFTData)
            data.add(c.abs()/this.data.length);
        this.FFTData=reverse(data.toArray(new Double[0]),bitsNum);
        this.FFTLength=FFTData.length;
        return this.FFTData;
    }


    private void butterFlyCalc(int length,Complex[] data)
    {
        int halfLen=length/2;
        Complex[] plusData=new Complex[halfLen];
        Complex[] minusData=new Complex[halfLen];

        for(int i=0;i<halfLen;i++) {
            if(i>=halfLen)
                return;
            plusData[i]=data[i].add(data[halfLen + i]);
            minusData[i] = new Complex(0, -2 * PI * i / length).exp();
            //minusData[i]=new Complex(0,-2*PI*i/smpRate).exp();
            minusData[i] = minusData[i].multiply(data[i].subtract(data[halfLen + i]));
            if(length==2){
                FFTList.add(plusData[0]);
                FFTList.add(minusData[0]);
                return;
            }
        }
        butterFlyCalc(plusData.length,plusData);
        butterFlyCalc(minusData.length,minusData);
        //System.out.println(new Complex(0,-2*PI/8).exp());
    }

    private Double[] reverse(Double[] data,int bitsNum)
    {
        double tempData;
        int len=data.length;
        for(int i=0;i<len;i++)
        {
            int revNum=Integer.rotateRight(Integer.reverse(i),Integer.SIZE-bitsNum);
            //System.out.println("swap:"+i+"("+data[i]+")"+"and"+Integer.rotateRight(Integer.reverse(i),Integer.SIZE-bitsNum)+"("+data[Integer.rotateRight(Integer.reverse(i),Integer.SIZE-bitsNum)]+")");
            if(i<=revNum) {
                //System.out.println(bitsNum+"swap:"+i+"to"+revNum);
                tempData = data[i];
                data[i] = data[revNum];
                data[revNum] = tempData;
            }
            //System.out.println(Integer.toBinaryString(i)+":rev:"+Integer.toBinaryString(Integer.rotateRight(Integer.reverse(i), 32 - 3)));
        }
        return data;
    }

    private int aryAdd(Short[] data)
    {
        int count=0;
        List<Complex> tempData=new ArrayList<Complex>();
        for(int i=0;i<data.length;i++)
            tempData.add(new Complex(data[i].doubleValue()*window.hamming(i,data.length)));
        int len=tempData.size();int len2=len;
        while((double)(len/=2)>=1)
            count++;
        int j=(int)FastMath.pow(2,count+1);

        for(int i=len2;i<j;i++) {
            tempData.add(new Complex(0));
        }

        this.data=tempData.toArray(new Complex[0]);
        //System.out.println("arrayAdd:"+data.length+":"+tempData.size());
        return count+1;
    }

    public int arrayMaxIndex() {
        int index = 0;
        for (int i = 1; i < FFTData.length/2; i++)
            index = (FFTData[index] >= FFTData[i]) ? index : i;
        return index;
    }
}
