package com.example.licentatakecare.map.util.TimeandDistance;

import com.google.gson.annotations.SerializedName;
import com.google.maps.model.Distance;
import com.google.maps.model.Duration;


public class DistanceMatrixResponse {
    @SerializedName("rows")
    public DistanceMatrixRow[] rows;

    // Add other necessary fields as per the API response structure
}

