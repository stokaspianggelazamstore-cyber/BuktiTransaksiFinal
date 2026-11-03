package com.buktitransaksi.app

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object TelegramHelper {
    private const val TAG = "TelegramHelper"
    private val client = OkHttpClient()

    fun sendMessage(message: String, onSuccess: (() -> Unit)? = null, onError: ((Exception) -> Unit)? = null) {
        val url = "https://api.telegram.org/bot${Constants.BOT_TOKEN}/sendMessage"
        
        val json = """
            {
              "chat_id": "${Constants.CHAT_ID}",
              "text": ${org.json.JSONObject.quote(message)},
              "parse_mode": "HTML"
            }
        """.trimIndent()

        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Gagal mengirim pesan ke Telegram", e)
                onError?.invoke(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        Log.d(TAG, "Berhasil mengirim pesan ke Telegram")
                        onSuccess?.invoke()
                    } else {
                        val err = IOException("Gagal HTTP: ${it.code} - ${it.body?.string()}")
                        Log.e(TAG, err.message ?: "Unknown error")
                        onError?.invoke(err)
                    }
                }
            }
        })
    }
}
