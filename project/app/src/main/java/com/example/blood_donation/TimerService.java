package com.example.blood_donation;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.blood_donation.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {
    private static final String CHANNEL_ID = "1";
    private String lastDonate;
    private Date currentDate;
    private Date lastDonateDate;
    private SimpleDateFormat sdf;
    private final Timer t = new Timer();
    // Check date every 120 seconds
    private final int DATE_CHECK_INTERVAL = 120;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            lastDonate = intent.getStringExtra("lastDonate");
            Log.d("Service Intent: ", "Last Donate" + lastDonate);
            sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Log.d(getApplicationContext() + "", "Timer task started");
                        currentDate = Calendar.getInstance().getTime();
                        lastDonateDate = sdf.parse(lastDonate);
                        long diffMilli = Math.abs(currentDate.getTime() - lastDonateDate.getTime());
                        long diff = TimeUnit.DAYS.convert(diffMilli, TimeUnit.MILLISECONDS);
                        Log.d(getApplicationContext() + "", "Date after last donate: " + diff);
                        if (diff >= 120) {
                            String title = "Blood donation countdown";
                            String context = "Notification context";
                            int NOTIFICATION_ID = 2000;
//                            Toast.makeText(getApplicationContext(), "It has been more than 10 days since your last blood donation.", Toast.LENGTH_SHORT).show();
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.notification_icon)
                                    .setContentTitle(title)
                                    .setContentText(context)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                            Log.d(getApplicationContext() + "","Notification called");
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }, 0, DATE_CHECK_INTERVAL * 1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        t.cancel();
        sendNotification();
        Toast.makeText(this, "Cool down time has ended. You may proceed blood donating again", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Blood Point")
                .setContentText("Cool down time has ended. You may proceed blood donating again")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Cool down has ended"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }


}
