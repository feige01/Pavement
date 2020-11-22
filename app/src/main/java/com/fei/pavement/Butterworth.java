package com.fei.pavement;

import biz.source_code.dsp.filter.*;
public class Butterworth {

    public synchronized double[] IIRFilter(double[] signal, double[] a, double[] b) {
//        System.out.println("?????");

//        iirFilterCoefficients= IirFilterDesignExstrom.design(FilterPassType.lowpass, 5,50,50);
        double[] in = new double[b.length];
        double[] out = new double[a.length-1];

        double[] outData = new double[signal.length];
        System.out.println(signal.length);
        for (int i = 0; i < 12000; i++) {
//            if(i==12000)
//            System.out.println("i="+i+" length="+signal.length);
//            System.out.println("?????");
            System.arraycopy(in, 0, in, 1, in.length - 1);
            in[0] = signal[i];

            //calculate y based on a and b coefficients
            //and in and out.
            float y = 0;
            for(int j = 0 ; j < b.length ; j++){
                y += b[j] * in[j];

            }

            for(int j = 0;j < a.length-1;j++){
                y -= a[j+1] * out[j];
            }

            //shift the out array
            System.arraycopy(out, 0, out, 1, out.length - 1);
            out[0] = y;

            outData[i] = y;

        }
        System.out.println("@@@@@@");
        return outData;
    }
//    double[] time = new double[150];
//    double[] valueA = new double[150];
//	for (int i = 0; i < 50 * 3; i++) {
//        time[i] = i / 50.0;
//        valueA[i] = Math.sin(2 * Math.PI * 5 * time[i])+Math.sin(2 * Math.PI * 15 * time[i]);
//    }
}