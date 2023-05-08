package com.example.licentatakecare.map.models.hospital;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpeningHours {
    @SerializedName("open_now")
    @Expose
    private boolean openNow;

    public boolean isOpenNow() {
        return openNow;
    }
}
