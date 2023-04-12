package com.example.licentatakecare.map.util.hospitals;

import com.example.licentatakecare.map.models.hospital.Hospital;

import java.util.List;

public interface HospitalsCallback {
    void onHospitalsRetrieved(List<Hospital> hospitals);
    void onHospitalUpdated(Hospital hospital);
}
