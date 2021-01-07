package com.example.blood_donation.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class CustomUser implements Serializable {
    private String address;
    private String division;
    private String phone;
    private String name;
    private String bloodGroup;
    private String time;
    private String date;

    public CustomUser(){

    }

    public CustomUser(String address, String division, String phone, String name, String bloodGroup, String time, String date) {
        this.address = address;
        this.division = division;
        this.phone = phone;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.time = time;
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
