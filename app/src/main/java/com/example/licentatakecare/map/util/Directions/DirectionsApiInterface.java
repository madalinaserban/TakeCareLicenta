package com.example.licentatakecare.map.util.Directions;

import com.google.maps.model.DistanceMatrix;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface DirectionsApiInterface {
    @GET
    Call<DirectionsResponse> getDirections(@Url String url);
}


