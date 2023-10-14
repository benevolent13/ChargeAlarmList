package com.benevolenceinc.chargealaram.utils;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.benevolenceinc.chargealaram.R;
import com.benevolenceinc.chargealaram.activity.MainActivity;

/**
 * Created by Hitesh on 3/13/19.
 */
public class DialogHelper {
    public static void showOneButtonDialog(Context context, String message){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.one_button_dialog, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        TextView tvYes = (TextView)dialogView.findViewById(R.id.tvYes);
        TextView tvMessage = (TextView)dialogView.findViewById(R.id.tvMessage);
        tvMessage.setText(message);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    public static void showTwoButtonDialog(String message,Context context, final OnYesClickListner onYesClickListner){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.two_button_dialog, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        TextView tvYes = (TextView)dialogView.findViewById(R.id.tvYes);
        TextView tvMessage = (TextView)dialogView.findViewById(R.id.tvMessage);
        TextView tvNo = (TextView)dialogView.findViewById(R.id.tvNo);

        final SpannableString text = new SpannableString(message);
        text.setSpan(new RelativeSizeSpan(1.5f), text.length()-4, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new ForegroundColorSpan(Color.RED), text.length()-4, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvMessage.setText(text);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onYesClickListner.onYes();
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

    public interface OnYesClickListner{
        void onYes();
    }
}
