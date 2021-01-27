package com.example.blood_donation.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.blood_donation.activities.Dashboard;

public class InternetConnectorReceiver extends BroadcastReceiver {
    public InternetConnectorReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            boolean isVisible = MyApplication.isActivityVisible();// Check if
            // activity
            // is
            // visible
            // or not
            Log.i("Activity is Visible ", "Is activity visible : " + isVisible);

            // If it is visible then trigger the task else do nothing
            if (isVisible) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager
                        .getActiveNetworkInfo();

                // Check internet connection and according to state change the
                // text of activity by calling method
                if (networkInfo != null && networkInfo.isConnected()) {

                    new Dashboard().changeTextStatus(true);
                } else {
                    new Dashboard().changeTextStatus(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
