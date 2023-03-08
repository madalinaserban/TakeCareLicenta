package com.example.licentatakecare.map.models;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.licentatakecare.map.util.HospitalsCallback;
import com.example.licentatakecare.map.util.ESection;
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

    public static void getHospitalList(final HospitalsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference hospitalsRef = db.collection("hospitals");

        hospitalsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Hospital> hospitals = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Hospital hospital = document.toObject(Hospital.class);
                    hospital.setId(document.getId());

                    // Get the subcollection sections for the current hospital
                    CollectionReference sectionsRef = hospitalsRef.document(document.getId()).collection("sections");

                    // Retrieve the data from the subcollection sections
                    sectionsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<Section> sections = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Section section = document.toObject(Section.class);
                                section.setId(document.getId());
                                sections.add(section);
                            }
                            hospital.setSections(sections);
                            hospitals.add(hospital);

                            // Call the callback once all data has been retrieved
                            if (hospitals.size() == queryDocumentSnapshots.size()) {
                                callback.onHospitalsRetrieved(hospitals);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error getting sections for hospital " + document.getId(), e);
                            // If the operation fails, call the onHospitalsRetrieved method with an empty list
                            callback.onHospitalsRetrieved(new ArrayList<Hospital>());
                        }
                    });
                }
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

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public int getAvailability(ESection eSection) {
        int availability = 0;
        if (eSection.name() != "ALL") {
            for (Section section : sections) {
                if (section.getName() == eSection.toString())
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
