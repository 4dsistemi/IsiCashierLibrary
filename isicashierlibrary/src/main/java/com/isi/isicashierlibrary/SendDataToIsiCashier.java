package com.isi.isicashierlibrary;

import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.isi.isiapi.classes.isicash.IsiCashBillAndElements;

public abstract class SendDataToIsiCashier {

    public abstract void operationAfterResultOk();
    public abstract void operationAfterResultError();
    public abstract void operationAfterResultCancel();

    public void sendBill(IsiCashBillAndElements bill, ComponentActivity activity){

        Intent myIntent = new Intent();
        myIntent.setClassName("com.isi.isicashier", "com.isi.isicashier.MainActivity");
        Gson gson = new Gson();
        String jsonString = gson.toJson(bill);
        myIntent.putExtra("data", jsonString);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        ActivityResultLauncher<Intent> someActivityResultLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getData() != null){
                        Intent data = result.getData();

                        IsiCashierResponse response = new IsiCashierResponse().getResponse(result.getResultCode(), data);

                        if(response.error){
                            operationAfterResultError();
                        }else{

                            if(response.returnCode == ISICASHIER_EXIT.OK){
                                operationAfterResultOk();
                            }else if(response.returnCode == ISICASHIER_EXIT.CANCELED){
                                operationAfterResultCancel();
                            }

                        }
                    }
                });

        someActivityResultLauncher.launch(myIntent);

    }

    public void sendBill(IsiCashBillAndElements bill, Fragment activity){

        Intent myIntent = new Intent();
        myIntent.setClassName("com.isi.isicashier", "com.isi.isicashier.MainActivity");
        Gson gson = new Gson();
        String jsonString = gson.toJson(bill);
        myIntent.putExtra("data", jsonString);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityResultLauncher<Intent> someActivityResultLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getData() != null){
                        Intent data = result.getData();

                        IsiCashierResponse response = new IsiCashierResponse().getResponse(result.getResultCode(), data);

                        if(response.error){
                            operationAfterResultError();
                        }else{

                            if(response.returnCode == ISICASHIER_EXIT.OK){
                                operationAfterResultOk();
                            }else if(response.returnCode == ISICASHIER_EXIT.CANCELED){
                                operationAfterResultCancel();
                            }

                        }
                    }
                });

        someActivityResultLauncher.launch(myIntent);

    }
}
