package com.example.blood_donation.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blood_donation.R;
import com.example.blood_donation.model.Donor;

import java.util.List;



public class SearchDonorAdapter extends RecyclerView.Adapter<SearchDonorAdapter.PostHolder> {
    private List<Donor> postLists;
    private OnSearchDonorListener mDonorListener;

    public class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView Name, Address, contact, posted, totaldonate;
        OnSearchDonorListener donorListener;
        public PostHolder(@NonNull View itemView, OnSearchDonorListener donorListener) {
            super(itemView);

            Name = itemView.findViewById(R.id.donorName);
            contact = itemView.findViewById(R.id.donorContact);
            totaldonate = itemView.findViewById(R.id.totaldonate);
            Address = itemView.findViewById(R.id.donorAddress);
            posted = itemView.findViewById(R.id.lastdonate);

            this.donorListener = donorListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.donorListener.OnSearchDonorClick(getAdapterPosition());
        }
    }

    public SearchDonorAdapter(List<Donor> postLists,  OnSearchDonorListener donorListener)
    {
        this.postLists = postLists;
        this.mDonorListener = donorListener;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View listItem = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.search_donor_item, viewGroup, false);

        return new PostHolder(listItem, mDonorListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PostHolder postHolder, int i) {

        if(i%2==0)
        {
            postHolder.itemView.setBackgroundColor(Color.parseColor("#C13F31"));
        }
        else
        {
            postHolder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        Donor donorData = postLists.get(i);
        postHolder.Name.setText("Name: "+donorData.getName());
        postHolder.contact.setText(donorData.getContact());
        postHolder.Address.setText("Address: "+donorData.getAddress());
        postHolder.totaldonate.setText("Total Donation: "+donorData.getTotalDonate()+" times");
        postHolder.posted.setText("Last Donation: "+donorData.getLastDonate());
    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }

    public interface OnSearchDonorListener{
        void OnSearchDonorClick(int position);
    }
}
