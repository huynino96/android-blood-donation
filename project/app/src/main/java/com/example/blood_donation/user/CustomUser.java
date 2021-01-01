package com.example.blood_donation.user;

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
    private String name, bloodGroup;
    private String time, date;


}
