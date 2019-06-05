package com.example.bikeaid.Model.OpenSourceRouting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Properties {
    @SerializedName("segments")
    @Expose
    private List<Segment> segments = null;
    @SerializedName("summary")
    @Expose
    private Summary summary;
    @SerializedName("way_points")
    @Expose
    private List<Integer> wayPoints = null;

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public List<Integer> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(List<Integer> wayPoints) {
        this.wayPoints = wayPoints;
    }

}
