package com.example.licentatakecare.map.util.timeAndDistance;

import com.example.licentatakecare.map.models.directions.model.Distance;
import com.example.licentatakecare.map.models.directions.model.Duration;
import com.google.gson.annotations.SerializedName;

public class DistanceMatrixElement {
    // Define the fields representing the distance and duration information
    // based on the API response structure
    // For example:
    @SerializedName("distance")
    public Distance distance;

    @SerializedName("duration")
    public Duration duration;

    // Add other necessary fields as per the API response structure
}
