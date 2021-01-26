package com.example.blood_donation.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.example.blood_donation.activities.PostActivity;
import com.example.blood_donation.adapters.BloodRequestAdapter;
import com.example.blood_donation.model.CustomUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeView extends Fragment implements BloodRequestAdapter.OnConfigPost {

    private DatabaseReference donor_ref;
    FirebaseAuth mAuth;
    private BloodRequestAdapter restAdapter;
    private List<CustomUser> postLists;
    private ProgressDialog pd;
    private Boolean isAdmin ;

    public HomeView() {
        isAdmin = false;
    }

    public HomeView(Boolean isAdminHome){
        isAdmin = isAdminHome;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_view_fragment, container, false);
        RecyclerView recentPosts = (RecyclerView) view.findViewById(R.id.recyleposts);

        recentPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        donor_ref = FirebaseDatabase.getInstance().getReference();
        postLists = new ArrayList<>();

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        getActivity().setTitle("Blood Point");

        restAdapter = new BloodRequestAdapter(postLists, this, isAdmin);
        RecyclerView.LayoutManager pmLayout = new LinearLayoutManager(getContext());
        recentPosts.setLayoutManager(pmLayout);
        recentPosts.setItemAnimator(new DefaultItemAnimator());
        recentPosts.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recentPosts.setAdapter(restAdapter);

        AddPosts();
        return view;

    }
    private void AddPosts()
    {
        Query allposts = donor_ref.child("posts");
        pd.show();
        allposts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    for (DataSnapshot singlepost : dataSnapshot.getChildren()) {
                        CustomUser customUserData = singlepost.getValue(CustomUser.class);
                        assert customUserData != null;
                        customUserData.setUID(singlepost.getKey());
                        Log.d("TAG", "onDataChange: " + customUserData.getUID());
                        postLists.add(customUserData);
                        restAdapter.notifyDataSetChanged();
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onEditPost(int position) {
        Log.d("TAG", "onEditPost: ");
        Intent intent = new Intent(getActivity(), PostActivity.class);
        intent.putExtra("Contact", postLists.get(position).getContact());
        intent.putExtra("Address", postLists.get(position).getAddress());
        intent.putExtra("BloodGroup", postLists.get(position).getBloodGroup());
        intent.putExtra("ChosenDivion", postLists.get(position).getDivision());
        intent.putExtra("Date", postLists.get(position).getDate());
        intent.putExtra("Time", postLists.get(position).getTime());
        startActivity(intent);
    }

    @Override
    public void onDeletePost(int position) {
        Query allposts = donor_ref.child("posts");
        pd.show();
        allposts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    for (DataSnapshot singlepost : dataSnapshot.getChildren()) {
                        if (singlepost.getKey().equals(postLists.get(position).getUID())){
                            singlepost.getRef().removeValue();
                        }
                    }
                    pd.dismiss();
                    postLists.remove(position);
                    restAdapter.notifyDataSetChanged();
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
