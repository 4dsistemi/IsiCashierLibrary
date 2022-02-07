package com.isi.isicashierlibrary;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.isi.isiapi.classes.isicash.IsiCashBillAndElements;

public class SendDataToIsiCashier {

    public static void sendBill(IsiCashBillAndElements bill, Activity activity){

        Intent myIntent = new Intent();
        myIntent.setClassName("com.isi.isicashier", "com.isi.isicashier.MainActivity");
        Gson gson = new Gson();
        String jsonString = gson.toJson(bill);
        myIntent.putExtra("data", jsonString);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivityForResult(myIntent, RequestCode.reqCode);

    }

    public static void sendBill(IsiCashBillAndElements bill, Fragment activity){

        Intent myIntent = new Intent();
        myIntent.setClassName("com.isi.isicashier", "com.isi.isicashier.MainActivity");
        Gson gson = new Gson();
        String jsonString = gson.toJson(bill);
        myIntent.putExtra("data", jsonString);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivityForResult(myIntent, RequestCode.reqCode);

    }

    public static void openPrinterSettings(Activity activity){
        Intent myIntent = new Intent();
        myIntent.setClassName("com.isi.isicashier", "com.isi.isicashier.printerSettings.PrintersActivity");
        activity.startActivityForResult(myIntent, RequestCode.reqCode);
    }

}
