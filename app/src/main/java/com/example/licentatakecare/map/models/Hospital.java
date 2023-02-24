package com.example.licentatakecare.map.models;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.licentatakecare.map.util.HospitalsCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private int empty_spots;
    private int cardiology;
    private int emergency;
    private int radiology;

    public Hospital() {
        // Required empty constructor for Firebase
    }

    public int getCardiology() {
        return cardiology;
    }

    public void setCardiology(int cardiology) {
        this.cardiology = cardiology;
    }

    public int getEmergency() {
        return emergency;
    }

    public void setEmergency(int emergency) {
        this.emergency = emergency;
    }

    public int getRadiology() {
        return radiology;
    }

    public void setRadiology(int radiology) {
        this.radiology = radiology;
    }

    public Hospital(String name, GeoPoint geoPoint, int empty_spots, int cardiology, int emergency, int radiology) {
        this.name = name;
        this.geoPoint = geoPoint;
        this.empty_spots = cardiology+emergency+radiology;
        this.cardiology = cardiology;
        this.emergency = emergency;
        this.radiology = radiology;
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

    public int getEmpty_spots() {
        return empty_spots;
    }

    public void setEmpty_spots(int empty_spots) {
        this.empty_spots = empty_spots;
    }

    public static void getHospitalList(final HospitalsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference hospitalsRef = db.collection("hospitals");

        hospitalsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Hospital> hospitals = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Hospital hospital = document.toObject(Hospital.class);
                    hospitals.add(hospital);
                }
                callback.onHospitalsRetrieved(hospitals);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error getting hospital list", e);
                // If the operation fails, call the onHospitalsRetrieved method with an empty list
                callback.onHospitalsRetrieved(new ArrayList<Hospital>());
            }
        });
    }
}
