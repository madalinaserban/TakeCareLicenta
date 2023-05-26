package com.example.licentatakecare.map.util.timeAndDistance;

import com.google.gson.annotations.SerializedName;

public class DistanceMatrixRow {
    // Define the fields representing the distance and duration information
    // based on the API response structure
    // For example:
    @SerializedName("elements")
    public DistanceMatrixElement[] elements;

    // Add other necessary fields as per the API response structure
}
