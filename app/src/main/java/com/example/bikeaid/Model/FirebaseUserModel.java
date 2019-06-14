package com.example.bikeaid.Model;

import android.net.Uri;

public class FirebaseUserModel {
    private String username;
    private String uriUserImage;
    private String uriNagrita;
    private String uriBlueBook;
    private String bikeType;

    public FirebaseUserModel() {

    }

    public FirebaseUserModel(String username, Uri uriUserImage, Uri uriNagrita, Uri uriBlueBook, String bikeType) {
        this.username = username;
        this.uriUserImage = String.valueOf(uriUserImage);
        this.uriNagrita = String.valueOf(uriNagrita);
        this.uriBlueBook = String.valueOf(uriBlueBook);
        this.bikeType = bikeType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUriUserImage() {
        return uriUserImage;
    }

    public void setUriUserImage(String uriUserImage) {
        this.uriUserImage = uriUserImage;
    }

    public String getUriNagrita() {
        return uriNagrita;
    }

    public void setUriNagrita(String uriNagrita) {
        this.uriNagrita = uriNagrita;
    }

    public String getUriBlueBook() {
        return uriBlueBook;
    }

    public void setUriBlueBook(String uriBlueBook) {
        this.uriBlueBook = uriBlueBook;
    }

    public String getBikeType() {
        return bikeType;
    }

    public void setBikeType(String bikeType) {
        this.bikeType = bikeType;
    }
}
