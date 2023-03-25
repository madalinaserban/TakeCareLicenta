package com.example.licentatakecare.map.util;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import com.example.licentatakecare.R;
import com.example.licentatakecare.map.models.Hospital;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class DirectionsHelper {

        private static final int MAX_HOSPITAL_DISTANCE = 10000; // Maximum distance to consider a hospital (in meters)
        private static final int MAX_HOSPITAL_AVAILABILITY = 0; // Maximum availability to consider a hospital

        public static Hospital getClosestHospitalWithAvailability(Location currentLocation, List<Hospital> hospitals,ESection section) {
            Hospital closestHospital = null;
            float closestDistance = Float.MAX_VALUE;

            for (Hospital hospital : hospitals) {
                GeoPoint geoPoint = hospital.getGeoPoint();
                Location hospitalLocation = new Location("");
                hospitalLocation.setLatitude(geoPoint.getLatitude());
                hospitalLocation.setLongitude(geoPoint.getLongitude());

                float distance = currentLocation.distanceTo(hospitalLocation);
                if (distance <= MAX_HOSPITAL_DISTANCE && hospital.getAvailability(section) > MAX_HOSPITAL_AVAILABILITY) {
                    if (distance < closestDistance) {
                        closestHospital = hospital;
                        closestDistance = distance;
                    }
                }
            }

            return closestHospital;
        }

    }

