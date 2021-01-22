package com.example.blood_donation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {
    private String lastDonate;
    private Date currentDate;
    private Date lastDonateDate;
    private SimpleDateFormat sdf;
    private final Timer t = new Timer();
    private int DATE_CHECK_INTERVAL = 60;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lastDonate = intent.getExtras().get("lastDonate") + "";
        sdf = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    currentDate = Calendar.getInstance().getTime();
                    lastDonateDate = sdf.parse(lastDonate);
                    long diffMilli = Math.abs(currentDate.getTime() - lastDonateDate.getTime());
                    long diff = TimeUnit.DAYS.convert(diffMilli, TimeUnit.MILLISECONDS);

                    if (diff > 10) {
                        // Notification build
                        Toast.makeText(TimerService.this, "Difference is > 10 days", Toast.LENGTH_SHORT).show();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }, 0, DATE_CHECK_INTERVAL * 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        t.cancel();
        Toast.makeText(this, "Cool down time has ended. You may proceed blood donating again", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
