package com.example.bikeaid.Utils;

import android.content.Context;

public class Util {
    public static float getdiscount(int price, int sales_price) {
        return (((float) price - (float) sales_price) / (float) price) * 100;
    }
    public static void toast(String msg, Context context){

    }
}
