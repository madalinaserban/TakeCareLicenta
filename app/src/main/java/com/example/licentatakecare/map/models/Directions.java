package com.example.licentatakecare.map.models;

import com.google.android.gms.maps.model.LatLng;

public class Directions {
        private LatLng origin;
        private LatLng destination;

        public Directions(LatLng origin, LatLng destination) {
            this.origin = origin;
            this.destination = destination;
        }

        public LatLng getOrigin() {
            return origin;
        }

        public void setOrigin(LatLng origin) {
            this.origin = origin;
        }

        public LatLng getDestination() {
            return destination;
        }

        public void setDestination(LatLng destination) {
            this.destination = destination;
        }
}

