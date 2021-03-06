package com.example.blood_donation.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

// Stub for airplane mode
public class AirPlaneModeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(AirPlaneModeReceiver.class.getSimpleName(), "Airplane mode");
        Toast.makeText(context.getApplicationContext(), "Airplane Mode is ON", Toast.LENGTH_LONG).show();
    }
}
