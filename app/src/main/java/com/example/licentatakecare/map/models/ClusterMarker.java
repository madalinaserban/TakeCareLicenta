package com.example.licentatakecare.map.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private final LatLng mPosition;
    private final String mTitle;
    private String mSnippet;
    private Hospital mHospital;
    private int mColor;
    private Marker marker;

    public Hospital getHospital() {
        return mHospital;
    }

    public void setHospital(Hospital hospital) {
        this.mHospital = hospital;
    }

    public void setColor(int color) {
        mColor = color;
    }


    public void setmNumAvailablePlaces(int mNumAvailablePlaces) {
        this.mNumAvailablePlaces = mNumAvailablePlaces;
    }

    private int mNumAvailablePlaces;

    public ClusterMarker(Hospital hospital, int numAvailablePlaces) {
        mPosition = new LatLng(hospital.getGeoPoint().getLatitude(), hospital.getGeoPoint().getLongitude());
        mTitle = hospital.getName();
        mSnippet = hospital.getSections().toString();
        mNumAvailablePlaces = numAvailablePlaces;
        mHospital = hospital;

    }

    public int getColor() {
        return mColor;
    }


    public void setSnippet(String mSnippet) {
        this.mSnippet = mSnippet;
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

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}

