package com.example.licentatakecare.map.util;

import com.example.licentatakecare.map.models.hospital.HospitalDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApiInterface {

    @GET("maps/api/place/details/json")
    Call<HospitalDetails> getPlaceDetails(@Query("place_id") String placeId,
                                          @Query("fields") String fields,
                                          @Query("key") String apiKey);
}


