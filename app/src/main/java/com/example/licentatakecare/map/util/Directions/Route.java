package com.example.licentatakecare.map.util.Directions;

import java.util.List;

public class Route implements Comparable<Route> {
    private Distance distance;

    public Distance getDistance() {
        return distance;
    }
    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public List<Leg> legs;
    @Override
    public int compareTo(Route other) {
        return this.distance.getValue() - other.getDistance().getValue();
    }
}
