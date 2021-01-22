package com.example.blood_donation.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blood_donation.R;
import com.example.blood_donation.adapters.UserViewAdapter;
import com.example.blood_donation.model.CustomUser;
import com.example.blood_donation.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserList extends Fragment {
    private DatabaseReference user_ref;
    FirebaseAuth mAuth;
    private UserViewAdapter userAdapter;
    private List<User> userList;
    private ProgressDialog pd;

    public UserList(){

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_view_fragment, container, false);
        RecyclerView userView = (RecyclerView) view.findViewById(R.id.user_view);

        userView.setLayoutManager(new LinearLayoutManager(getContext()));

        user_ref = FirebaseDatabase.getInstance().getReference();
        userList = new ArrayList<>();

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        getActivity().setTitle("Blood Point");

        userAdapter = new UserViewAdapter(userList);
        RecyclerView.LayoutManager pmLayout = new LinearLayoutManager(getContext());
        userView.setLayoutManager(pmLayout);
        userView.setItemAnimator(new DefaultItemAnimator());
        userView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        userView.setAdapter(userAdapter);

        getUser();
        return view;
    }

    private void getUser(){
        Query allposts = user_ref.child("users");
        pd.show();
        allposts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    for (DataSnapshot singlepost : dataSnapshot.getChildren()) {
                        User customUserData = singlepost.getValue(User.class);
                        userList.add(customUserData);
                        userAdapter.notifyDataSetChanged();
                    }
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText(getActivity(), "Database is empty now!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d("User", databaseError.getMessage());

            }
        });
    }
}
