package com.benevolenceinc.chargealaram.activity;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;


import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benevolenceinc.chargealaram.R;
import com.benevolenceinc.chargealaram.adapter.DataAdapater;
import com.benevolenceinc.chargealaram.database.DatabaseHelper;
import com.benevolenceinc.chargealaram.model.Data;
import com.benevolenceinc.chargealaram.utils.ScheduleJob;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScheduleJob.startJobDispatcherService(getApplicationContext());

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            showSettingDialog();
        } else {
            // You can directly ask for the permission.
            requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    13);
        }


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Create a full screen content callback.
        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                mInterstitialAd = null;
                // Proceed to the next level.
                Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
                startActivity(intent);
            }
        };

        InterstitialAd.load(
                this,
                getString(R.string.interstitial_id),
                new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        mInterstitialAd = ad;
                        mInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // Code to be executed when an ad request fails.
                    }
                });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        TextView textView = (TextView) findViewById(R.id.tvAddAlarm);
        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.floatingButton);

        List<Data> list = new ArrayList<>();

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        list = databaseHelper.getData();

        if (list.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }

        dataLog();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.READ_PHONE_STATE) ==
                        PackageManager.PERMISSION_GRANTED)) {
                    if (mInterstitialAd != null && canShowAd()) {
                        setPreferenceValue();
                        mInterstitialAd.show(MainActivity.this);
                    } else {
                        setPreferenceValue();
                        Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
                        startActivity(intent);
                    }
                } else {
                    showSettingDialog();
                }

            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showDialog();
            }
        });

        DataAdapater dataAdapater = new DataAdapater(list, getApplicationContext());
        recyclerView.setAdapter(dataAdapater);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    void setPreferenceValue(){
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
SharedPreferences.Editor editor = sharedPref.edit();
int value = getPreferenceValue();
value++;
editor.putInt(getString(R.string.ad_showing_key), value);
editor.apply();
    }

    int getPreferenceValue(){
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(getString(R.string.ad_showing_key), 0);
    }

    boolean canShowAd(){
        return getPreferenceValue()% 3 == 0;
    }

    void dataLog(){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);


        List<Data> list = databaseHelper.getData();
        Log.e("LIST LENGTH TIFICATION", String.valueOf(list.size()));
        for (int y = 0; y < list.size(); y++) {
            Log.e("LIST PERCENTAGE", String.valueOf(list.get(y).getPercentage()));
            Log.e("LIST STATUS", String.valueOf(list.get(y).getStatus()));
            Log.e("LIST ID", String.valueOf(list.get(y).get_id()));
            Log.e("ONCE NOTIFIED", String.valueOf(list.get(y).getOnce_notified()));}
    }

    void managePhoneNotification() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED) {

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                showSettingDialog();
            } else {
                // You can directly ask for the permission.
                requestPermissions(
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        21);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        switch (requestCode) {
            case 13:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                managePhoneNotification();
                return;
            case 21:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Other 'case' lines to check for other
        // permissions this app might request
    }


    private void showSettingDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.notification_setting_dialog, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        TextView tvYes = (TextView) dialogView.findViewById(R.id.tvYes);
        TextView tvNo = (TextView) dialogView.findViewById(R.id.tvNo);


        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.exit_dialog_box, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        TextView tvRate = (TextView) dialogView.findViewById(R.id.tvRate);
        TextView tvYes = (TextView) dialogView.findViewById(R.id.tvYes);
        TextView tvNo = (TextView) dialogView.findViewById(R.id.tvNo);
        TextView tvShare = (TextView) dialogView.findViewById(R.id.tvShare);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        tvRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                alertDialog.dismiss();

            }
        });

        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                    String sAux = "\nSet charge alarm with Charge Alarm!\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Choose to Share"));
                } catch (Exception e) {
                    //e.toString();
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }
}
