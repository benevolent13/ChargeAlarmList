package com.benevolenceinc.chargealaram.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.benevolenceinc.chargealaram.utils.ScheduleJob;

/**
 * Created by Hitesh on 01-02-2018.
 */

public class StartReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ScheduleJob.startJobDispatcherService(context);
    }
}
