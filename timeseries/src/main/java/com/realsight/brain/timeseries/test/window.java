package com.realsight.brain.timeseries.test;

import static org.apache.commons.math3.util.FastMath.PI;
import static org.apache.commons.math3.util.FastMath.cos;

/**
 * Created by abedaigorou on 16/03/28.
 */
public class window
{

    public static double hamming(double x,int N)
    {
        if(0<=x&&x<N-1)
            return 0.54- (0.46*cos(2*PI*x/(N-1)));
        else
            return 0;
    }
}
