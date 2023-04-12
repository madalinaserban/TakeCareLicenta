package com.example.licentatakecare.map.models.hospital;


public class Section {
    private String id;
    private String name;
    private int availability;
    private int total_spaces;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Section() {
    }

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
