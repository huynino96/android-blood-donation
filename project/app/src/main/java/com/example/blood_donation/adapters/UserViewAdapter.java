package com.example.blood_donation.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blood_donation.R;
import com.example.blood_donation.model.Donor;
import com.example.blood_donation.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.UserHolder>{
    private List<User> userList;
    private String[] blood_array, division_array;

    public static class UserHolder extends RecyclerView.ViewHolder
    {
        TextView name, bloodgroup, address, contact, division, lastDonate, totalDonate;
        ImageView imageView;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userName);
            contact = itemView.findViewById(R.id.userContact);
            bloodgroup = itemView.findViewById(R.id.userBG);
            address = itemView.findViewById(R.id.userAddress);
            division = itemView.findViewById(R.id.userDivision);
            lastDonate = itemView.findViewById(R.id.lastDonate);
            totalDonate = itemView.findViewById(R.id.totalDonate);
            imageView = itemView.findViewById(R.id.imageDonor);
        }
    }

    public UserViewAdapter(List<User> userList)
    {
        this.userList = userList;
    }


    @NonNull
    @Override
    public UserViewAdapter.UserHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View listItem = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_view_item, viewGroup, false);
        blood_array = viewGroup.getContext().getResources().getStringArray(R.array.Blood_Group);
        division_array = viewGroup.getContext().getResources().getStringArray(R.array.division_list);
        return new UserViewAdapter.UserHolder(listItem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int i) {
        User userData = userList.get(i);
        holder.name.setText("Name: "+userData.getName());
        holder.contact.setText(userData.getContact());
        holder.address.setText("Address: "+userData.getAddress());
        holder.bloodgroup.setText(blood_array[userData.getBloodGroup()]);
        holder.division.setText(division_array[userData.getDivision()] );
        setDonorStat(holder, userData);
    }

    private void setDonorStat(@NonNull UserHolder holder, User userData){
        DatabaseReference user_ref = FirebaseDatabase.getInstance().getReference("donors");
        Query qpath  = user_ref.child(division_array[userData.getDivision()])
                .child(blood_array[userData.getBloodGroup()]);
        qpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot singleItem : dataSnapshot.getChildren())
                    {
                        Donor donorData = singleItem.getValue(Donor.class);
                        if (userData.getUID()!= null){
                            if (userData.getUID().equals(donorData.getUID())){
                                holder.totalDonate.setText("Total donate: "+donorData.getTotalDonate());
                                holder.lastDonate.setText("Last donate: " + donorData.getLastDonate());
                                holder.imageView.setVisibility(View.VISIBLE);
                                holder.totalDonate.setVisibility(View.VISIBLE);
                                holder.lastDonate.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                else
                {
                    Log.d("TAG", "Database is empty now!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
