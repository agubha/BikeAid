package com.example.bikeaid.Model;

import com.example.bikeaid.Model.OpenSourceRouting.ResponsePath;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenSourceConnection {

    @GET("driving-car/")
    Call<ResponsePath> requestPath(@Query("api_key") String string, @Query("start")String v, @Query("end") String v1);
}
