package com.example.licentatakecare.map.models.directions;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface DirectionsApiInterface {
    @GET
    Call<DirectionsResponse> getDirections(@Url String url);
}


