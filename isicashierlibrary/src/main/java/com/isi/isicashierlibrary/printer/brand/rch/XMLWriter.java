package com.isi.isicashierlibrary.printer.brand.rch;

import android.util.Log;
import android.util.Xml;

import com.isi.isiapi.classes.isicash.IsiCashBill;
import com.isi.isiapi.classes.isicash.IsiCashElementBill;
import com.isi.isicashierlibrary.printer.exceptions.InputException;
import com.isi.isicashierlibrary.printer.classes.SearchCustomerResponse;
import com.isi.isicashierlibrary.printer.classes.CategoryQuantity;
import com.isi.isicashierlibrary.printer.classes.ElementBillQuantity;
import com.isi.isicashierlibrary.printer.classes.PaymentType;
import com.isi.isicashierlibrary.printer.classes.RepartoQuantity;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class XMLWriter {

    public static String CLEAR() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=K");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();


        return writer.toString();

    }

    public static String UPDATE() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C901");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();


        return writer.toString();

    }

    public static String PAPER_AHEAD() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=f");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();


        return writer.toString();

    }

    public static String CREATE_BILL(List<IsiCashElementBill> billProducts, String paymentType, IsiCashBill bill, int howManyTicket, float valorTicket, String lotteryCode) throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");

        for (IsiCashElementBill billProduct : billProducts){
            if(billProduct.price != 0){
                serializer.startTag("", "cmd");

                String price = String.format(Locale.getDefault(), "%.0f", billProduct.price *100);

                serializer.text("=R" + billProduct.department + "/$" + price  +"/*" + billProduct.quantity + "/(" + billProduct.name + ")");
                serializer.endTag("", "cmd");

                if(billProduct.discount_valor != 0){
                    serializer.startTag("", "cmd");
                    switch (billProduct.discount_type){
                        case 1:
                            serializer.text("=%/*" + billProduct.discount_valor);
                            break;
                        case 0:
                            serializer.text("=V/*" + (int)(billProduct.discount_valor*100));
                            break;
                        default: break;
                    }
                    serializer.endTag("", "cmd");
                }
            }
        }

        if(bill.discount_valor != 0){

            serializer.startTag("", "cmd");
            serializer.text("=S");
            serializer.endTag("", "cmd");

            if(bill.discount_type == 1){
                serializer.startTag("", "cmd");
                serializer.text(String.format(Locale.getDefault(), "=%%/*%.2f", bill.discount_valor));
                serializer.endTag("", "cmd");
            }else{
                serializer.startTag("", "cmd");
                serializer.text(String.format(Locale.getDefault(), "=V/*%d", (int)(bill.discount_valor * 100)));
                serializer.endTag("", "cmd");
            }

        }

        if(howManyTicket != 0){

            serializer.startTag("", "cmd");
            serializer.text("=T7/$" + (int)(howManyTicket * valorTicket)*100 + "&" + howManyTicket);
            serializer.endTag("", "cmd");

        }

        if(lotteryCode != null){
            if(!lotteryCode.equals("") && lotteryCode.length() == 8){
                serializer.startTag("", "cmd");
                serializer.text("=\"/?L/$1/(" + lotteryCode.toUpperCase() + ")");
                Log.e("TAG", "CREATE_BILL: " + "=\\\"/?L/$1/" + lotteryCode);
                serializer.endTag("", "cmd");
            }
        }


        serializer.startTag("", "cmd");
        serializer.text("=" + paymentType);
        serializer.endTag("", "cmd");


        serializer.startTag("", "cmd");
        serializer.text("=c");
        serializer.endTag("", "cmd");

        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();

    }

    public static String SET_IVA(String numberIva, String valor) throws IOException {
        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");

        serializer.startTag("", "cmd");
        serializer.text(">>/?V/$"+numberIva+"/*"+valor);
        serializer.endTag("", "cmd");

        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString().replace("&gt;", ">");


    }

    public static String SUBTOTAL_PRINTED(ArrayList<IsiCashElementBill> billProducts) throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");

        for (IsiCashElementBill billProduct : billProducts){
            serializer.startTag("", "cmd");

            String price = Float.toString((int)(billProduct.price* 100));

            serializer.text("=R/$" + price  +"/*" + billProduct.quantity + "/(" + billProduct.name + ")");
            serializer.endTag("", "cmd");

        }

        serializer.startTag("", "cmd");
        serializer.text("=S");
        serializer.endTag("", "cmd");

        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();

    }

    public static String ANNULLO() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=a");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();


        return writer.toString();

    }

    public static String INTESTAZIONE(ArrayList<String> strings) throws IOException, InputException {

        for (String s : strings)
            if (s.length() > 35) throw new InputException("The message is too long");

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");

        serializer.startTag("", "cmd");
        serializer.text("=C4");
        serializer.endTag("", "cmd");

        int i = 0;

        for(String row : strings){

            serializer.startTag("", "cmd");
            serializer.text(">>/?H/$"+ (i+1) +"/(" + row + ")");
            serializer.endTag("", "cmd");

            i++;

        }

        serializer.startTag("", "cmd");
        serializer.text("=C1");
        serializer.endTag("", "cmd");


        serializer.endTag("", "Service");
        serializer.endDocument();


        return writer.toString().replace("&gt;", ">");

    }

    public static String MESSAGGIO_PUBBLICITARIO(String message) throws IOException, InputException {

        if(message.length() > 35) throw new InputException("The message is too long");

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C4");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text(">>/?h/(" + message + ")");
        serializer.endTag("", "cmd");

        serializer.startTag("", "cmd");
        serializer.text("=C1");
        serializer.endTag("", "cmd");

        serializer.endTag("", "Service");
        serializer.endDocument();


        return writer.toString();

    }

    public static String CHIUSURA_GIORNALIERA() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C3");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=C10");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=C1");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();


        return writer.toString();

    }

    public static String MESSAGGIO_CORTESIA(String msg) throws IOException, InputException {

        if(msg.length() >= 48) throw new InputException("The message is too long");

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text(">>/?t/$1/(" + msg + ")");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();


        return writer.toString();

    }

    public static String SET_TOTALI() throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text(">T1/?A/$1/&1/[0/]1/^1/_0/(CONTANTI)");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text(">T2/?A/$0/&0/[1/]0/^0/_0/(NON RISCOSSO)");
        serializer.endTag("", "cmd");

        serializer.startTag("", "cmd");
        serializer.text(">T3/?A/$1/&1/[0/]1/^0/_0/(ASSEGNI)");
        serializer.endTag("", "cmd");

        serializer.startTag("", "cmd");
        serializer.text(">T4/?A/$0/&1/[0/]1/^0/_0/(EFT POS)");
        serializer.endTag("", "cmd");

        serializer.startTag("", "cmd");
        serializer.text(">T5/?A/$1/&1/[0/]1/^0/_1/(TICKET)");
        serializer.endTag("", "cmd");

        serializer.endTag("", "Service");
        serializer.endDocument();


        return writer.toString();


    }

    public static String SET_REPARTI(String reparto, String aliquota, int beniServizi) throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text(">R" + reparto + "/?A/$100/*" + aliquota + "/(Rep. "+ reparto +")/&500000/[50/]0/_1/@" + beniServizi);
        serializer.endTag("", "cmd");

        serializer.endTag("", "Service");
        serializer.endDocument();


        return writer.toString();


    }

    public static String GET_DATA_E_ORA() throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("<</?d");
        serializer.endTag("", "cmd");

        serializer.endTag("", "Service");
        serializer.endDocument();


        return writer.toString();


    }

    public static String SET_DATA_E_ORA() throws IOException{

        Calendar c = Calendar.getInstance();

        DateFormat format = new SimpleDateFormat("ddMMyy", Locale.getDefault());
        DateFormat hour = new SimpleDateFormat("HHmmss", Locale.getDefault());

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text(">>/?d/$" + format.format(c.getTime()) + "/*" + hour.format(c.getTime()));
        serializer.endTag("", "cmd");

        serializer.endTag("", "Service");
        serializer.endDocument();


        return writer.toString();


    }

    public static String VERIFICA_RESO(String data, int chiusura, int progressivo) throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text(String.format(Locale.getDefault(), "=r/&%s/[%d/]%d/*1", data, chiusura, progressivo));
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");

        serializer.endDocument();

        return writer.toString();

    }

    public static String RESO(String data, int chiusura, int progressivo, float valor, int reparto) throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text(String.format(Locale.getDefault(), "=r/&%s/!0/[%d/]%d", data, chiusura, progressivo));
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text(String.format(Locale.getDefault(), "=R%d/$%d", reparto, (int)(valor * 100)));
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=T1");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");

        serializer.endDocument();

        return writer.toString();

    }

    public static String RESO_TOTALE(String data, int chiusura, int progressivo) throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text(String.format(Locale.getDefault(), "=k/&%s/!0/[%d/]%d", data, chiusura, progressivo));
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");

        serializer.endDocument();

        return writer.toString();

    }

    public static String PARZIALE() throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C2");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=C10");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=C1");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        serializer.endDocument();

        return writer.toString();

    }

    public static String PRESENT() throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C453/$2");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        serializer.endDocument();

        return writer.toString();

    }

    //ADDED by FRADESA

    public static String DISPLAY_MESSAGE(String riga1, String riga2) throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=D1/(" + riga1 + ")");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=D2/(" + riga2 + ")");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    public static String DEPOSITO(Float euro) throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=e/$" + (euro/100));
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    public static String PRELIEVO(Float euro) throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=p/$" + (euro/100));
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    public static String OPEN_DRAWER() throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C86");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    //comando stampa integrale della memoria di riepilogo (solo in Z)
    public static String PRINT_ALL_MEMORY() throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C400");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    //comando stampa memoria di riepilogo tra data (/&ggmmaa) e data(/[ggmmaa
    public static String PRINT_MEMORY_BETWEEN(Date d1, Date d2) throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        DateFormat df = new SimpleDateFormat("ddMMyy", Locale.getDefault());
        String date1 = df.format(d1);
        String date2 = df.format(d2);
        serializer.text("=C401/&" + date1 + "/[" + date2);
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    //comando stampa totale corrispettivi della memoria di riepilogo da data (/&ggmmaa) a data (/[ggmmaa)
    public static String TOTALE_CORRISPETTIVI_TRA(Date d1, Date d2) throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "cmd");
        serializer.text("=c3");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        DateFormat df = new SimpleDateFormat("ddMMyy", Locale.getDefault());
        String date1 = df.format(d1);
        String date2 = df.format(d2);
        serializer.text("=c403/&" + date1 + "/[" + date2);
        serializer.endTag("", "cmd");
        serializer.endDocument();

        return writer.toString();
    }

    //operatore
    public static String SET_OPERATOR(int i) throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "cmd");
        serializer.text("=O" + i);
        serializer.endTag("", "cmd");
        serializer.endDocument();

        return writer.toString();
    }

    //apertura/chiusura documento gestionale
    //se passi TRUE taglia la carta in testa e in coda al documento
    public static String GESTIONAL_DOCUMENT(boolean cut) throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "cmd");
        if (cut){
            serializer.text("=o/*1");
        }else {
            serializer.text("=o");
        }
        serializer.endTag("", "cmd");
        serializer.endDocument();

        return writer.toString();
    }

    //comando di chiusura del documento di vendita se è attivata l’”Opzione fidelity”
    public static String CLOSE_WITH_FIDELITY() throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "cmd");
        serializer.text("=c");
        serializer.endTag("", "cmd");
        serializer.endDocument();

        return writer.toString();
    }

    //stampa codice alfanumerico (massimo 25 caratteri, 32 in modalità documento gestionale )
    public static String ALPHANUMERIC_PRINT(String message, boolean big) throws IOException, InputException {
        if (message.length() > 25) throw new InputException("The message is too long");

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "cmd");
        String s = "";
        if(big) s = "/*2";
        serializer.text("=\"/?A/(" + message + ")" +s);
        serializer.endTag("", "cmd");
        serializer.endDocument();

        return writer.toString();
    }

    //imposta la chiave selezionata
    public static String SET_KEY(KEY_TYPE key) throws IOException{

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        switch (key){
            case LOCK:
                serializer.text("=C0");
                break;
            case REG:
                serializer.text("=C1");
                break;
            case X:
                serializer.text("=C2");
                break;
            case Z:
                serializer.text("=C3");
                break;
            case PRG:
                serializer.text("=C4");
                break;
            case SRV:
                serializer.text("=C5");
                break;
            default:
                throw new IOException("invalid key argument");
        }
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }
    public enum KEY_TYPE{
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
        serializer.startTag("", "cmd");
        serializer.text("=l");
        serializer.endTag("", "cmd");
        serializer.endDocument();

        return writer.toString();
    }

    public static String DAILY_REPARTI() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);


        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C501/$1");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    public static String DAILY_FINANZIARI() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);


        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C503/$1");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    public static String DAILY_IVA() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);


        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C508/$1");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    public static String DGFE_FROM_TO(Date d1, Date d2) throws IOException {

        DateFormat df = new SimpleDateFormat("ddMMyy", Locale.getDefault());
        String date1 = df.format(d1);
        String date2 = df.format(d2);

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C451/$1/&"+date1+"/["+date2);
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    public static String CORRISPETTIVI_FROM_TO(Date d1) throws IOException {

        DateFormat df = new SimpleDateFormat("ddMMyy", Locale.getDefault());
        String date1 = df.format(d1);

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C403/&"+date1+"/["+date1);
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    public static String PENDING_FILES() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("<</?i/*5");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    public static String SEND_PENDING_FILE() throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=C422");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    public static String PRINT_FATTURA(List<IsiCashElementBill> products, String paymentType, String recoverCode, SearchCustomerResponse customer, IsiCashBill bill) throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");

        serializer.startTag("", "cmd");
        serializer.text("=A/$1/("+customer.getDescription()+")");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=A/$2/("+customer.getAddress()+")");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=A/$3/("+customer.getCity()+")");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=A/$4/("+customer.getTaxCode()+")");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=A/$5/("+customer.getPec()+")");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=F/*4");
        serializer.endTag("", "cmd");

        for (IsiCashElementBill billProduct : products){
            if(billProduct.price != 0){
                serializer.startTag("", "cmd");

                String price = String.format(Locale.getDefault(), "%.0f", billProduct.price*100);

                serializer.text("=R" + billProduct.department + "/$" + price  +"/*" + billProduct.quantity + "/(" + billProduct.name + ")");
                serializer.endTag("", "cmd");

                if(billProduct.discount_valor != 0){
                    serializer.startTag("", "cmd");
                    switch (billProduct.discount_type){
                        case 1:
                            serializer.text("=%/*" +  billProduct.discount_valor);
                            break;
                        case 0:
                            serializer.text("=V/*" + (int)(billProduct.discount_valor*100));
                            break;
                        default: break;
                    }
                    serializer.endTag("", "cmd");
                }
            }
        }

        if(bill.discount_valor != 0){

            if(bill.discount_type == 1){
                serializer.startTag("", "cmd");
                serializer.text(String.format(Locale.getDefault(), "=%%/*%.2f", bill.discount_valor));
                serializer.endTag("", "cmd");
            }else{
                serializer.startTag("", "cmd");
                serializer.text(String.format(Locale.getDefault(), "=V/*%d", (int)(bill.discount_valor * 100)));
                serializer.endTag("", "cmd");
            }

        }

        serializer.startTag("", "cmd");
        serializer.text("=" + paymentType);
        serializer.endTag("", "cmd");

        serializer.startTag("", "cmd");
        serializer.text("=c");
        serializer.endTag("", "cmd");

        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();

    }

    public static String PRINT_REPORT(String date, float total, ArrayList<ElementBillQuantity> elements, ArrayList<PaymentType> paymentTypes, ArrayList<RepartoQuantity> repartoQuantities, ArrayList<CategoryQuantity> repartoCategories) throws IOException {

        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("=o");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=\"/()");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=\"/(Report del "+ date +")");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=\"/()");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=\"/(Dettaglio)");
        serializer.endTag("", "cmd");
        for (ElementBillQuantity element: elements) {
            serializer.startTag("", "cmd");
            serializer.text("=\"/("+String.format(Locale.getDefault(), "%d X %s   %.2f", element.getQuantity(), element.getElementBill().name, element.getPrice())+")");
            serializer.endTag("", "cmd");
        }

        serializer.startTag("", "cmd");
        serializer.text("=\"/()");
        serializer.endTag("", "cmd");

        serializer.startTag("", "cmd");
        serializer.text("=\"/(Categorie)");
        serializer.endTag("", "cmd");
        for (CategoryQuantity element: repartoCategories) {
            serializer.startTag("", "cmd");
            serializer.text("=\"/("+String.format(Locale.getDefault(), "%s   %.2f", element.getCategory_id().name, element.getTotal())+")");
            serializer.endTag("", "cmd");
        }

        serializer.startTag("", "cmd");
        serializer.text("=\"/()");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=\"/(Tipo pagamento)");
        serializer.endTag("", "cmd");

        for (PaymentType quantity : paymentTypes){

            String paymentTypeIn;

            switch (quantity.paymentType){
                case "T1":
                    paymentTypeIn = "CONTANTI";
                    break;
                case "T2":
                    paymentTypeIn = "NON RISCOSSO";
                    break;
                case "T3":
                    paymentTypeIn = "ASSEGNO";
                    break;
                case "T4":
                    paymentTypeIn = "POS";
                    break;
                case "T5":
                    paymentTypeIn = "NON STAMPATO";
                    break;
                case "T6":
                    paymentTypeIn = "NON FISCALE";
                    break;
                default:
                    paymentTypeIn = "NON DEFINITO";
            }

            serializer.startTag("", "cmd");
            serializer.text("=\"/("+String.format(Locale.getDefault(), "%s %.2f", paymentTypeIn, quantity.quantity)+")");
            serializer.endTag("", "cmd");
        }

        serializer.startTag("", "cmd");
        serializer.text("=\"/()");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=\"/(Recap reparti)");
        serializer.endTag("", "cmd");

        for (RepartoQuantity quantity : repartoQuantities){
            serializer.startTag("", "cmd");
            serializer.text("=\"/("+String.format(Locale.getDefault(), "Reparto %d   %.2f", quantity.reparto, quantity.quantity)+")");
            serializer.endTag("", "cmd");
        }

        serializer.startTag("", "cmd");
        serializer.text("=\"/()");
        serializer.endTag("", "cmd");
        serializer.startTag("", "cmd");
        serializer.text("=\"/(Totale: "+String.format(Locale.getDefault(), "%.2f", total)+")");
        serializer.endTag("", "cmd");

        serializer.startTag("", "cmd");
        serializer.text("=o");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

    public static String MATRICOLA() throws IOException{
        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Service");
        serializer.startTag("", "cmd");
        serializer.text("<</?m");
        serializer.endTag("", "cmd");
        serializer.endTag("", "Service");
        serializer.endDocument();

        return writer.toString();
    }

}
