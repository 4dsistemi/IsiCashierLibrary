package com.isi.isicashierlibrary;

public class IsiCashierResponse {

    public ISICASHIER_EXIT returnCode;
    public boolean error = false;
    public float total = 0;
    public float discount = 0;
    public String ctzonCard;

    public IsiCashierResponse() {
    }
}

