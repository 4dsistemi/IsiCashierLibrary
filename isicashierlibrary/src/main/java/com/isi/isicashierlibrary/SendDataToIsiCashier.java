package com.isi.isicashierlibrary;

import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.isi.isiapi.classes.isicash.IsiCashBillAndElements;

public abstract class SendDataToIsiCashier {

    private final ActivityResultLauncher<Intent> someActivityResultLauncher;

    public abstract void operationAfterResultOk();
    public abstract void operationAfterResultError();
    public abstract void operationAfterResultCancel();

    public SendDataToIsiCashier(ComponentActivity activity){

        someActivityResultLauncher = activity.registerForActivityResult(
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
    }

    public SendDataToIsiCashier(Fragment fragment){

        someActivityResultLauncher = fragment.registerForActivityResult(
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
    }

    public void sendBill(IsiCashBillAndElements bill){

        Intent myIntent = new Intent();
        myIntent.setClassName("com.isi.isicashier", "com.isi.isicashier.MainActivity");
        Gson gson = new Gson();
        String jsonString = gson.toJson(bill);
        myIntent.putExtra("data", jsonString);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        someActivityResultLauncher.launch(myIntent);

    }
}
