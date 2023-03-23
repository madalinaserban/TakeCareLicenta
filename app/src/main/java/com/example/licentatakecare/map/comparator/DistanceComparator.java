package com.example.licentatakecare.map.comparator;
import android.location.Location;
import com.example.licentatakecare.map.models.Hospital;
import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

public class DistanceComparator implements Comparator<Hospital> {
    private LatLng userLocation;

    public DistanceComparator(LatLng userLocation) {
        this.userLocation = userLocation;
    }

    @Override
    public int compare(Hospital h1, Hospital h2) {
        float[] results1 = new float[1];
        Location.distanceBetween(userLocation.latitude, userLocation.longitude,
                h1.getGeoPoint().getLatitude(), h1.getGeoPoint().getLongitude(), results1);
        float distance1 = results1[0];
        float[] results2 = new float[1];
        Location.distanceBetween(userLocation.latitude, userLocation.longitude,
                h2.getGeoPoint().getLatitude(), h2.getGeoPoint().getLongitude(), results2);
        float distance2 = results2[0];
        return Float.compare(distance1, distance2);
    }

}
