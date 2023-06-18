package com.example.licentatakecare.map.util.directions.callback;

import com.example.licentatakecare.map.models.directions.model.Route;

public interface DirectionsCallback {

    void onSuccess(Route route);

    void onFailure();
}


