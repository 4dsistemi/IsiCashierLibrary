package com.isi.isicashierlibrary.printer.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.isi.isiapi.classes.FiscalPrinter;

public class FiscalPrinterPreferences {
    public FiscalPrinterPreferences() {
    }

    public static void changeFiscalprinterPreferences(Context c, FiscalPrinter printer) {
        SharedPreferences prefs = c.getSharedPreferences("printer", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fiscal_printer", (new Gson()).toJson(printer));
        editor.apply();
    }

    public static FiscalPrinter getFiscalprinterPreferences(Context c) {
        SharedPreferences prefs = c.getSharedPreferences("printer", 0);
        String printer = prefs.getString("fiscal_printer", (String)null);
        return printer == null ? null : (FiscalPrinter)(new Gson()).fromJson(printer, FiscalPrinter.class);
    }
}

