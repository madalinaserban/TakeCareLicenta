package com.example.licentatakecare.Authentication;

public class User {
    String first_name;
    String last_name;
    String mail;
    String password;
    String age;
    String bloodType;
    String gender;
    String card_id;

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public User(String first_name, String last_name, String mail, String password, String age, String bloodType, String gender) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.mail = mail;
        this.password = password;
        this.age = age;
        this.bloodType = bloodType;
        this.gender = gender;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public String getAge() {
        return age;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getGender() {
        return gender;
    }
}
