package com.example.blood_donation.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private String name;
    private String email;
    private String phone;
    private String address;
    private int gender;
    private int bloodGroup;
    private int division;
}
