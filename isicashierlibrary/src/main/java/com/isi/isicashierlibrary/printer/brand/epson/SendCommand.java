package com.isi.isicashierlibrary.printer.brand.epson;

import android.content.Context;
import android.util.Log;
import android.util.Xml;


import com.isi.isicashierlibrary.printer.brand.ERROR_TYPE;
import com.isi.isicashierlibrary.printer.classes.ReceiptReturn;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.concurrent.CountDownLatch;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
                String result = post.execute().get();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(new StringReader(result));
                parser.nextTag();

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
            e.printStackTrace();
            return ERROR_TYPE.ERRORE_CONNESSIONE;
        }


    }

    public ReceiptReturn sendCommandReceipt(final String xml, final Context c){

        final CountDownLatch latch = new CountDownLatch(1);

        ReceiptReturn receiptReturn = new ReceiptReturn(null, 0, 0);

        new Thread(() -> {

            try{
                MakeHTTPPostXML post = new MakeHTTPPostXML(xml, c);
                String result = post.execute().get();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(result));
                Document doc = builder.parse(is);

                Element el = (Element)doc.getElementsByTagName("response").item(0);

                if(el.getAttribute("sucess").equals("true")){
                    ok = ERROR_TYPE.NESSUN_ERRORE;

                    Element info = (Element)el.getElementsByTagName("addInfo").item(0);

                    Log.e("TAG", "sendCommandReceipt: " + info.toString());

                    receiptReturn.setClosureNumber(Integer.parseInt(info.getElementsByTagName("zRepNumber").item(0).getNodeValue()));
                    receiptReturn.setDocumentNumber(Integer.parseInt(info.getElementsByTagName("fiscalReceiptNumber").item(0).getNodeValue()));
                }else{
                    ok = ERROR_TYPE.ERRORE_CONNESSIONE;
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
                String result = post.execute().get();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(new StringReader(result));
                parser.nextTag();


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
                String result = post.execute().get();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(new StringReader(result));
                parser.nextTag();

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
                String result = post.execute().get();

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

}
