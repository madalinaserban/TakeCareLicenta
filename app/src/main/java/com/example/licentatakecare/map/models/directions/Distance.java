package com.example.licentatakecare.map.models.directions;

import com.google.gson.annotations.SerializedName;

public class Distance {

    public String text;
    @SerializedName("value")
    public int value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


}
