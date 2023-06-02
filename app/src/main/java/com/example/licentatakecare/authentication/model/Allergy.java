package com.example.licentatakecare.authentication.model;

import com.example.licentatakecare.authentication.userData.helper.AllergyType;

import java.util.Locale;

public class Allergy {

    private String name;
    private AllergyType type;

    public Allergy(String name, String type) {
        this.name = name;
        this.type = AllergyType.valueOf(type.toUpperCase(Locale.ROOT));
    }

    public String getName() {
        return name;
    }

    public AllergyType getType() {
        return type;
    }
}


