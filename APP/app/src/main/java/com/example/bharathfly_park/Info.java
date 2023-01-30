package com.example.bharathfly_park;

import android.graphics.Bitmap;

import java.util.Calendar;
import java.util.Date;

public class Info {
    public String img;
    public float[] pred;
    public Date currentTime;
    public double[] loc;

    public Info(){
       this.currentTime  = Calendar.getInstance().getTime();
    }


}

