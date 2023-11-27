package com.isi.isicashierlibrary.printer.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.isi.isiapi.classes.isicash.IsiCashBillAndElements;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;

public class BillQueue {
    public BillQueue() {
    }

    public void addBill(IsiCashBillAndElements bill, Context c) {
        ArrayList<IsiCashBillAndElements> queue = this.getBillQueue(c);
        if (queue == null) {
            queue = new ArrayList();
        }

        queue.add(bill);
        SharedPreferences shared = c.getSharedPreferences("bill_queue", 0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("bill_queue", (new Gson()).toJson(queue));
        editor.apply();
    }

    public void overrideBill(ArrayList<IsiCashBillAndElements> bills, Context c) {
        SharedPreferences shared = c.getSharedPreferences("bill_queue", 0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("bill_queue", (new Gson()).toJson(bills));
        editor.apply();
    }

    public ArrayList<IsiCashBillAndElements> getBillQueue(Context c) {
        SharedPreferences shared = c.getSharedPreferences("bill_queue", 0);
        String queue = shared.getString("bill_queue", (String)null);
        return queue != null ? (ArrayList)(new Gson()).fromJson(queue, (new TypeToken<ArrayList<IsiCashBillAndElements>>() {}).getType()) : null;
    }
}
