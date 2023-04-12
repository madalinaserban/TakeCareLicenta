package com.example.licentatakecare.map.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class HospitalDistanceCalculator {

    public static TreeMap<Double, Hospital> getHospitalsByDistance(LatLng userLocation, List<Hospital> hospitals) {
        TreeMap<Double, Hospital> hospitalsByDistance = new TreeMap<>();

        for (Hospital hospital : hospitals) {
            LatLng hospitalLocation = new LatLng(hospital.getGeoPoint().getLatitude(), hospital.getGeoPoint().getLongitude());
            double distance = distanceBetween(userLocation, hospitalLocation);
            hospitalsByDistance.put(distance, hospital);
        }

        return hospitalsByDistance;
    }

    private static double distanceBetween(LatLng point1, LatLng point2) {
        double lat1 = point1.latitude;
        double lon1 = point1.longitude;
        double lat2 = point2.latitude;
        double lon2 = point2.longitude;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6371 * c * 1000; // in meters

        return distance;
    }


}
