package com.example.blood_donation.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomUser implements Serializable {
    private String address;
    private String division;
    private String phone;
    private String name;
    private String bloodGroup;
    private String time;
    private String date;

}
