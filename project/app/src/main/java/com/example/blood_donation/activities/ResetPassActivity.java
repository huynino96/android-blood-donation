package com.example.blood_donation.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blood_donation.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Reset Password screen
public class ResetPassActivity extends AppCompatActivity {

    EditText userEmail;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);


        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        userEmail = findViewById(R.id.resetUsingEmail);

        findViewById(R.id.resetPassbtn).setOnClickListener(v -> {

            FirebaseUser user = mAuth.getCurrentUser();

            final String email = userEmail.getText().toString();

            if(TextUtils.isEmpty(email))
            {
                userEmail.setError("Email required!");
            }
            else
            {
                // Set confirmation email after reset password attempt
                pd.show();
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "We have sent an email to "+" '"+ email +"'. Please check your email.", Toast.LENGTH_LONG)
                                        .show();
                                startActivity(new Intent(getApplicationContext(), Login.class));
                                //userEmail.setText(null);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Sorry, There is something went wrong. please try again some time later.", Toast.LENGTH_LONG)
                                        .show();
                                userEmail.setText(null);
                            }
                            pd.dismiss();
                        });
            }
        });
    }
}
