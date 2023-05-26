package com.example.licentatakecare.map.util.timeAndDistance;

import com.google.gson.annotations.SerializedName;


public class DistanceMatrixResponse {
    @SerializedName("rows")
    public DistanceMatrixRow[] rows;

    // Add other necessary fields as per the API response structure
}

