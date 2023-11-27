package com.isi.isicashierlibrary.printer.brand.epson;

import android.util.Log;
import android.util.Xml;

import com.isi.isiapi.classes.isicash.IsiCashBill;
import com.isi.isiapi.classes.isicash.IsiCashElementBill;
import com.isi.isicashierlibrary.printer.exceptions.InputException;
import com.isi.isicashierlibrary.printer.classes.SearchCustomerResponse;
import com.isi.isicashierlibrary.printer.classes.ElementBillQuantity;
import com.isi.isicashierlibrary.printer.classes.PaymentType;
import com.isi.isicashierlibrary.printer.classes.RepartoQuantity;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class XMLWriter {

    public static String CLEAR() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("http://schemas.xmlsoap.org/soap/envelope/", "s:Envelope");
        serializer.startTag("", "s:Body");
        serializer.startTag("", "printerCommand");
        serializer.startTag("", "clearText");
        serializer.endTag("", "clearText");
        serializer.endTag("", "printerCommand");
        serializer.endTag("", "s:Body");
        serializer.endTag("http://schemas.xmlsoap.org/soap/envelope/", "s:Envelope");

        serializer.endDocument();

        Log.e("TAG", "CLEAR: " + writer.toString());

        return writer.toString();

    }

    public static String UPDATE() throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();

    }

    public static String PAPER_AHEAD() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();

    }
    public static String CREATE_BILL(List<IsiCashElementBill> billProducts, String paymentType, IsiCashBill bill, int howManyTicket, float valorTicket, String lotteryCode) throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "s:Envelope");
        serializer.attribute("", "xmlns:s", "http://schemas.xmlsoap.org/soap/envelope/");
        serializer.startTag("", "s:Body");
        serializer.startTag("", "printerFiscalReceipt");
        serializer.startTag("", "beginFiscalReceipt");
        serializer.endTag("", "beginFiscalReceipt");

        float total = 0;

        for (final IsiCashElementBill billProduct : billProducts) {

            float priceFloat = billProduct.quantity * billProduct.price;

            if (billProduct.discount_valor != 0) {

                if (billProduct.discount_type == 0) {

                    priceFloat -= billProduct.discount_valor;

                } else {

                    priceFloat = priceFloat - (priceFloat * billProduct.discount_valor) / 100;

                }

            }

            total += priceFloat;

        }

        for (IsiCashElementBill billProduct : billProducts) {
            if (billProduct.price != 0) {

                serializer.startTag("", "printRecItem");
                serializer.attribute("", "operator", "1");
                serializer.attribute("", "description", billProduct.name);
                serializer.attribute("", "quantity", String.format(Locale.getDefault(), "%d", billProduct.quantity));
                serializer.attribute("", "unitPrice", String.format(Locale.ENGLISH, "%.2f", billProduct.price));
                serializer.attribute("", "department", String.format(Locale.getDefault(), "%d", billProduct.department));
                serializer.attribute("", "justification", "1");
                serializer.endTag("", "printRecItem");

                if (billProduct.discount_valor != 0) {

                    float valor = 0;
                    switch (billProduct.discount_type) {
                        case 1:
                            valor = (billProduct.quantity * billProduct.price / 100) * billProduct.discount_valor;
                            break;
                        case 0:
                            valor = billProduct.discount_valor;
                            break;
                        default:
                            break;
                    }

                    serializer.startTag("", "printRecItemAdjustment");
                    serializer.attribute("", "operator", "1");
                    serializer.attribute("", "description", "Sconto");
                    serializer.attribute("", "adjustmentType", "1");
                    serializer.attribute("", "amount", String.format(Locale.ENGLISH, "%.2f", valor));
                    serializer.attribute("", "department", String.format(Locale.getDefault(), "%d", billProduct.department));
                    serializer.attribute("", "justification", "1");
                    serializer.endTag("", "printRecItemAdjustment");
                }
            }
        }

        if (lotteryCode != null) {
            if (!lotteryCode.equals("") && lotteryCode.length() == 8) {
                serializer.startTag("", "printRecLotteryID");
                serializer.attribute("", "operator", "1");
                serializer.attribute("", "code", lotteryCode);
                serializer.endTag("", "printRecLotteryID");
            }
        }

        if (bill.discount_valor != 0) {

            if (bill.discount_type == 1) {

                float discount = total * bill.discount_valor / 100;

                serializer.startTag("", "printRecSubtotalAdjustment");
                serializer.attribute("", "operator", "1");
                serializer.attribute("", "adjustmentType", "2");
                serializer.attribute("", "description", "Sconto");
                serializer.attribute("", "amount", String.format(Locale.getDefault(), "%.2f", discount));
                serializer.attribute("", "justification", "1");
                serializer.endTag("", "printRecSubtotalAdjustment");

                total -= discount;
            } else {
                serializer.startTag("", "printRecSubtotalAdjustment");
                serializer.attribute("", "operator", "1");
                serializer.attribute("", "adjustmentType", "2");
                serializer.attribute("", "description", "Sconto");
                serializer.attribute("", "amount", String.format(Locale.getDefault(), "%.2f", bill.discount_valor));
                serializer.attribute("", "justification", "1");
                serializer.endTag("", "printRecSubtotalAdjustment");

                total -= bill.discount_valor;
            }

        }

        String payment;

        switch (paymentType){
            case "T1":
                payment = "0";
                break;
            case "T2":
                payment = "5";
                break;
            case "T7":
                payment = "4";
                break;
            default:
                payment = "2";

        }


        if (howManyTicket != 0) {

            serializer.startTag("", "printRecTotal");
            serializer.attribute("", "operator", "1");
            serializer.attribute("", "payment", String.format(Locale.getDefault(), "%.2f", howManyTicket * valorTicket));
            serializer.attribute("", "paymentType", "3");
            serializer.attribute("", "index", "");
            serializer.attribute("", "justification", "1");
            serializer.endTag("", "printRecSubtotalAdjustment");

            if(howManyTicket * valorTicket - total != 0){
                serializer.startTag("", "printRecTotal");
                serializer.attribute("", "operator", "1");
                serializer.attribute("", "payment", String.format(Locale.getDefault(), "%.2f", howManyTicket * valorTicket - total));
                serializer.attribute("", "paymentType", payment);
                serializer.attribute("", "index", "");
                serializer.attribute("", "justification", "1");
                serializer.endTag("", "printRecTotal");
            }
        }else{

            serializer.startTag("", "printRecTotal");
            serializer.attribute("", "operator", "");
            serializer.attribute("", "payment", String.format(Locale.getDefault(), "%.2f", total));
            serializer.attribute("", "paymentType", payment);
            serializer.attribute("", "index", "");
            serializer.attribute("", "justification", "1");
            serializer.endTag("", "printRecTotal");
        }


        serializer.startTag("", "endFiscalReceipt");
        serializer.endTag("", "endFiscalReceipt");
        serializer.endTag("", "printerFiscalReceipt");
        serializer.endTag("", "s:Body");
        serializer.endTag("", "s:Envelope");
        serializer.endDocument();


        return writer.toString();

    }

    public static String SET_IVA(String numberIva, String valor) throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();


    }

    public static String SUBTOTAL_PRINTED(ArrayList<IsiCashElementBill> billProducts) throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();

    }

    public static String ANNULLO() throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();

    }

    public static String INTESTAZIONE(ArrayList<String> strings) throws IOException, InputException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();

    }

    public static String MESSAGGIO_PUBBLICITARIO(String message) throws IOException, InputException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();

    }
    public static String CHIUSURA_GIORNALIERA() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "s:Envelope");
        serializer.attribute("", "xmlns:s", "http://schemas.xmlsoap.org/soap/envelope/");
        serializer.startTag("", "s:Body");

        serializer.startTag("", "printerFiscalReport");
        serializer.startTag("", "printZReport");
        serializer.attribute("", "operator", "1");
        serializer.attribute("", "timeout", "30000");
        serializer.endTag("", "printZReport");
        serializer.endTag("", "printerFiscalReport");

        serializer.endTag("", "s:Body");
        serializer.endTag("", "s:Envelope");


        return writer.toString();

    }

    public static String MESSAGGIO_CORTESIA(String msg) throws IOException, InputException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();

    }

    public static String SET_TOTALI() throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();


    }

    public static String SET_REPARTI(String reparto, String aliquota, int beniServizi) throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();


    }

    public static String GET_DATA_E_ORA() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();


    }

    public static String SET_DATA_E_ORA() throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();


    }

    public static String VERIFICA_RESO(String data, int chiusura, int progressivo) throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();

    }

    public static String RESO(String data, int chiusura, int progressivo, float valor, int reparto) throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();

    }
    public static String RESO_TOTALE(String data, int chiusura, int progressivo, String matricola) throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "s:Envelope");
        serializer.attribute("", "xmlns:s", "http://schemas.xmlsoap.org/soap/envelope/");
        serializer.startTag("", "s:Body");

        serializer.startTag("", "printerFiscalReceipt");
        serializer.startTag("", "printRecMessage");
        serializer.attribute("", "operator", "1");
        serializer.attribute("", "message", String.format(Locale.getDefault(), "VOID %04d %04d %s %s", chiusura, progressivo, data, matricola));
        serializer.endTag("", "printRecMessage");
        serializer.endTag("", "printerFiscalReceipt");

        serializer.endTag("", "s:Body");
        serializer.endTag("", "s:Envelope");
        serializer.endDocument();

        return writer.toString();

    }

    public static String PARZIALE() throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();

    }

    public static String PRESENT() throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();

    }

    //ADDED by FRADESA

    public static String DISPLAY_MESSAGE(String riga1, String riga2) throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public static String DEPOSITO(Float euro) throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public static String PRELIEVO(Float euro) throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public static String OPEN_DRAWER() throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    //comando stampa integrale della memoria di riepilogo (solo in Z)
    public static String PRINT_ALL_MEMORY() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    //comando stampa memoria di riepilogo tra data (/&ggmmaa) e data(/[ggmmaa
    public static String PRINT_MEMORY_BETWEEN(Date d1, Date d2) throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    //comando stampa totale corrispettivi della memoria di riepilogo da data (/&ggmmaa) a data (/[ggmmaa)
    public static String TOTALE_CORRISPETTIVI_TRA(Date d1, Date d2) throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    //operatore
    public static String SET_OPERATOR(int i) throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    //apertura/chiusura documento gestionale
    //se passi TRUE taglia la carta in testa e in coda al documento
    public static String GESTIONAL_DOCUMENT(boolean cut) throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    //comando di chiusura del documento di vendita se è attivata l’”Opzione fidelity”
    public static String CLOSE_WITH_FIDELITY() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    //stampa codice alfanumerico (massimo 25 caratteri, 32 in modalità documento gestionale )
    public static String ALPHANUMERIC_PRINT(String message, boolean big) throws IOException, InputException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    //imposta la chiave selezionata
    public static String SET_KEY(KEY_TYPE key) throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public enum KEY_TYPE {
        LOCK,
        REG,
        X,
        Z,
        PRG,
        SRV
    }

    //libera prezzo
    public static String FREE_PRICE() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public static String DAILY_REPARTI() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public static String DAILY_FINANZIARI() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public static String DAILY_IVA() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public static String DGFE_FROM_TO(Date d1, Date d2) throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public static String CORRISPETTIVI_FROM_TO(Date d1) throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public static String PENDING_FILES() throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public static String SEND_PENDING_FILE() throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public static String PRINT_FATTURA(List<IsiCashElementBill> products, String paymentType, String recoverCode, SearchCustomerResponse customer, IsiCashBill bill) throws IOException {


        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();

    }

    public static String PRINT_REPORT(String date, float total, ArrayList<ElementBillQuantity> elements, ArrayList<PaymentType> paymentTypes, ArrayList<RepartoQuantity> repartoQuantities) throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

    public static String MATRICOLA() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.endDocument();

        return writer.toString();
    }

}
