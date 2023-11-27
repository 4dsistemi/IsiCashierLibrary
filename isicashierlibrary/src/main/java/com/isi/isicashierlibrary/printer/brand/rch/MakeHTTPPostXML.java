package com.isi.isicashierlibrary.printer.brand.rch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.isi.isiapi.classes.FiscalPrinter;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MakeHTTPPostXML{

    private final String xml;
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

    protected String post() {

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

        try {

            final MediaType JSON
                    = MediaType.get("application/xml; charset=utf-8");

            OkHttpClient client = new OkHttpClient.Builder().
                    writeTimeout(5, TimeUnit.SECONDS).
                    readTimeout(15, TimeUnit.SECONDS).
                    connectTimeout(timeout, TimeUnit.SECONDS).build();

            RequestBody body = RequestBody.create(xml, JSON);
            Request request = new Request.Builder()
                    .url("http://"+ ipMachine + "/service.cgi")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            return response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
            //exception = e;
            return "";
        }
    }
}
