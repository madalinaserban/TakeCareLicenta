package com.example.licentatakecare.map.util;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.licentatakecare.map.models.Hospital;
import com.example.licentatakecare.map.models.Section;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
                List<Task<Void>> tasks = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Hospital hospital = document.toObject(Hospital.class);
                    hospital.setId(document.getId());
                    // sections for the current hospital
                    CollectionReference sectionsRef = hospitalsRef.document(document.getId()).collection("sections");

                    Task<Void> task = sectionsRef.get().continueWith(new Continuation<QuerySnapshot, Void>() {
                        @Override
                        public Void then(@NonNull Task<QuerySnapshot> task) throws Exception {
                            if (task.isSuccessful()) {
                                List<Section> sections = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Section section = document.toObject(Section.class);
                                    section.setId(document.getId());
                                    sections.add(section);
                                }
                                hospital.setSections(sections);
                                hospitals.add(hospital);
                                Log.d("Spitale",""+hospital.getName());
                            } else {
                                Log.e(TAG, "Error getting sections for hospital " + document.getId(), task.getException());
                            }
                            return null;
                        }
                    });
                    tasks.add(task);
                }
                Tasks.whenAll(tasks).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onHospitalsRetrieved(hospitals);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting sections for hospitals", e);
                        callback.onHospitalsRetrieved(new ArrayList<Hospital>());
                    }
                });
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
