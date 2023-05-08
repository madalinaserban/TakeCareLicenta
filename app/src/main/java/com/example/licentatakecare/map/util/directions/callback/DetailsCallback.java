package com.example.licentatakecare.map.util.directions.callback;

public interface DetailsCallback {
    void onDetailsReceived(String name, String address, String phone, String website, double rating, boolean isOpen, String photoUrl);

    void onError(String errorMessage);
}

