package com.benevolenceinc.chargealaram.utils;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.Constraints.Builder;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by Hitesh on 01-02-2018.
 */

public class ScheduleJob {
    public static void startJobDispatcherService(Context context) {
        Constraints constraints = new Constraints.Builder()
                .build();

        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(ServiceForNotification.class, 3, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();
        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("alarm-job", ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, request);
    }
}



