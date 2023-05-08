package com.example.licentatakecare.map.util.directions;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlacesApiResponse {

    @SerializedName("results")
    private List<Result> results;

    @SerializedName("status")
    private String status;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Result {

        @SerializedName("geometry")
        private Geometry geometry;

        @SerializedName("name")
        private String name;

        @SerializedName("vicinity")
        private String vicinity;

        public Geometry getGeometry() {
            return geometry;
        }
        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVicinity() {
            return vicinity;
        }

        public void setVicinity(String vicinity) {
            this.vicinity = vicinity;
        }
    }

    public static class Geometry {

        @SerializedName("location")
        private Location location;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }

    public static class Location {

        @SerializedName("lat")
        private double latitude;

        @SerializedName("lng")
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

}
