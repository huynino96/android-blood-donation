package com.example.blood_donation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;

import com.example.blood_donation.activities.SplashActivity;

public class SendMessageActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SMS = 99;
    private static final String MOM_PHONE_NUMBER = "090888623" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        requestPermission();
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(SendMessageActivity.this, new String[]{
                Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SMS);
    }

    private void sendMessage() {
        //get message detail
         Intent incomingIntent = getIntent();
         String text = (String) incomingIntent.getExtras().get("message");
         //sending Sms Message
         SmsManager smsManager = SmsManager.getDefault();
         smsManager.sendTextMessage(MOM_PHONE_NUMBER, null,
                 text, null, null);
         // Go back to main activity
        //Need to fix the activity
         Intent intent = new Intent(SendMessageActivity.this, SplashActivity.class);
         setResult(100, intent);
         finish();
    }
}