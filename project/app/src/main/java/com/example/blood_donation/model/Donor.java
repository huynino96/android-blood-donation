package com.example.blood_donation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Donor {
    private int totalDonate;
    private String lastDonate;
    private String name;
    private String phone;
    private String UID;
    private String address;
}
