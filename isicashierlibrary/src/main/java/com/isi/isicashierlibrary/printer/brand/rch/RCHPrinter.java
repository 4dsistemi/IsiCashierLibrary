package com.isi.isicashierlibrary.printer.brand.rch;

import android.content.Context;

import com.isi.isiapi.classes.FiscalPrinter;
import com.isi.isiapi.classes.isicash.IsiCashBill;
import com.isi.isiapi.classes.isicash.IsiCashElementBill;
import com.isi.isicashierlibrary.printer.exceptions.InputException;
import com.isi.isicashierlibrary.printer.classes.SearchCustomerResponse;
import com.isi.isicashierlibrary.printer.PrinterInterface;
import com.isi.isicashierlibrary.printer.classes.ReceiptReturn;
import com.isi.isicashierlibrary.printer.brand.ERROR_TYPE;
import com.isi.isicashierlibrary.printer.classes.CategoryQuantity;
import com.isi.isicashierlibrary.printer.classes.ElementBillQuantity;
import com.isi.isicashierlibrary.printer.classes.PaymentType;
import com.isi.isicashierlibrary.printer.classes.RepartoQuantity;
import com.isi.sunmipaxxprinterlibrary.SunmiPaxxPrinter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RCHPrinter implements PrinterInterface {

    private Context context;

    @Override
    public boolean init(Context c) {
        this.context = c;
        SunmiPaxxPrinter.getInstance(c);
        return true;
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
    public void updateCash() {
        SendCommand command = new SendCommand();
        try {
            command.sendCommand(XMLWriter.UPDATE(), context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean verificaReso(String date, int chiusura, int progressivo) {
        SendCommand command = new SendCommand();
        try {
            return command.sendCommand(XMLWriter.VERIFICA_RESO(date, chiusura, progressivo), context) == ERROR_TYPE.NESSUN_ERRORE;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean resoTotale(Date date, int chiusura, int progressivo, String matricola) {

        String dateString = new SimpleDateFormat("ddMMyy", Locale.getDefault()).format(date);

        SendCommand command = new SendCommand();
        try {
            return command.sendCommand(XMLWriter.RESO_TOTALE(dateString, chiusura, progressivo), context) == ERROR_TYPE.NESSUN_ERRORE;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean resoParziale(String date, int chiusura, int progressivo, float valor, int rep) {
        SendCommand command = new SendCommand();
        try {
            return command.sendCommand(XMLWriter.RESO(date, chiusura, progressivo, valor, rep), context) == ERROR_TYPE.NESSUN_ERRORE;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean annullo() {
        SendCommand command = new SendCommand();
        try {
            return command.sendCommand(XMLWriter.ANNULLO(), context) == ERROR_TYPE.NESSUN_ERRORE;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean aheadPaper() {
        try {
            new SendCommand().sendCommand(XMLWriter.PAPER_AHEAD(), context);
            return true;
        } catch (IOException e) {
            return false;
        }

    }

    @Override
    public boolean setIntestazione(ArrayList<String> rows, FiscalPrinter printer) {
        SendCommand command = new SendCommand();
        try {
            command.sendCommandReturn(XMLWriter.INTESTAZIONE(rows), context, printer.ip, 10000);
            return true;
        } catch (IOException | InputException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean test() {
        return false;
    }

    @Override
    public ReceiptReturn printReceipt(List<IsiCashElementBill> products, String payment, IsiCashBill bill, int howManyTicket, float valor, String lotteryCode) {
        SendCommand sendCommand = new SendCommand();
        try {
            return sendCommand.sendCommandReceipt(XMLWriter.CREATE_BILL(products, payment,bill, howManyTicket, valor, lotteryCode), context);
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
        try {
            new SendCommand().sendCommand(XMLWriter.OPEN_DRAWER(), context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setIva(float[] ivas, FiscalPrinter printer) {

        try{
            SendCommand command = new SendCommand();
            command.sendCommandReturn(XMLWriter.SET_KEY(XMLWriter.KEY_TYPE.PRG), context, printer.ip, 10000);
            for (int i = 0; i < 5; i++) {
                command.sendCommandReturn(XMLWriter.SET_IVA(String.format(Locale.getDefault(),"%d" ,i+1), String.format(Locale.getDefault(), "%.2f", ivas[i])), context, printer.ip, 10000);
            }
            command.sendCommandReturn(XMLWriter.SET_KEY(XMLWriter.KEY_TYPE.REG), context, printer.ip, 10000);

            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public boolean setReparto(String rep, String iva, FiscalPrinter printer, int beniServizi) {

        try{
            SendCommand command = new SendCommand();

            command.sendCommandReturn(XMLWriter.SET_KEY(XMLWriter.KEY_TYPE.PRG), context, printer.ip, 10000);
            String result = command.sendCommandReturn(XMLWriter.SET_REPARTI(rep, iva, beniServizi), context, printer.ip, 10000);
            command.sendCommandReturn(XMLWriter.SET_KEY(XMLWriter.KEY_TYPE.REG), context, printer.ip, 10000);
        }catch (Exception e){
            return false;
        }

        return true;
    }

    @Override
    public void printDGFE(Calendar c, Calendar end) {
        SendCommand command = new SendCommand();

        try {
            command.sendCommand(XMLWriter.SET_KEY(XMLWriter.KEY_TYPE.Z), context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            command.sendCommand(XMLWriter.DGFE_FROM_TO(c.getTime(), end.getTime()), context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            command.sendCommand(XMLWriter.SET_KEY(XMLWriter.KEY_TYPE.REG), context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setFooter(ArrayList<String> lines, FiscalPrinter printer) {

        //TODO

        return false;
    }

    @Override
    public void partial() {
        SendCommand command = new SendCommand();
        try {
            command.sendCommand(XMLWriter.PARZIALE(), context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printFattura(List<IsiCashElementBill> products, String paymentType, String recoverCode, SearchCustomerResponse customer, IsiCashBill bill) {
        SendCommand command = new SendCommand();
        try {
            command.sendCommand(XMLWriter.PRINT_FATTURA(products, paymentType, recoverCode, customer, bill), context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printReport(String date, float total, ArrayList<ElementBillQuantity> elements, ArrayList<PaymentType> paymentTypes, ArrayList<RepartoQuantity> repartoQuantities, ArrayList<CategoryQuantity> repartoCategories) {
        SendCommand command = new SendCommand();
        try {
            command.sendCommand(XMLWriter.PRINT_REPORT(date, total, elements, paymentTypes, repartoQuantities, repartoCategories), context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getMatricola(FiscalPrinter printer) {
        SendCommand command = new SendCommand();
        try {
            return command.sendCommandReturn(XMLWriter.MATRICOLA(), context, printer.ip, 200);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void printFiscalmemory(Calendar c) {

        SendCommand command = new SendCommand();

        try {
            command.sendCommand(XMLWriter.SET_KEY(XMLWriter.KEY_TYPE.Z), context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            command.sendCommand(XMLWriter.PRINT_MEMORY_BETWEEN(c.getTime(), c.getTime()), context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            command.sendCommand(XMLWriter.SET_KEY(XMLWriter.KEY_TYPE.REG), context);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void messageDisplay(String message1, String message2) {
        SendCommand command = new SendCommand();

        try {
            command.sendCommand(XMLWriter.DISPLAY_MESSAGE(message1, message2), context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
