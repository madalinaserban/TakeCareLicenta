package com.example.licentatakecare.map;

import com.example.licentatakecare.models.Hospital;

import java.util.List;

public interface HospitalsCallback {
        void onHospitalsRetrieved(List<Hospital> hospitals);

}
