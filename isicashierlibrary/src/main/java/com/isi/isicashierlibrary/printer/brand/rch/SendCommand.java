package com.isi.isicashierlibrary.printer.brand.rch;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.isi.isicashierlibrary.printer.brand.ERROR_TYPE;
import com.isi.isicashierlibrary.printer.classes.ReceiptReturn;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class SendCommand {

    private ERROR_TYPE ok = ERROR_TYPE.NESSUN_ERRORE;
    private int returnInt = -1;
    private String returned = null;

    public ERROR_TYPE sendCommand(final String xml, final Context c){

        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {

            try{
                MakeHTTPPostXML post = new MakeHTTPPostXML(xml, c);
                String result = post.post();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(new StringReader(result));
                parser.nextTag();

                ArrayList<Tag> tags = readXML(parser);


                for (Tag tag : tags){

                    switch (tag.name) {
                        case "errorCode":
                            if (!tag.text.equals("0")) {

                                ok = ERROR_TYPE.CODICE_ERRORE;

                            }
                            break;

                        case "printerError":
                            if (tag.text.equals("1")) {

                                ok = ERROR_TYPE.ERRORE_STAMPANTE;

                            }
                            break;
                        case "paperEnd":
                            if (tag.text.equals("1")) {

                                ok = ERROR_TYPE.ERRORE_CARTA;

                            }
                            break;
                        case "coverOpen":
                            if (tag.text.equals("1")) {

                                ok = ERROR_TYPE.ERRORE_COVER;

                            }
                            break;
                    }

                }

                latch.countDown();


            }catch (Exception e){
                ok = ERROR_TYPE.ERRORE_CONNESSIONE;
                latch.countDown();
            }


        }).start();

        try {
            latch.await();
            return ok;
        } catch (InterruptedException e) {
            return ERROR_TYPE.ERRORE_CONNESSIONE;
        }


    }

    public ReceiptReturn sendCommandReceipt(final String xml, final Context c){

        final CountDownLatch latch = new CountDownLatch(1);

        ReceiptReturn receiptReturn = new ReceiptReturn(null, 0, 0);

        new Thread(() -> {

            try{
                MakeHTTPPostXML post = new MakeHTTPPostXML(xml, c);
                String result = post.post();

                Log.e("TAG", "sendCommandReceipt: " + result);

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(new StringReader(result));
                parser.nextTag();

                ArrayList<Tag> tags = readXML(parser);


                for (Tag tag : tags){

                    switch (tag.name) {
                        case "errorCode":
                            if (!tag.text.equals("0")) {

                                ok = ERROR_TYPE.CODICE_ERRORE;

                            }
                            break;

                        case "printerError":
                            if (tag.text.equals("1")) {

                                ok = ERROR_TYPE.ERRORE_STAMPANTE;

                            }
                            break;
                        case "paperEnd":
                            if (tag.text.equals("1")) {

                                ok = ERROR_TYPE.ERRORE_CARTA;

                            }
                            break;
                        case "coverOpen":
                            if (tag.text.equals("1")) {

                                ok = ERROR_TYPE.ERRORE_COVER;

                            }
                            break;
                        case "lastZ":
                            receiptReturn.setClosureNumber(Integer.parseInt(tag.text) + 1);
                            break;
                        case "lastDocF":
                            receiptReturn.setDocumentNumber(Integer.parseInt(tag.text));
                            break;
                    }

                }

                if(receiptReturn.getClosureNumber() == 0 || receiptReturn.getDocumentNumber() == 0){
                    ok = ERROR_TYPE.ERRORE_CHIUSURA;
                }

                latch.countDown();


            }catch (Exception e){
                ok = ERROR_TYPE.ERRORE_CONNESSIONE;
                latch.countDown();
            }


        }).start();

        try {
            latch.await();
            receiptReturn.setError_type(ok);
            return receiptReturn;
        } catch (InterruptedException e) {
            receiptReturn.setError_type(ERROR_TYPE.ERRORE_CONNESSIONE);
            return receiptReturn;
        }


    }

    public int sendCommandWithENQ(final String xml, final Context c){

        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {

            try{
                MakeHTTPPostXML post = new MakeHTTPPostXML(xml, c);
                String result = post.post();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(new StringReader(result));
                parser.nextTag();

                ArrayList<Tag> tags = readXML(parser);


                for (Tag tag : tags){

                    switch (tag.name) {
                        case "value":
                            returnInt = Character.getNumericValue(tag.text.toCharArray()[3]);
                            break;
                    }

                }

                latch.countDown();


            }catch (Exception e){
                latch.countDown();
            }


        }).start();

        try {
            latch.await();
            return returnInt;
        } catch (InterruptedException e) {
            return returnInt;
        }


    }

    public ERROR_TYPE sendCommand(final String xml, final Context c, final String ip){

        int numberOfCommand = StringUtils.countMatches(xml, "<cmd>");
        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {

            try{
                MakeHTTPPostXML post = new MakeHTTPPostXML(xml, c, ip);
                String result = post.post();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(new StringReader(result));
                parser.nextTag();

                ArrayList<Tag> tags = readXML(parser);

                for (Tag tag : tags){

                    switch (tag.name) {
                        case "errorCode":
                            if (!tag.text.equals("0")) {

                                ok = ERROR_TYPE.CODICE_ERRORE;

                            }
                            break;


                        case "printerError":
                            if (tag.text.equals("1")) {

                                ok = ERROR_TYPE.ERRORE_STAMPANTE;

                            }
                            break;
                        case "paperEnd":
                            if (tag.text.equals("1")) {

                                ok = ERROR_TYPE.ERRORE_CARTA;

                            }
                            break;
                        case "coverOpen":
                            if (tag.text.equals("1")) {

                                ok = ERROR_TYPE.ERRORE_COVER;

                            }
                            break;
                    }

                }

                latch.countDown();


            }catch (Exception e){
                ok = ERROR_TYPE.ERRORE_CONNESSIONE;
                latch.countDown();
            }


        }).start();

        try {
            latch.await();
            return ok;
        } catch (InterruptedException e) {
            return ERROR_TYPE.ERRORE_CONNESSIONE;
        }


    }

    public String sendCommandReturn(final String xml, final Context c, final String ip, final int timeout){

        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {

            try{
                MakeHTTPPostXML post = new MakeHTTPPostXML(xml, c, ip, timeout);
                String result = post.post();

                XmlToJson xmlToJson = new XmlToJson.Builder(result).build();
                JSONObject jsonObject = xmlToJson.toJson();

                JSONObject request = jsonObject.getJSONObject("Service").getJSONObject("Request");
                JSONObject enq = jsonObject.getJSONObject("Service").getJSONObject("Enq");

                if(!request.getString("errorCode").equals("0")){
                    ok = ERROR_TYPE.ERRORE_STAMPANTE;
                    return;
                }

                if(!request.getString("coverOpen").equals("0")){
                    ok = ERROR_TYPE.ERRORE_COVER;
                    return;
                }

                if(!request.getString("paperEnd").equals("0")){
                    ok = ERROR_TYPE.ERRORE_CARTA;
                    return;
                }

                if(!request.getString("errorCode").equals("0")){
                    ok = ERROR_TYPE.ERRORE_STAMPANTE;
                    return;
                }

                returned = enq.getString("value");

                latch.countDown();


            }catch (Exception e){
                ok = ERROR_TYPE.ERRORE_CONNESSIONE;
                latch.countDown();
            }


        }).start();

        try {
            latch.await();
            if(returned != null)
                return returned;
            return ok.toString();
        } catch (InterruptedException e) {
            return ERROR_TYPE.ERRORE_CONNESSIONE.toString();
        }


    }

    private ArrayList<Tag> readXML(XmlPullParser parser) throws XmlPullParserException, IOException {

        ArrayList<Tag> tags = new ArrayList<>();

        //ottengo il primo evento, ovvero la radice del documento xml
        int eventType = parser.getEventType();

        // in base al tipo di evento, aggiorno l'output
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                tags.add(new Tag(parser.getName()));
            }else if(eventType == XmlPullParser.TEXT) {
                if(parser.getText().trim().length() > 0 && parser.getText() != null){
                    tags.get(tags.size() -1).setText(parser.getText());
                }

            }
            eventType = parser.next();
        }
        return tags;
    }

    private class Tag{

        String name;
        String text;

        Tag(String name) {
            this.name = name;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}
