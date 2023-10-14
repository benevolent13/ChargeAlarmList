package com.benevolenceinc.chargealaram.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.benevolenceinc.chargealaram.R;
import com.benevolenceinc.chargealaram.database.DatabaseHelper;
import com.benevolenceinc.chargealaram.model.Data;
import com.benevolenceinc.chargealaram.utils.DialogHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Hitesh on 3/7/19.
 */
public class AddAlarmActivity extends AppCompatActivity {
    String percentage;
    private DatabaseHelper databaseHelper;
    private List<Data> list = new ArrayList<>();
    private String message;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_layout);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Create a full screen content callback.
        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                mInterstitialAd = null;
                // Proceed to the next level.
                save();
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

        databaseHelper = new DatabaseHelper(AddAlarmActivity.this);
        list = databaseHelper.getData();

        final DecimalFormat formatter = new DecimalFormat("00");
        final DecimalFormat formatter100 = new DecimalFormat("000");
        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.floatingButton);
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setValue(50);
        percentage = formatter.format(50);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(newVal == 100){
                    percentage = formatter100.format(newVal);
                }else{
                    percentage = formatter.format(newVal);
                }

            }
        });

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(AddAlarmActivity.this);
                } else {
                    save();
                }
            }
        });
    }

    private void save() {
        if (submit(Integer.parseInt(percentage))) {
            DialogHelper.showTwoButtonDialog("You will be notified at " + percentage + "%", AddAlarmActivity.this, new DialogHelper.OnYesClickListner() {
                @Override
                public void onYes() {
                    Data data = new Data();
                    data.setStatus(1);
                    data.setPercentage(Integer.parseInt(percentage));
                    data.setOnce_notified(13);
                    databaseHelper.setValue(data);
                    startActivity(new Intent(AddAlarmActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });
        } else {
            DialogHelper.showOneButtonDialog(AddAlarmActivity.this, message);
        }
    }

    boolean submit(int percentage) {
        boolean isReturn = true;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPercentage() == percentage) {
                isReturn = false;
                message = "You have already set alarm at \n" + percentage + "%";
            }
        }
        if (list.size() > 15) {
            isReturn = false;
            message = "Alarm list can not exceed 15";
        }
        return isReturn;
    }

}
