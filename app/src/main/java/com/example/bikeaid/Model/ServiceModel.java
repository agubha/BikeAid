package com.example.bikeaid.Model;

import com.google.gson.annotations.SerializedName;

public class ServiceModel {
    @SerializedName("description")
    private String description;
    @SerializedName("id")
    private int id;
    @SerializedName("image")
    private String image;
    @SerializedName("price")
    private int price;
    @SerializedName("title")
    private String title;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
