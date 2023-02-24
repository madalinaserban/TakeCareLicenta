package com.example.licentatakecare.map.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private final LatLng mPosition;
    private final String mTitle;
    private String mSnippet;

    public void setmNumAvailablePlaces(int mNumAvailablePlaces) {
        this.mNumAvailablePlaces = mNumAvailablePlaces;
    }

    private  int mNumAvailablePlaces;

    public ClusterMarker(double lat, double lng, String title, String snippet,int numAvailablePlaces) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mNumAvailablePlaces = numAvailablePlaces;
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


}

