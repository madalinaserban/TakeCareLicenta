package com.example.licentatakecare.map.util.directions;

import com.example.licentatakecare.map.models.hospital.Hospital;
import com.example.licentatakecare.map.models.hospital.Section;
import com.example.licentatakecare.map.util.clusters.ESection;
import com.google.android.gms.maps.model.LatLng;

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


    //EUCLIDIAN DE FACUT DUPA HARTA !!!
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
