package com.example.licentatakecare.map.util;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.licentatakecare.map.models.Hospital;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import com.google.firebase.firestore.EventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreUpdateListener {

    private final HospitalsCallback mCallback;
    private final CollectionReference mHospitalsRef;
    private Map<String, ListenerRegistration> mListeners = new HashMap<>();

    public FirestoreUpdateListener(HospitalsCallback callback) {
        mCallback = callback;
        mHospitalsRef = FirebaseFirestore.getInstance().collection("hospitals");
    }

    public void startListening() {
        for (ESection section : ESection.values()) {
            ListenerRegistration registration = mHospitalsRef.whereEqualTo("sections." + section.name() + ".availability", true)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                            Log.d("MapsActivity", "onHospitalsModified called");
                            if (error != null) {
                                Log.e("FirestoreUpdateListener", "onEvent: Error getting hospitals for section " + section.name(), error);
                                return;
                            }

                            List<Hospital> addedHospitals = new ArrayList<>();
                            List<Hospital> modifiedHospitals = new ArrayList<>();
                            List<Hospital> removedHospitals = new ArrayList<>();

                            for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {
                                Hospital hospital = documentChange.getDocument().toObject(Hospital.class);
                                hospital.setId(documentChange.getDocument().getId());

                                switch (documentChange.getType()) {
                                    case ADDED:
                                        addedHospitals.add(hospital);
                                        break;
                                    case MODIFIED:
                                        modifiedHospitals.add(hospital);
                                        break;
                                    case REMOVED:
                                        removedHospitals.add(hospital);
                                        break;
                                }
                            }

                            if (mCallback != null) {
                                if (!addedHospitals.isEmpty()) {
                                    //  mCallback.onHospitalsAdded(addedHospitals);
                                }
                                if (!modifiedHospitals.isEmpty()) {
                                  //  mCallback.onHospitalsModified(modifiedHospitals);
                                }
                                if (!removedHospitals.isEmpty()) {
                                    //  mCallback.onHospitalsRemoved(removedHospitals);
                                }
                            }
                        }
                    });
            mListeners.put(section.name(), registration);
        }
    }

    public void stopListening() {
        for (ListenerRegistration listener : mListeners.values()) {
            listener.remove();
        }
        mListeners.clear();
    }
}
