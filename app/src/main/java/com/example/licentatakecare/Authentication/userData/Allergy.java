package com.example.licentatakecare.Authentication.userData;

import com.example.licentatakecare.Authentication.userData.Helper.AllergyType;

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


