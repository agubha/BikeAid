package com.example.bikeaid.Model.CityPair;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PairLists {

    @SerializedName("pairs")
    @Expose
    private List<Pair> pairs = null;

    public List<Pair> getPairs() {
        return pairs;
    }

    public void setPairs(List<Pair> pairs) {
        this.pairs = pairs;
    }
}
