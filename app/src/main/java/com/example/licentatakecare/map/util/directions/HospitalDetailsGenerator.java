package com.example.licentatakecare.map.util.directions;

import android.content.Context;

import com.example.licentatakecare.R;
import com.example.licentatakecare.map.models.hospital.Hospital;
import com.example.licentatakecare.map.models.hospital.HospitalDetails;
import com.example.licentatakecare.map.util.PlacesApiInterface;
import com.example.licentatakecare.map.util.directions.callback.DetailsCallback;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HospitalDetailsGenerator {

    private PlacesApiInterface createPlacesApi() {
        return new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PlacesApiInterface.class);
    }

    public void displayHospitalDetails(Context mContext, Hospital closestHospital, final DetailsCallback detailsCallback) {

        PlacesApiInterface placesApiInterface = createPlacesApi();

        String placeId = closestHospital.getGoogle_id();
        String fields = "name,formatted_address,formatted_phone_number,website,opening_hours,rating,photo";
        String url = "https://maps.googleapis.com/maps/api/place/details/json?" +
                "place_id=" + placeId +
                "&fields=" + fields +
                "&key=" + mContext.getResources().getString(R.string.google_maps_key);


        Call<HospitalDetails> call = placesApiInterface.getPlaceDetails(placeId, fields, mContext.getResources().getString(R.string.google_maps_key));
        call.enqueue(new Callback<HospitalDetails>() {
            @Override
            public void onResponse(Call<HospitalDetails> call, Response<HospitalDetails> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Extract the details for the hospital from the API response
                    HospitalDetails.Result result = response.body().getResult();
                    String name = result.getName();
                    String address = result.getFormattedAddress();
                    String phone = result.getFormattedPhoneNumber();
                    String website = result.getWebsite();
                    double rating = result.getRating();
                    boolean isOpen = result.isOpenNow();
                    String photoUrl = result.getPhotoUrl();

                    // Pass the details to the callback
                    detailsCallback.onDetailsReceived(name, address, phone, website, rating, isOpen, photoUrl);

                }
            }

            @Override
            public void onFailure(Call<HospitalDetails> call, Throwable t) {
                // Handle API call failure
            }
        });
    }
}
