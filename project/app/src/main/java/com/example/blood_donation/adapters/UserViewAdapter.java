package com.example.blood_donation.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blood_donation.R;
import com.example.blood_donation.model.User;

import java.util.List;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.UserHolder>{
    private List<User> userList;

    public static class UserHolder extends RecyclerView.ViewHolder
    {
        TextView name, bloodgroup, address, contact, division, donorStat;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userName);
            contact = itemView.findViewById(R.id.userContact);
            bloodgroup = itemView.findViewById(R.id.userBG);
            address = itemView.findViewById(R.id.userAddress);
            division = itemView.findViewById(R.id.userDivision);
            donorStat = itemView.findViewById(R.id.donorStatus);
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

        return new UserViewAdapter.UserHolder(listItem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int i) {
        User userData = userList.get(i);
        holder.name.setText("Name: "+userData.getName());
        holder.contact.setText(userData.getContact());
        holder.address.setText("Address: "+userData.getAddress());
        holder.bloodgroup.setText(Integer.toString(userData.getBloodGroup()));
        holder.division.setText(Integer.toString(userData.getDivision()) );
//        holder.donorStat.setText("Last Donation: "+userData.getLastDonate());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
