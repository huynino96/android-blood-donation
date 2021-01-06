package com.example.blood_donation.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blood_donation.R;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
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
                                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                    startActivity(intent);
                                    finish();
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

}
