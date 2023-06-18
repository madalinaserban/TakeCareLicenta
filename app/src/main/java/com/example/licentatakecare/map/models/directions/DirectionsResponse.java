package com.example.licentatakecare.map.models.directions;

import com.example.licentatakecare.map.models.directions.model.Route;

import java.util.List;

public class DirectionsResponse {

    public List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
