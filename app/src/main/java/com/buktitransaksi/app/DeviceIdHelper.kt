package com.buktitransaksi.app

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

object DeviceIdHelper {
    private const val TAG = "DeviceIdHelper"
    private const val PREFS_NAME = "bukti_transaksi_prefs"
    private const val KEY_DEVICE_ID = "device_id"
    private const val KEY_REGISTERED = "device_registered"

    fun getDeviceId(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)
        
        if (deviceId.isNullOrEmpty()) {
            val randomNum = Random().nextInt(10000)
            deviceId = String.format(Locale.US, "RZ%04d", randomNum)
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
            Log.d(TAG, "Device ID baru digenerate: $deviceId")
        }
        return deviceId!!
    }

    suspend fun checkAndRegisterDevice(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isRegistered = prefs.getBoolean(KEY_REGISTERED, false)
        val deviceId = getDeviceId(context)

        if (!isRegistered) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())

            val message = "🆕 Device Baru Terdaftar\n\nDevice : $deviceId\n\nWaktu : $timestamp"

            withContext(Dispatchers.IO) {
                try {
                    TelegramHelper.sendMessage(
                        message = message,
                        onSuccess = {
                            prefs.edit().putBoolean(KEY_REGISTERED, true).apply()
                            Log.d(TAG, "Pendaftaran device berhasil dikirim ke Telegram")
                        },
                        onError = { e ->
                            Log.e(TAG, "Gagal mengirim pendaftaran device", e)
                        }
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Exception saat registrasi device", e)
                }
            }
        }
    }
}
