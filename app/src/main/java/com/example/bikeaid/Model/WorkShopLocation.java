package com.example.bikeaid.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkShopLocation {
    @SerializedName("location")
    @Expose
    private List<Locations> location = null;

    public List<Locations> getLocation() {
        return location;
    }

    public void setLocation(List<Locations> location) {
        this.location = location;
    }
}
