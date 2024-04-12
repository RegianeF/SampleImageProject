package com.example.sampleproject.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast


class WifiReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                // Wi-Fi está conectado
                println("Wi-Fi conectado")
                Toast.makeText(context, "Wi-Fi conectado", Toast.LENGTH_SHORT).show()
            } else {
                println("Wi-Fi não conectado")
                // Wi-Fi não está conectado
                Toast.makeText(context, "Wi-Fi não conectado", Toast.LENGTH_SHORT).show()
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                // Wi-Fi está conectado
                Toast.makeText(context, "Wi-Fi conectado", Toast.LENGTH_SHORT).show()
            } else {
                // Wi-Fi não está conectado
                Toast.makeText(context, "Wi-Fi não conectado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
