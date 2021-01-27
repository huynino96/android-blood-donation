package com.example.blood_donation.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blood_donation.R;
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
import com.tomer.fadingtextview.FadingTextView;

import java.util.concurrent.TimeUnit;

// Still runs even if "tomer" library is marked red
public class Login extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        // Check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            changeTextStatus(true);
        } else {
            changeTextStatus(false);
        }

        user = mAuth.getCurrentUser();
        if (user != null)
        {
            onDefiningRole(user);
        }
        else {
            initializeView();
        }
    }

    // Set up login screen
    private void initializeView(){
        inputEmail = findViewById(R.id.input_username);
        inputPassword = findViewById(R.id.input_password);

        Button signin = findViewById(R.id.button_login);
        Button signup = findViewById(R.id.button_register);
        Button resetpass = findViewById(R.id.button_forgot_password);

        // Authenticate user sign in
        signin.setOnClickListener(v -> {
            final String email = inputEmail.getText().toString()+"";
            final String password = inputPassword.getText().toString()+"";

            try {
                if(password.length()>0 && email.length()>0) {
                    pd.show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, task -> {
                                if (!task.isSuccessful()) {
                                    Log.d("TAG", "onCreate: failed" );

                                    Toast.makeText(getApplicationContext(),
                                            "Authentication Failed",
                                            Toast.LENGTH_LONG).show();
                                    Log.v("error", task.getException().getMessage());
                                    pd.dismiss();
                                } else {
                                    pd.dismiss();
                                    Log.d("TAG", "onCreate: success" );
                                    onDefiningRole(mAuth.getCurrentUser());
                                }
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

        // To Sign Up activity
        signup.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        });

        // To Reset Password activity
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
    }

    // Welcome animation
    private void onDefiningRole(FirebaseUser cur_user) {
        setContentView(R.layout.welcome_log_in);
        FadingTextView fadingUserTextView = (FadingTextView) findViewById(R.id.user_name_faded);

        FirebaseDatabase user_db = FirebaseDatabase.getInstance();
        DatabaseReference userdb_ref = user_db.getReference("users");
        Query singleUser = userdb_ref.child(cur_user.getUid());
        singleUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //pd.show();
                User user = snapshot.getValue(User.class);
                if (user != null){
                    String[] userArr = {user.getName()};
                    fadingUserTextView.setTexts(userArr);
                    fadingUserTextView.setTimeout(30, TimeUnit.MILLISECONDS);
                    //If admin show the admin site
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (user.getRole() != null) {
                                if (user.getRole().equals("admin")) {
                                    Intent intent = new Intent(getApplicationContext(), Admin.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }, 1000);
                }else {
                    setContentView(R.layout.activity_login);
                    initializeView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
