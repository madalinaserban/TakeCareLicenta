package com.example.licentatakecare.map.util.TimeandDistance;

import com.example.licentatakecare.map.models.hospital.Hospital;

import java.util.List;

public interface CalculateDistancesCallback {

    void onDistancesCalculated(List<Hospital> sortedHospitals);
    void onDistancesCalculationFailed();


}
