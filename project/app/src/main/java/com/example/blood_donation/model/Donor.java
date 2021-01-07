package com.example.blood_donation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class Donor {
    private int totalDonate;
    private String lastDonate;
    private String name;
    private String phone;
    private String UID;
    private String address;

    public Donor(){

    }
    public Donor(int totalDonate, String lastDonate, String name, String phone, String UID, String address) {
        this.totalDonate = totalDonate;
        this.lastDonate = lastDonate;
        this.name = name;
        this.phone = phone;
        this.UID = UID;
        this.address = address;
    }

    public int getTotalDonate() {
        return totalDonate;
    }

    public void setTotalDonate(int totalDonate) {
        this.totalDonate = totalDonate;
    }

    public String getLastDonate() {
        return lastDonate;
    }

    public void setLastDonate(String lastDonate) {
        this.lastDonate = lastDonate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
