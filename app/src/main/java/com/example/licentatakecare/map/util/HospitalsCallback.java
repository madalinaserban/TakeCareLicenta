package com.example.licentatakecare.map.util;

import com.example.licentatakecare.map.models.Hospital;

import java.util.List;

public interface HospitalsCallback {
    void onHospitalsRetrieved(List<Hospital> hospitals);
    void onHospitalsModified(Hospital hospital);
}
