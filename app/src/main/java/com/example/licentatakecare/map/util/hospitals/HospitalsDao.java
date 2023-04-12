package com.example.licentatakecare.map.util.hospitals;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.licentatakecare.map.models.hospital.Hospital;
import com.example.licentatakecare.map.models.hospital.Section;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HospitalsDao {
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
                    // sections for the current hospital
                    CollectionReference sectionsRef = hospitalsRef.document(document.getId()).collection("sections");

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
                            callback.onHospitalsRetrieved(hospitals);
                            Log.d(TAG,"OnHospitalsRetrieved");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error getting sections for hospital " + document.getId(), e);
                        }
                    });

                    sectionsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.e(TAG, "Error getting sections for hospital " + document.getId(), e);
                                return;
                            }

                            if (queryDocumentSnapshots != null) {
                                List<Section> sections = new ArrayList<>();
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Section section = document.toObject(Section.class);
                                    section.setId(document.getId());
                                    sections.add(section);
                                }
                                hospital.setSections(sections);
                                callback.onHospitalUpdated(hospital);
                                Log.d(TAG,"OnHospitalUpdated");
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error getting hospital list", e);
                callback.onHospitalsRetrieved(new ArrayList<Hospital>());
            }
        });
    }

}
