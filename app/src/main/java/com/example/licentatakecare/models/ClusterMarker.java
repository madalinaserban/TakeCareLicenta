package com.example.licentatakecare.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.licentatakecare.Hospital;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;
    private final int mNumAvailablePlaces;

    public ClusterMarker(double lat, double lng, String title, String snippet, int numAvailablePlaces) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mNumAvailablePlaces = numAvailablePlaces;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSnippet() {
        return mSnippet;
    }

    public int getNumAvailablePlaces() {
        return mNumAvailablePlaces;
    }
}

