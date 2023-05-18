package com.example.licentatakecare.map.util.TimeandDistance;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DistanceMatrixService {
    @GET("distancematrix/json")
    Call<DistanceMatrixResponse> getDistanceMatrix(
            @Query("origins") String origins,
            @Query("destinations") String destinations,
            @Query("key") String apiKey
    );
}
