package com.example.blood_donation.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blood_donation.R;
import com.example.blood_donation.broadcast.AirPlaneModeReceiver;
import com.example.blood_donation.broadcast.MyApplication;
import com.example.blood_donation.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    private AirPlaneModeReceiver airPlaneModeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        airPlaneModeReceiver = new AirPlaneModeReceiver();

        IntentFilter filter = new IntentFilter("android.intent.action.AIRPLANE_MODE");
        registerReceiver(airPlaneModeReceiver, filter);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            changeTextStatus(true);
        } else {
            changeTextStatus(false);
        }


        if(mAuth.getCurrentUser() != null)
        {
            onDefiningRole(mAuth.getCurrentUser());
        }


        inputEmail = findViewById(R.id.input_username);
        inputPassword = findViewById(R.id.input_password);

        Button signin = findViewById(R.id.button_login);
        Button signup = findViewById(R.id.button_register);
        Button resetpass = findViewById(R.id.button_forgot_password);

        signin.setOnClickListener(v -> {

            final String email = inputEmail.getText().toString()+"";
            final String password = inputPassword.getText().toString()+"";

            try {
                if(password.length()>0 && email.length()>0) {
                    pd.show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, task -> {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),
                                            "Authentication Failed",
                                            Toast.LENGTH_LONG).show();
                                    Log.v("error", task.getException().getMessage());
                                } else {
                                    Log.d("TAG", "onCreate: success" );
                                    onDefiningRole(mAuth.getCurrentUser());
                                }
                                pd.dismiss();
                            });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please fill all the field.", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        signup.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        });

        resetpass.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ResetPassActivity.class);
            startActivity(intent);
        });


    }

    public void changeTextStatus(boolean isConnected) {

        // Change status according to boolean value
        if (isConnected) {
            Toast.makeText(getApplicationContext(), "Internet is connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "There is no internet connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        MyApplication.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {

        super.onResume();
        MyApplication.activityResumed();// On Resume notify the Application
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(airPlaneModeReceiver);
    }

    private void onDefiningRole(FirebaseUser cur_user){
        FirebaseDatabase user_db = FirebaseDatabase.getInstance();
        DatabaseReference userdb_ref = user_db.getReference("users");
        Query singleUser = userdb_ref.child(cur_user.getUid());
        singleUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //pd.show();
                User user =  dataSnapshot.getValue(User.class);
                //If admin show the admin site
                if (user.getRole() != null){
                    if (user.getRole().equals("admin")){
                        Log.d("TAG", "onDataChange: admin");
                        Intent intent = new Intent(getApplicationContext(), Admin.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Log.d("TAG", "onDataChange: member");
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                        finish();
                    }
                }
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

    }
}
