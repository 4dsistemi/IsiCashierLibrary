package com.isi.isicashierlibrary

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.isi.isiapi.classes.isicash.IsiCashBillAndElements

abstract class SendDataToIsiCashier {

    private val someActivityResultLauncher: ActivityResultLauncher<Intent>

    abstract fun operationAfterResultOk(response: IsiCashierResponse?)
    abstract fun operationAfterResultError()
    abstract fun operationAfterResultCancel()

    private val version: String

    constructor(activity: ComponentActivity, version: String) {
        this.version = version
        someActivityResultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.data != null) {
                val data = result.data
                val response = IsiCashierResponse().getResponse(result.resultCode, data)
                if (response.error) {
                    operationAfterResultError()
                } else {
                    if (response.returnCode == ISICASHIER_EXIT.OK) {
                        operationAfterResultOk(response)
                    } else if (response.returnCode == ISICASHIER_EXIT.CANCELED) {
                        operationAfterResultCancel()
                    }
                }
            }
        }
    }

    constructor(fragment: Fragment, version: String) {

        this.version = version

        someActivityResultLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.data != null) {

                val data = result.data
                val response = IsiCashierResponse().getResponse(result.resultCode, data)

                if (response.error) {

                    operationAfterResultError()

                } else {

                    if (response.returnCode == ISICASHIER_EXIT.OK) {

                        operationAfterResultOk(response)

                    } else if (response.returnCode == ISICASHIER_EXIT.CANCELED) {

                        operationAfterResultCancel()

                    }
                }
            }
        }
    }

    fun sendBill(bill: IsiCashBillAndElements?) {

        val myIntent = Intent()
        myIntent.setClassName("com.isi.isicashier$version", "com.isi.isicashier.MainActivity")

        val gson = Gson()
        val jsonString = gson.toJson(bill)

        myIntent.putExtra("data", jsonString)
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        someActivityResultLauncher.launch(myIntent)
    }
}