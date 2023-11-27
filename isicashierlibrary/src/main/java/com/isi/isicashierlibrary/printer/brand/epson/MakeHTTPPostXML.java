package com.isi.isicashierlibrary.printer.brand.epson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.isi.isiapi.classes.FiscalPrinter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MakeHTTPPostXML extends AsyncTask<Void, Void, String> {

    private final String xml;
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private String ip;
    private int timeout = 10000;

    MakeHTTPPostXML(String xml, Context context) {

        this.xml = xml;
        this.context = context;

    }

    MakeHTTPPostXML(String xml, Context context, String ip) {

        this.xml = xml;
        this.context = context;
        this.ip = ip;

    }

    MakeHTTPPostXML(String xml, Context context, String ip, int timeout) {

        this.xml = xml;
        this.context = context;
        this.ip = ip;
        this.timeout = timeout;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String ipMachine = "";

        if(ip != null){
            ipMachine = ip;
        }else{
            try{
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                FiscalPrinter printerPref = new Gson().fromJson(preferences.getString("printer_pref", ""), FiscalPrinter.class);

                if(printerPref != null){
                    ipMachine = printerPref.ip;
                }
            }catch (Exception ignored){

            }
        }

        URL url;
        try {

            url = new URL("http://" + ipMachine + "/cgi-bin/fpmate.cgi?devid=local_printer&timeout=1000");
            Log.e("", "doInBackground: " + url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(timeout);
            urlConnection.setConnectTimeout(timeout);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Accept", "application/xml");
            urlConnection.setRequestProperty("Content-Type", "application/xml");
            urlConnection.setRequestProperty("Content-Lenght", Integer.toString(xml.length()));


            DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
            os.write(xml.getBytes(StandardCharsets.US_ASCII));
            os.close();

            int statusCode = urlConnection.getResponseCode();

            if(statusCode == 200){
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return "";
                }
                reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }


                if (buffer.length() == 0){
                    return "";
                }

                return buffer.toString();
            }else{
                return "";
            }


        } catch (IOException e) {
            e.printStackTrace();
            //exception = e;
            return "";
        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    //exception = e;
                    Log.e("test", "Error closing stream", e);
                }
            }
        }
    }
}
