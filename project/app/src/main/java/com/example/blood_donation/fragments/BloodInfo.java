package com.example.blood_donation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.blood_donation.R;

// Simple infographic about compatible blood types/health requirements
public class BloodInfo extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.blood_info_appbar, container, false);
        getActivity().setTitle("Donor Eligibility");
        return view;
    }
}

