package com.example.licentatakecare.map.util.directions;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.licentatakecare.R;
import com.example.licentatakecare.map.models.directions.DirectionsApiInterface;
import com.example.licentatakecare.map.models.directions.DirectionsResponse;
import com.example.licentatakecare.map.models.directions.model.Route;
import com.example.licentatakecare.map.models.hospital.Hospital;
import com.example.licentatakecare.map.util.directions.callback.DirectionsCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HospitalRouteGenerator {
    HospitalDistanceCalculator calculator;

    private DirectionsApiInterface createDirectionsApi() {
        return new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DirectionsApiInterface.class);
    }

    public void displayDirectionsToHospital(Context mContext, Location currentLocation, Hospital closestHospital, final DirectionsCallback directionsCallback) {

        // Use the Google Maps API to draw a route from the user's current location to the closest hospital
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        LatLng hlatLng = new LatLng(closestHospital.getGeoPoint().getLatitude(), closestHospital.getGeoPoint().getLongitude());
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + latLng.latitude + "," + latLng.longitude +
                "&destination=" + hlatLng.latitude + "," + hlatLng.longitude +
                "&key=" + mContext.getResources().getString(R.string.google_maps_key);


        createDirectionsApi().getDirections(url).enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (!response.isSuccessful()) {
                    Log.e("", "Direction response not successful: " + response.code());
                    directionsCallback.onFailure();
                    return;
                }

                Log.e("", "Origin: " + latLng.longitude + " " + latLng.latitude);
                Log.e("", "Destination: " + hlatLng.longitude + " " + hlatLng.latitude);

                DirectionsResponse directionsResponse = response.body();
                if (directionsResponse == null) {
                    Log.e("", "Direction response body is null");
                    directionsCallback.onFailure();
                    return;
                }

                List<Route> routes = directionsResponse.getRoutes();
                if (routes.isEmpty()) {
                    Log.e("", "Direction response has no routes");
                    directionsCallback.onFailure();
                    return;
                }

                // Get the first route
                Route route = routes.get(0);
                directionsCallback.onSuccess(route);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                // Handle failure here
                Log.e("A", "Direction API request failed", t);
                directionsCallback.onFailure();
            }
        });
    }
}
