package com.mohammed.animaloftoday;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class CheckRecentRun extends Service {

    public CheckRecentRun() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences setting = getSharedPreferences(ConstValue.SHARED_PREF, MODE_PRIVATE);
        if (setting.getBoolean(ConstValue.ENABLED, true)) {
            if (setting.getLong(ConstValue.LAST_RUN, Long.MAX_VALUE) < System.currentTimeMillis()) {
                Utility.showSmallNotification(this, ConstValue.MISS_TITLE, ConstValue.MISS_DATA);
            }
        }
        setAlarm();
        stopSelf();
    }

    private void setAlarm() {
        Intent intent = new Intent(this, CheckRecentRun.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getService(this
                , ConstValue.REQ_CODE_FOR_ALARM
                , intent
                , PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP
                , System.currentTimeMillis() + ConstValue.DELAY
                , pendingIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}