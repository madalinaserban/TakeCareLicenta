package com.example.licentatakecare.map.models;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.licentatakecare.map.util.HospitalsCallback;
import com.example.licentatakecare.map.util.ESection;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Hospital {
    private String name;
    private GeoPoint geoPoint;
    private String id;
    private List<Section> sections;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
