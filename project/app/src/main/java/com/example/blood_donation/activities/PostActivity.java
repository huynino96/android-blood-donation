package com.example.blood_donation.activities;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blood_donation.R;
import com.example.blood_donation.model.CustomUser;
import com.example.blood_donation.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    ProgressDialog pd;

    EditText text1, text2;
    Spinner spinner1, spinner2;
    Button btnpost;

    FirebaseDatabase fdb;
    DatabaseReference db_ref;
    FirebaseAuth mAuth;

    Calendar cal;
    String uid;
    String Time, Date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        getSupportActionBar().setTitle("Post Blood Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text1 = findViewById(R.id.getMobile);
        text2 = findViewById(R.id.getLocation);

        spinner1 = findViewById(R.id.SpinnerBlood);
        spinner2 = findViewById(R.id.SpinnerDivision);

        btnpost = findViewById(R.id.postbtn);

        cal = Calendar.getInstance();

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);

        month+=1;
        Time = "";
        Date = "";
        String ampm="AM";

        if(cal.get(Calendar.AM_PM) ==1)
        {
            ampm = "PM";
        }

        if(hour<10)
        {
            Time += "0";
        }
        Time += hour;
        Time +=":";

        if(min<10) {
            Time += "0";
        }

        Time +=min;
        Time +=(" "+ampm);

        Date = day+"/"+month+"/"+year;

        FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();

        if(cur_user == null)
        {
            startActivity(new Intent(PostActivity.this, Login.class));
        } else {
            uid = cur_user.getUid();
        }

        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance();
        db_ref = fdb.getReference("posts");

        String[] bloodGroup = getResources().getStringArray(R.array.Blood_Group);
        String[] division_list = getResources().getStringArray(R.array.division_list);

        Intent intent = getIntent();
        if (intent.getExtras() != null){
            text1.setText(intent.getStringExtra("Contact"));
            text2.setText(intent.getStringExtra("Address"));
            spinner1.setSelection(Arrays.asList(bloodGroup).indexOf(intent.getStringExtra("BloodGroup")));
            spinner2.setSelection(Arrays.asList(division_list).indexOf(intent.getStringExtra("ChosenDivion")));
            Date = intent.getStringExtra("Date");
            Time = intent.getStringExtra("Time");
        }

        try {
            btnpost.setOnClickListener(v -> {
                pd.show();
                final Query findname = fdb.getReference("users").child(uid);

                if(text1.getText().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Enter your contact number!",
                            Toast.LENGTH_LONG).show();
                }
                else if(text2.getText().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Enter your location!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    findname.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                db_ref.child(uid).child("Name").setValue(dataSnapshot.getValue(User.class).getName());
                                db_ref.child(uid).child("Contact").setValue(text1.getText().toString());
                                db_ref.child(uid).child("Address").setValue(text2.getText().toString());
                                db_ref.child(uid).child("Division").setValue(spinner2.getSelectedItem().toString());
                                db_ref.child(uid).child("BloodGroup").setValue(spinner1.getSelectedItem().toString());
                                db_ref.child(uid).child("Time").setValue(Time);
                                db_ref.child(uid).child("Date").setValue(Date);
                                Toast.makeText(PostActivity.this, "Your post has been created successfully",
                                        Toast.LENGTH_LONG).show();
                                startActivity(new Intent(PostActivity.this, Dashboard.class));
                                pd.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "Database error.",
                                        Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("User", databaseError.getMessage());

                        }
                    });
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        pd.dismiss();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
