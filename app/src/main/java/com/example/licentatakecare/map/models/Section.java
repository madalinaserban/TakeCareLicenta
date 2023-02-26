package com.example.licentatakecare.map.models;

import com.example.licentatakecare.map.util.ESection;

public class Section{
    private String name;
    private int availability;
    private int total_spaces;

    public Section(){}

    public Section(String name, int availability, int total_spaces) {
        this.name = name;
        this.availability = availability;
        this.total_spaces = total_spaces;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public int getTotal_spaces() {
        return total_spaces;
    }

    public void setTotal_spaces(int total_spaces) {
        this.total_spaces = total_spaces;
    }
}
