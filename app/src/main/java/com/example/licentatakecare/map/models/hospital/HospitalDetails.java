package com.example.licentatakecare.map.models.hospital;

import com.example.licentatakecare.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HospitalDetails {

    @SerializedName("result")
    @Expose
    private Result result;

    public Result getResult() {
        return result;
    }

    public class Result {
        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("formatted_address")
        @Expose
        private String formattedAddress;

        @SerializedName("formatted_phone_number")
        @Expose
        private String formattedPhoneNumber;

        @SerializedName("website")
        @Expose
        private String website;

        @SerializedName("rating")
        @Expose
        private double rating;

        @SerializedName("opening_hours")
        @Expose
        private OpeningHours openingHours;

        @SerializedName("photos")
        @Expose
        private List<Photo> photos;

        public String getName() {
            return name;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public String getFormattedPhoneNumber() {
            return formattedPhoneNumber;
        }

        public String getWebsite() {
            return website;
        }

        public double getRating() {
            return rating;
        }

        public boolean isOpenNow() {
            return openingHours != null && openingHours.isOpenNow();
        }

        public String getPhotoUrl() {
            if (photos != null && photos.size() > 0) {
                return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photos.get(0).getPhotoReference() +
                        "&key=AIzaSyDHanKxsZf-dj1MjStUejStPo7XoqpIQWo";
            }
            return null;
        }
    }
}




