package com.example.blood_donation.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blood_donation.R;
import com.example.blood_donation.adapters.SearchDonorAdapter;
import com.example.blood_donation.model.Donor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// Search for donors of desired blood type
public class SearchDonor extends Fragment implements SearchDonorAdapter.OnSearchDonorListener{

    private View view;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase fdb;
    DatabaseReference db_ref, user_ref;

    Spinner bloodGroup, division;
    Button btnSearch;
    ProgressDialog pd;
    List<Donor> donorItem;

    private RecyclerView recyclerView;

    private SearchDonorAdapter sdadapter;

    public SearchDonor() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.search_donor_fragment, container, false);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        fdb = FirebaseDatabase.getInstance();
        db_ref = fdb.getReference("donors");

        bloodGroup = view.findViewById(R.id.btngetBloodGroup);
        division = view.findViewById(R.id.btngetDivison);
        btnSearch = view.findViewById(R.id.btnSearch);

        getActivity().setTitle("Find Blood Donor");

        btnSearch.setOnClickListener((View.OnClickListener) v -> {
            pd.show();
            donorItem = new ArrayList<>();
            donorItem.clear();
            sdadapter = new SearchDonorAdapter(donorItem, this);
            recyclerView = (RecyclerView) view.findViewById(R.id.showDonorList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            RecyclerView.LayoutManager searchDonor = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(searchDonor);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(sdadapter);
            Query qpath  = db_ref.child(division.getSelectedItem().toString())
                    .child(bloodGroup.getSelectedItem().toString());
            qpath.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        for(DataSnapshot singleItem : dataSnapshot.getChildren())
                        {
                            Donor donorData = singleItem.getValue(Donor.class);
                            donorItem.add(donorData);
                            sdadapter.notifyDataSetChanged();
                        }
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
            pd.dismiss();
        });
        return view;
    }

    @Override
    public void OnSearchDonorClick(int position) {
        Donor donor = donorItem.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Call "+ donor.getContact() + "?")
                .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:"+donor.getContact()));startActivity(callIntent);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }


}
