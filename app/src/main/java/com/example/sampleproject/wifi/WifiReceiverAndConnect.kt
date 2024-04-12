package com.example.sampleproject.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import kotlinx.coroutines.flow.MutableStateFlow

// private val wifiReceiver = WifiReceiverAndConnect()

/*    override fun onResume() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(wifiReceiver, filter)

        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiReceiver)
    }*/

class WifiReceiverAndConnect : BroadcastReceiver() {

    val isWifiEnabled = MutableStateFlow(false)

    val hasSomeConnecting = MutableStateFlow<String?>(null)

    override fun onReceive(context: Context?, intent: Intent?) {

        val wifiManager =
            context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (wifiManager.isWifiEnabled) {
            isWifiEnabled.value = true
        } else {
            isWifiEnabled.value = false
        }

        if (wifiManager.connectionInfo != null) {
            val currentConnectedSSID = wifiManager.connectionInfo.ssid
            if (currentConnectedSSID != null) {
                hasSomeConnecting.value = currentConnectedSSID
            }

        }
    }

}
