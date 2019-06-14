package com.example.bikeaid.Model.City;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cities {
    @SerializedName("location")
    @Expose
    private List<Location> location = null;

    public List<Location> getLocation() {
        return location;
    }

    public void setLocation(List<Location> location) {
        this.location = location;
    }

}
