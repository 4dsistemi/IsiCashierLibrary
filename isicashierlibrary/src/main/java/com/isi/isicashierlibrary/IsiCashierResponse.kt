package com.isi.isicashierlibrary

import android.app.Activity
import android.content.Intent

class IsiCashierResponse {

    var returnCode: ISICASHIER_EXIT? = null
    var error = false
    private var total = 0f
    private var discount = 0f
    private var ctzonCard: String? = null

    fun getResponse(resultCode: Int, data: Intent?): IsiCashierResponse {
        val response = IsiCashierResponse()

        if (resultCode == Activity.RESULT_CANCELED) {

            response.returnCode = ISICASHIER_EXIT.CANCELED

        } else {

            response.returnCode = ISICASHIER_EXIT.OK

            val error = data!!.getBooleanExtra("error", true)
            val total = data.getFloatExtra("total", 0f)
            val discount = data.getFloatExtra("discount", 0f)
            val ctzonCard = data.getStringExtra("ctzonCard")

            response.total = total
            response.error = error
            response.discount = discount
            response.ctzonCard = ctzonCard
        }

        return response
    }
}