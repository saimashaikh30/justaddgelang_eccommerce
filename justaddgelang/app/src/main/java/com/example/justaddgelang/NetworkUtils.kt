package com.example.justaddgelang

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

fun fetchDetailsByPinCode(pincode: String, onResult: (PostOffice?) -> Unit) {
    val url = "https://api.postalpincode.in/pincode/$pincode"
    val request = Request.Builder().url(url).build()
    val client = OkHttpClient()

    client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            e.printStackTrace()
            onResult(null)
        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
            response.body?.let {
                val json = it.string()
                val result = Gson().fromJson(json, Array<PinCodeResponse>::class.java)
                if (result.isNotEmpty() && result[0].Status == "Success") {
                    // Take the first PostOffice entry
                    val postOffice = result[0].PostOffice?.firstOrNull()
                    onResult(postOffice)
                } else {
                    onResult(null)
                }
            } ?: onResult(null)
        }
    })
}
