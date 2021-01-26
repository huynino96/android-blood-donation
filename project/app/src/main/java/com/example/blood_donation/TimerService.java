package com.example.blood_donation;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

// Notify user after blood donation cooldown has expired
public class TimerService extends Service {
    private static final String CHANNEL_ID = "1";
    private String lastDonate;
    private Date currentDate;
    private Date lastDonateDate;
    private SimpleDateFormat sdf;
    private final Timer t = new Timer();
    private final int DATE_DIFFERENT = 120;
    // Check date every 86400 seconds (1 day)
    private final int DATE_CHECK_INTERVAL = 86400;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            lastDonate = intent.getStringExtra("lastDonate");
            Log.d("Service Intent: ", "Last Donation" + lastDonate);
            sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            t.scheduleAtFixedRate(new TimerTask() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    try {
                        Log.d(getApplicationContext() + "", "Timer task started");
                        currentDate = Calendar.getInstance().getTime();
                        lastDonateDate = sdf.parse(lastDonate);
                        long diffMilli = Math.abs(currentDate.getTime() - lastDonateDate.getTime());
                        long diff = TimeUnit.DAYS.convert(diffMilli, TimeUnit.MILLISECONDS);
                        Log.d(getApplicationContext() + "", "Date after last donation: " + diff);
                        if (diff >= DATE_DIFFERENT) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                sendNotification();
                            }
                            else{
                                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(TimerService.this, "My Notification")
                                        .setContentTitle("Blood Point")
                                        .setContentText("It has been more 120 days since your last blood donation.\nYou may proceed to donate again.")
                                        .setSmallIcon(R.mipmap.blood_bank_icon_round);

                                manager.notify(1, builder.build());
                            }
                            Log.d(getApplicationContext() + "", "Notification called");
                        }


                    } catch (ParseException | NullPointerException e) {
                        e.printStackTrace();
                    }

                }
            }, 0, DATE_CHECK_INTERVAL * 1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        t.cancel();
        sendNotification();
        Toast.makeText(this, "Cool down time has ended. You may donate blood again.", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String id = "myServiceChannel";
        CharSequence name = "Notification channel";
        String description = "Notification channel for services";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name,importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        notificationManager.createNotificationChannel(mChannel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.blood_bank_icon_round)
                .setContentTitle("Blood Point")
                .setContentText("It has been more 120 days since your last blood donation.\nYou may proceed to donate again.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(id);

        notificationManager.notify(1, builder.build());
    }
}
