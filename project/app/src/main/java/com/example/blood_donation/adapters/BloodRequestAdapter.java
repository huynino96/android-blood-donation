package com.example.blood_donation.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blood_donation.R;
import com.example.blood_donation.model.CustomUser;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;



public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.PostHolder> {


    private List<CustomUser> postLists;
    private OnConfigPost mConfigPost;
    private Boolean isAdmin;

    public static class PostHolder extends RecyclerView.ViewHolder
    {
        TextView Name, bloodgroup, Address, contact, posted;
        ImageButton editBtn, deleteBtn;
        OnConfigPost configPost;
        CustomUser customUser;

        public PostHolder(@NonNull View itemView, OnConfigPost configPost) {
            super(itemView);


            Name = itemView.findViewById(R.id.reqstUser);
            contact = itemView.findViewById(R.id.targetCN);
            bloodgroup = itemView.findViewById(R.id.targetBG);
            Address = itemView.findViewById(R.id.reqstLocation);
            posted = itemView.findViewById(R.id.posted);
            editBtn = itemView.findViewById(R.id.edit_button);
            deleteBtn = itemView.findViewById(R.id.delete_button);

            this.configPost = configPost;
        }

    }


    public BloodRequestAdapter(List<CustomUser> postLists, OnConfigPost onConfigPost, Boolean isAdmin)
    {
        this.postLists = postLists;
        this.mConfigPost = onConfigPost;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View listitem = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.request_list_item, viewGroup, false);

        return new PostHolder(listitem, mConfigPost);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PostHolder postHolder, int i) {

        String cur_ID = FirebaseAuth.getInstance().getUid();

        if(i%2==0)
        {
            postHolder.itemView.setBackgroundColor(Color.parseColor("#C13F31"));
        }
        else
        {
            postHolder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        CustomUser customUserData = postLists.get(i);
        postHolder.Name.setText("Posted by: "+customUserData.getName());
        postHolder.Address.setText("From: "+customUserData.getAddress()+", "+customUserData.getDivision());
        postHolder.bloodgroup.setText("Needs "+customUserData.getBloodGroup());
        postHolder.posted.setText("Posted on:"+customUserData.getTime()+", "+customUserData.getDate());
        postHolder.contact.setText(customUserData.getContact());
        postHolder.customUser = customUserData;

        if (customUserData.getUID().equals(cur_ID)){
            postHolder.editBtn.setVisibility(View.VISIBLE);
            postHolder.deleteBtn.setVisibility(View.VISIBLE);
        }

        if (isAdmin){
            postHolder.deleteBtn.setVisibility(View.VISIBLE);
        }

        postHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postHolder.editBtn.setVisibility(View.GONE);
                postHolder.deleteBtn.setVisibility(View.GONE);
                postHolder.configPost.onDeletePost(i);
            }
        });

        postHolder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postHolder.configPost.onEditPost(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }

    public interface OnConfigPost{
        void onEditPost(int position);
        void onDeletePost(int position);
    }
}
