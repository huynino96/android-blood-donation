package com.example.blood_donation.model;

public class User {

    private String Name, Email, Contact, Address, Role, UID;
    private int Gender, BloodGroup, Division;

    public User() {

    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public int getDivision() {
        return Division;
    }

    public void setDivision(int division) {
        this.Division = division;
    }

    public String getName() {
        return Name;
    }

    public int getBloodGroup() {
        return BloodGroup;
    }

    public void setBloodGroup(int bloodGroup) {
        this.BloodGroup = bloodGroup;
    }

    public String getEmail() {
        return Email;
    }

    public int getGender() {
        return Gender;
    }

    public void setName(String name) { this.Name = name; }

    public void setEmail(String email) {
        this.Email = email;
    }

    public void setGender(int gender) {
        this.Gender = gender;
    }

    public String getRole() {
        return Role;
    }

    public String getUID() {
        return UID;
    }
}
