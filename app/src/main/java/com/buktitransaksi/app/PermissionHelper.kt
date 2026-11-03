package com.buktitransaksi.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun PermissionExplanationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Tidak bisa dismiss dengan tap luar */ },
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Lanjut")
            }
        }
    )
}

object PermissionHelper {
    fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }

    fun requestBatteryOptimizationExemption(activity: Activity) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${activity.packageName}")
        }
        activity.startActivity(intent)
    }
}
