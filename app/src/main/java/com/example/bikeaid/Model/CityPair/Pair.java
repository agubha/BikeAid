package com.example.bikeaid.Model.CityPair;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pair {
    @SerializedName("a")
    @Expose
    private String a;
    @SerializedName("b")
    @Expose
    private String b;
    @SerializedName("dis")
    @Expose
    private Integer dis;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public Integer getDis() {
        return dis;
    }

    public void setDis(Integer dis) {
        this.dis = dis;
    }
}
