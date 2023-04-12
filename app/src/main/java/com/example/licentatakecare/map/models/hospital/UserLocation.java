package com.example.licentatakecare.map.models.hospital;

import com.google.android.gms.maps.model.LatLng;

public class UserLocation {
    private LatLng current_location;

    public LatLng getCurrent_location() {
        return current_location;
    }

    public void setCurrent_location(LatLng current_location) {
        this.current_location = current_location;
    }
}
