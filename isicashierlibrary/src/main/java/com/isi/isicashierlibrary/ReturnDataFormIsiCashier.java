package com.isi.isicashierlibrary;

import android.app.Activity;
import android.content.Intent;

public class ReturnDataFormIsiCashier {

    public IsiCashierResponse getResponse(int resultCode, Intent data){

        IsiCashierResponse response = new IsiCashierResponse();

        if(resultCode == Activity.RESULT_CANCELED){
            response.returnCode = ISICASHIER_EXIT.CANCELED;
        }else{
            response.returnCode = ISICASHIER_EXIT.OK;

            boolean error = data.getBooleanExtra("error", true);
            float total = data.getFloatExtra("total", 0);
            float discount = data.getFloatExtra("discount", 0);
            String ctzonCard = data.getStringExtra("ctzonCard");

            response.total = total;
            response.error = error;
            response.discount = discount;
            response.ctzonCard = ctzonCard;
        }


        return response;

    }

}
