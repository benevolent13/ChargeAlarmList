package com.benevolenceinc.chargealaram.utils;


import static android.content.Context.NOTIFICATION_SERVICE;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.benevolenceinc.chargealaram.R;
import com.benevolenceinc.chargealaram.activity.MainActivity;
import com.benevolenceinc.chargealaram.database.DatabaseHelper;
import com.benevolenceinc.chargealaram.model.Data;
import com.google.common.util.concurrent.ListenableFuture;

import android.content.Context;
import androidx.work.ListenableWorker;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

/**
 * Created by Hitesh on 1/23/18.
 */

public class ServiceForNotification extends Worker {



    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public ServiceForNotification(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        getApplicationContext().registerReceiver(batteryLevelBroadcast,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return  Result.success();
    }

    @Override
    public void onStopped() {
        try {
            getApplicationContext().unregisterReceiver(batteryLevelBroadcast);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onStopped();
    }

    BroadcastReceiver batteryLevelBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            //Toast.makeText(context,String.valueOf(level),Toast.LENGTH_LONG).show();
            Log.e("LEVEL",String.valueOf(level));
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            List<Data> list = databaseHelper.getData();
            Log.e("LIST LENGTH", String.valueOf(list.size()));
            for (int i = 0; i < list.size(); i++) {
                Log.e("LIST PERCENTAGE", String.valueOf(list.get(i).getPercentage()));
                Log.e("LIST STATUS", String.valueOf(list.get(i).getStatus()));
                Log.e("LIST ID", String.valueOf(list.get(i).get_id()));
                Log.e("ONCE NOTIFIED", String.valueOf(list.get(i).getOnce_notified()));
                if(list.get(i).getStatus() == 1){
                    if(level==list.get(i).getPercentage()){
                        if(list.get(i).getOnce_notified() == 13){
                            createNotification(context,list.get(i));
                        }
                    }else if(level < list.get(i).getPercentage()){
                        list.get(i).setOnce_notified(13);
                        Log.i("LESS NOTIFIED", String.valueOf(list.get(i).getOnce_notified()));
                        databaseHelper.updateOnceNotified(list.get(i));
                    }else if(level > list.get(i).getPercentage()){
                        list.get(i).setOnce_notified(13);
                        Log.i("GREAT ONCE_NOTIFIED", String.valueOf(list.get(i).getOnce_notified()));
                        databaseHelper.updateOnceNotified(list.get(i));
                    }
                }
            }

           onStopped();

        }
    };

    private void createNotification(Context context,Data data) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        // END_INCLUDE(notificationCompat)

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence channelname = "my_channel";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;

        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(intent);
        // END_INCLUDE(intent)

        builder.setVibrate(new long[]{1000,1000});

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.drawable.alarm_icon);//change it
        // END_INCLUDE(ticker)

        // BEGIN_INCLUDE(buildNotification)
        // Cancel the notification when clicked
        builder.setAutoCancel(true);
        builder.setChannelId(CHANNEL_ID);

        builder.setSound( Uri.parse("android.resource://"
                + context.getPackageName() + "/" +R.raw.timer_beeps));

        // Build the notification
        Notification notification = builder.build();

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);

        // Set text on a TextView in the RemoteViews programmatically.
        String stringLevel = String.valueOf(data.getPercentage());
        contentView.setTextViewText(R.id.tvPercentage, stringLevel);

        notification.contentView = contentView;

        // START_INCLUDE(notify)
        // Use the NotificationManager to show the notification
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, channelname, importance);
            nm.createNotificationChannel(mChannel);
        }
        nm.notify(data.get_id(), notification);
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        data.setOnce_notified(21);
        databaseHelper.updateOnceNotified(data);
        // END_INCLUDE(notify)
    }
}
