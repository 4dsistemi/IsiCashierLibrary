package com.isi.isicashierlibrary.printer.fastPay;

import android.content.Context;

import com.isi.isiapi.classes.FiscalPrinter;
import com.isi.isicashierlibrary.printer.PrinterInterface;
import com.isi.isicashierlibrary.printer.brand.epson.EPSONPrinter;
import com.isi.isicashierlibrary.printer.brand.rch.RCHPrinter;
import com.isi.isicashierlibrary.printer.preferences.FiscalPrinterPreferences;

public class OpenDrawer {
    private final Context context;

    public OpenDrawer(Context context) {
        this.context = context;
    }

    public void open() {
        PrinterInterface p = this.getPrinter();
        if (p != null) {
            (new Thread(() -> {
                try {
                    p.oepnDrawer();
                } catch (Exception var2) {
                }

            })).start();
        }

    }

    private PrinterInterface getPrinter() {
        PrinterInterface printer = null;
        FiscalPrinter printerPref = FiscalPrinterPreferences.getFiscalprinterPreferences(this.context);
        if (printerPref != null) {
            if (printerPref.type == 0) {
                printer = new RCHPrinter();
            } else {
                printer = new EPSONPrinter();
            }

            ((PrinterInterface)printer).init(this.context);
        }

        return (PrinterInterface)printer;
    }
}
