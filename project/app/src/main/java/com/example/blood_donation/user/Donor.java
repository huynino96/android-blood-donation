package com.example.blood_donation.user;

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
    private String lastDonate, name, phone, UID, address;
}
