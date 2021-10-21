package com.isi.isicashierlibrary;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

public class SendDataToIsiCashier {

    public static void sendBill(Bill bill, Activity activity){

        Intent myIntent = new Intent();
        myIntent.setClassName("com.isi.isicashier", "com.isi.isicashier.MainActivity");
        Gson gson = new Gson();
        String jsonString = gson.toJson(bill);
        myIntent.putExtra("data", jsonString);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivityForResult(myIntent, RequestCode.reqCode);

    }

    public static void sendBill(Bill bill, Fragment activity){

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

    public static void sendProcutsNoUI(Bill bill, Activity activity, Payment.PAYMENT_TYPE paymentType){

        Intent myIntent = new Intent();
        myIntent.setClassName("com.isi.isicashier", "com.isi.isicashier.invisibleActivity.PaymentAutoActivity");
        Gson gson = new Gson();
        String jsonString = gson.toJson(bill.getProducts());
        myIntent.putExtra("data", jsonString);
        myIntent.putExtra("payment_type", paymentType.toString());
        activity.startActivityForResult(myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), RequestCode.reqCode);

    }


}
