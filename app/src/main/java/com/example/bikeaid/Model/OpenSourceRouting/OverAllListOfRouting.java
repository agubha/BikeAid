package com.example.bikeaid.Model.OpenSourceRouting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OverAllListOfRouting {
    @SerializedName("data")
    @Expose
    private List<ResponsePath> data = null;

    public List<ResponsePath> getData() {
        return data;
    }

    public void setData(List<ResponsePath> data) {
        this.data = data;
    }
}
