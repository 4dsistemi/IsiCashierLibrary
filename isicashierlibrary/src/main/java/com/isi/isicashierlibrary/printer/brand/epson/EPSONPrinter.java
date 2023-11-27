package com.isi.isicashierlibrary.printer.brand.epson;

import android.content.Context;

import com.isi.isiapi.classes.FiscalPrinter;
import com.isi.isiapi.classes.isicash.IsiCashBill;
import com.isi.isiapi.classes.isicash.IsiCashElementBill;
import com.isi.isicashierlibrary.printer.PrinterInterface;
import com.isi.isicashierlibrary.printer.brand.ERROR_TYPE;
import com.isi.isicashierlibrary.printer.classes.CategoryQuantity;
import com.isi.isicashierlibrary.printer.classes.ElementBillQuantity;
import com.isi.isicashierlibrary.printer.classes.PaymentType;
import com.isi.isicashierlibrary.printer.classes.ReceiptReturn;
import com.isi.isicashierlibrary.printer.classes.RepartoQuantity;
import com.isi.isicashierlibrary.printer.classes.SearchCustomerResponse;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EPSONPrinter implements PrinterInterface {

    private Context context;

    @Override
    public boolean init(Context c) {
        context = c;
        return false;
    }

    @Override
    public boolean aheadPaper() {
        return false;
    }

    @Override
    public boolean setIntestazione(ArrayList<String> rows, FiscalPrinter printer) {
        return false;
    }

    @Override
    public boolean test() {
        return false;
    }

    @Override
    public ReceiptReturn printReceipt(List<IsiCashElementBill> products, String payment, IsiCashBill bill, int howManyTicket, float valorTicket, String lotteryCode) {
        SendCommand sendCommand = new SendCommand();
        try {
            return sendCommand.sendCommandReceipt(XMLWriter.CREATE_BILL(products, payment,bill, howManyTicket, valorTicket, lotteryCode), context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ReceiptReturn(ERROR_TYPE.ERRORE_CONNESSIONE, 0,0);
    }

    @Override
    public boolean chiusura() {

        SendCommand comm = new SendCommand();

        try {
            return ERROR_TYPE.NESSUN_ERRORE == comm.sendCommand(XMLWriter.CHIUSURA_GIORNALIERA(), context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void oepnDrawer() {

    }

    @Override
    public void printDGFE(Calendar c, Calendar end) {

    }

    @Override
    public boolean setIva(float[] ivas, FiscalPrinter printer) {
        return false;
    }

    @Override
    public boolean setReparto(String rep, String iva, FiscalPrinter printer, int beneServizi) {
        return false;
    }

    @Override
    public boolean setFooter(ArrayList<String> lines, FiscalPrinter printer) {
        return false;
    }

    @Override
    public void messageDisplay(String message1, String message2) {

    }

    @Override
    public void pressCl() {
        SendCommand command = new SendCommand();
        try {
            command.sendCommand(XMLWriter.CLEAR(), context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void partial() {

    }

    @Override
    public void printFattura(List<IsiCashElementBill> products, String paymentType, String recoverCode, SearchCustomerResponse customer, IsiCashBill bill) {

    }

    @Override
    public void printReport(String date, float total, ArrayList<ElementBillQuantity> elements, ArrayList<PaymentType> paymentTypes, ArrayList<RepartoQuantity> repartoQuantities, ArrayList<CategoryQuantity> repartoCategories) {

    }

    @Override
    public String getMatricola(FiscalPrinter printer) {
        return null;
    }

    @Override
    public void printFiscalmemory(Calendar c) {

    }

    @Override
    public void updateCash() {

    }

    @Override
    public boolean verificaReso(String date, int chiusura, int progressivo) {
        return false;
    }

    @Override
    public boolean resoTotale(Date date, int chiusura, int progressivo, String matricola) {

        String dateString = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(date);

        SendCommand command = new SendCommand();
        try {
            return command.sendCommand(XMLWriter.RESO_TOTALE(dateString, chiusura, progressivo, matricola), context) == ERROR_TYPE.NESSUN_ERRORE;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean resoParziale(String date, int chiusura, int progressivo, float valor, int rep) {
        return false;
    }

    @Override
    public boolean annullo() {
        return false;
    }
}
