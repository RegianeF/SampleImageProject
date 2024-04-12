package com.example.sampleproject.wifi

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi


@RequiresApi(Build.VERSION_CODES.Q)
fun anotherteste(
    context: Context,
    ssid: String,
    password: String,
) {
    val suggestion = WifiNetworkSuggestion.Builder()
        .setSsid(ssid)
        .setWpa2Passphrase(password)
        .build()

    val wifiManager =
        (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)

    wifiManager.removeNetworkSuggestions(mutableListOf(suggestion))

    when (wifiManager.addNetworkSuggestions(mutableListOf(suggestion))) {

        WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS -> {
            // Toast.makeText(context, "Você está conectado em $ssid", Toast.LENGTH_SHORT).show()
            println("Cenoura : Success ")
        }

        WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE -> {
            println("Cenoura : Duplicate ")
        }

        else -> {
            println("Cenoura : Else ")
        }

    }
}

fun isConnectedTo(ssid: String, context: Context): Boolean {
    var retVal = false
    val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo = wifi.connectionInfo
    if (wifiInfo != null) {
        val currentConnectedSSID = wifiInfo.ssid
        if (currentConnectedSSID != null && ssid == currentConnectedSSID) {
            retVal = true
        }
    }
    return retVal
}
