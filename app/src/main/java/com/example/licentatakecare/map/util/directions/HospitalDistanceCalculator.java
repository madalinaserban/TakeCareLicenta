package com.example.licentatakecare.map.util.directions;

import android.location.Location;

import com.example.licentatakecare.map.models.directions.Distance;
import com.example.licentatakecare.map.models.directions.Duration;
import com.example.licentatakecare.map.models.hospital.Hospital;
import com.example.licentatakecare.map.util.timeAndDistance.CalculateDistancesCallback;
import com.example.licentatakecare.map.util.timeAndDistance.DistanceMatrixElement;
import com.example.licentatakecare.map.util.timeAndDistance.DistanceMatrixResponse;
import com.example.licentatakecare.map.util.timeAndDistance.DistanceMatrixService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HospitalDistanceCalculator {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static final String API_KEY = "AIzaSyDHanKxsZf-dj1MjStUejStPo7XoqpIQWo";

    private DistanceMatrixService distanceMatrixService;

    public HospitalDistanceCalculator() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        distanceMatrixService = retrofit.create(DistanceMatrixService.class);
    }

    public void calculateDistancesToHospitals(Location userLocation, List<Hospital> hospitals, final CalculateDistancesCallback callback) {
        // Build the destinations string
        StringBuilder destinationsBuilder = new StringBuilder();
        for (Hospital hospital : hospitals) {
            destinationsBuilder.append(hospital.getGeoPoint().getLatitude())
                    .append(",")
                    .append(hospital.getGeoPoint().getLongitude())
                    .append("|");
        }
        String destinations = destinationsBuilder.toString();

        Call<DistanceMatrixResponse> call = distanceMatrixService.getDistanceMatrix(
                userLocation.getLatitude() + "," + userLocation.getLongitude(),
                destinations,
                API_KEY
        );

        call.enqueue(new Callback<DistanceMatrixResponse>() {
            @Override
            public void onResponse(Call<DistanceMatrixResponse> call, Response<DistanceMatrixResponse> response) {
                if (response.isSuccessful()) {
                    DistanceMatrixResponse distanceMatrixResponse = response.body();
                    if (distanceMatrixResponse != null && distanceMatrixResponse.rows.length > 0) {
                        DistanceMatrixElement[] elements = distanceMatrixResponse.rows[0].elements;
                        for (int i = 0; i < elements.length; i++) {
                            Distance distance = elements[i].distance;
                            Duration duration = elements[i].duration;

                            Hospital hospital = hospitals.get(i);
                            hospital.setDistance(distance.value);
                            hospital.setTimeToGetThere(duration.text);
                        }

                        callback.onDistancesCalculated(hospitals);
                    } else {
                        callback.onDistancesCalculationFailed();
                    }
                } else {
                    callback.onDistancesCalculationFailed();
                }
            }

            @Override
            public void onFailure(Call<DistanceMatrixResponse> call, Throwable t) {
                callback.onDistancesCalculationFailed();
            }
        });
    }
}




