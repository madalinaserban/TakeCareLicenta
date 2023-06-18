package com.example.licentatakecare.map.models.hospital;

import com.example.licentatakecare.map.util.clusters.ESection;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class Hospital implements Comparable<Hospital> {
    private String name;
    private GeoPoint geoPoint;
    private String id;
    private String google_id;
    private String timeToGetThere;
    private HospitalDetails hospitalDetails;
    private List<Section> sections;
    private double distance;
    public String getGoogle_id() {
        return google_id;
    }

    public HospitalDetails getHospitalDetails() {
        return hospitalDetails;
    }

    public void setHospitalDetails(HospitalDetails hospitalDetails) {
        this.hospitalDetails = hospitalDetails;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimeToGetThere() {
        return timeToGetThere;
    }

    public void setTimeToGetThere(String timeToGetThere) {
        this.timeToGetThere = timeToGetThere;
    }


    public Hospital() {
        // Required empty constructor for Firebase
    }

    public Hospital(String name, GeoPoint geoPoint) {
        this.name = name;
        this.geoPoint = geoPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }


    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public int getAvailability(ESection eSection) {
        int availability = 0;
        if (!(eSection.name().equals("ALL"))) {
            for (Section section : sections) {
                if (section.getName().equals(eSection.toString()))
                    availability = section.getAvailability();
            }
        } else {
            for (Section section : sections) {
                availability += section.getAvailability();
            }
        }
        return availability;
    }
    public int getTotalSpaces(ESection eSection) {
        int total_beds = 0;
        if (!(eSection.name().equals("ALL"))) {
            for (Section section : sections) {
                if (section.getName().equals(eSection.toString()))
                    total_beds = section.getTotal_spaces();
            }
        } else {
            for (Section section : sections) {
                total_beds += section.getTotal_spaces();
            }
        }
        return total_beds;
    }
    @Override
    public int compareTo(Hospital otherHospital) {
        return Double.compare(distance, otherHospital.distance);
    }


}
