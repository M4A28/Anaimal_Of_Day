package com.mohammed.animaloftoday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        boolean isBootCompleted = intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED);
        boolean isTimeChanged = intent.getAction().equals(Intent.ACTION_TIME_CHANGED);
        boolean isPhoneOnline = Utility.isOnline(context);
        boolean isOkTime = calendar.get(Calendar.HOUR) % 6 == 0;

        if (isOkTime && isTimeChanged && isPhoneOnline || isBootCompleted)
            Utility.randomAnimal(context);
    }

}