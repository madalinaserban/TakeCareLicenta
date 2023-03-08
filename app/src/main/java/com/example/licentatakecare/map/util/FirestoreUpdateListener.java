package com.example.licentatakecare.map.util;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.licentatakecare.map.models.Hospital;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import com.google.firebase.firestore.EventListener;
import java.util.List;
public class FirestoreUpdateListener {

    private final HospitalsCallback mCallback;
    private final CollectionReference mHospitalsRef;
    private ListenerRegistration mRegistration;

    public FirestoreUpdateListener(HospitalsCallback callback) {
        mCallback = callback;
        mHospitalsRef = FirebaseFirestore.getInstance().collection("hospitals");
    }

    public void startListening() {
        mRegistration = mHospitalsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("FirestoreUpdateListener", "onEvent: Error getting hospitals", error);
                    return;
                }

                List<Hospital> hospitals = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Hospital hospital = document.toObject(Hospital.class);
                    if (hospital != null) {
                        hospitals.add(hospital);
                    }
                }
                if (mCallback != null) {
                    mCallback.onHospitalsRetrieved(hospitals);
                }
            }
        });
    }

    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }
    }
}






