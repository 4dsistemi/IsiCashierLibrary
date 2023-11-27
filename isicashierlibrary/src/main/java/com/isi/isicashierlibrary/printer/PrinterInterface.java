package com.isi.isicashierlibrary.printer;

import android.content.Context;
import com.isi.isiapi.classes.FiscalPrinter;
import com.isi.isiapi.classes.isicash.IsiCashBill;
import com.isi.isiapi.classes.isicash.IsiCashElementBill;
import com.isi.isicashierlibrary.printer.classes.CategoryQuantity;
import com.isi.isicashierlibrary.printer.classes.ElementBillQuantity;
import com.isi.isicashierlibrary.printer.classes.PaymentType;
import com.isi.isicashierlibrary.printer.classes.ReceiptReturn;
import com.isi.isicashierlibrary.printer.classes.RepartoQuantity;
import com.isi.isicashierlibrary.printer.classes.SearchCustomerResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public interface PrinterInterface {
    boolean init(Context var1);

    boolean aheadPaper();

    boolean setIntestazione(ArrayList<String> var1, FiscalPrinter var2);

    boolean test();

    ReceiptReturn printReceipt(List<IsiCashElementBill> var1, String var2, IsiCashBill var3, int var4, float var5, String var6);

    boolean chiusura();

    void oepnDrawer();

    void printDGFE(Calendar var1, Calendar var2);

    boolean setIva(float[] var1, FiscalPrinter var2);

    boolean setReparto(String var1, String var2, FiscalPrinter var3, int var4);

    boolean setFooter(ArrayList<String> var1, FiscalPrinter var2);

    void messageDisplay(String var1, String var2);

    void pressCl();

    void partial();

    void printFattura(List<IsiCashElementBill> var1, String var2, String var3, SearchCustomerResponse var4, IsiCashBill var5);

    void printReport(String var1, float var2, ArrayList<ElementBillQuantity> var3, ArrayList<PaymentType> var4, ArrayList<RepartoQuantity> var5, ArrayList<CategoryQuantity> var6);

    String getMatricola(FiscalPrinter var1);

    void printFiscalmemory(Calendar var1);

    void updateCash();

    boolean verificaReso(String var1, int var2, int var3);

    boolean resoTotale(Date var1, int var2, int var3, String var4);

    boolean resoParziale(String var1, int var2, int var3, float var4, int var5);

    boolean annullo();
}
